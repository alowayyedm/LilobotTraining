package com.bdi.agent.api;

import com.azure.core.annotation.Delete;
import com.bdi.agent.model.BeliefMap;
import com.bdi.agent.model.Knowledge;
import com.bdi.agent.model.PhaseConditions;
import com.bdi.agent.model.Scenario;
import com.bdi.agent.model.api.scenarios.Scenarios;
import com.bdi.agent.model.api.scenarios.actions.Action;
import com.bdi.agent.model.api.scenarios.beliefs.Belief;
import com.bdi.agent.model.api.scenarios.beliefs.GenericBelief;
import com.bdi.agent.model.api.scenarios.desires.Constraint;
import com.bdi.agent.model.api.scenarios.desires.Desire;
import com.bdi.agent.model.api.scenarios.intents.Intent;
import com.bdi.agent.model.api.scenarios.intents.ModifyIntent;
import com.bdi.agent.model.enums.BeliefUpdateType;
import com.bdi.agent.model.enums.BoundaryCheck;
import com.bdi.agent.model.util.BeliefConstraint;
import com.bdi.agent.service.ActionService;
import com.bdi.agent.service.AgentService;
import com.bdi.agent.service.KnowledgeService;
import com.bdi.agent.service.ScenarioService;
import com.bdi.agent.utils.DefaultScenarioFactory;
import java.io.Console;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import javax.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.HttpClientErrorException;

@CrossOrigin(origins = {"http://${server.web}"})
@RestController
@RequestMapping("scenarios")
public class ScenarioController {

    private final ScenarioService scenarioService;
    private final AgentService agentService;
    private final KnowledgeService knowledgeService;

    /**
     * Construct a new scenario controller.
     *
     * @param scenarioService The scenario service to use
     * @param agentService The agent service to use
     * @param knowledgeService The knowledge service to use
     */
    public ScenarioController(ScenarioService scenarioService, AgentService agentService,
                              KnowledgeService knowledgeService) {
        this.scenarioService = scenarioService;
        this.agentService = agentService;
        this.knowledgeService = knowledgeService;
        agentService.createDefaultScenarios();
        List<String> tempScenarios = scenarioService.getAllScenarios();
        tempScenarios.stream().forEach(s -> scenarioService.editScenario(scenarioService.getScenarioByNameFromJson(s)));
    }

    /**
     * Create a new scenario.
     *
     * @param scenario The name of the scenario
     * @return Empty
     */
    @PostMapping("/{scenario}")
    public ResponseEntity<Void> createScenario(@PathVariable String scenario) {
        if (scenarioService.scenarioExists(scenario)) {
            throw new HttpClientErrorException(HttpStatus.BAD_REQUEST, "a scenario with name "
                    + scenario + " already exists");
        }
        Scenario sc = new Scenario();
        sc.setName(scenario);
//        sc.setKnowledgeList(knowledgeService.getForScenario("knowledge_default.csv")
//                .stream().map(k -> new Knowledge(scenario, k.getType(), k.getSubject(), k.getAttribute())).toList());
        scenarioService.editScenario(sc);
        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    /**
     * Delete a scenario.
     *
     * @param scenario The scenario to delete
     * @return Empty
     */
    @DeleteMapping("/{scenario}")
    public ResponseEntity<Void> deleteScenario(@PathVariable String scenario) {
        boolean deleted = scenarioService.deleteScenario(scenario);
        if (!deleted) {
            throw new HttpClientErrorException(HttpStatus.NOT_FOUND, "could not find scenario: " + scenario);
        }
        return new ResponseEntity<>(HttpStatus.OK);
    }

    /**
     * endpoint to update responses given by the chatbot for specified intents.
     *
     * @param modifyIntentRequest request to change response values
     * @return the newly modified intents
     */
    @PutMapping("/{scenarios}/knowledge")
    public ResponseEntity<ModifyIntent> editExistingIntents(@PathVariable String scenarios,
                                                            @RequestBody ModifyIntent modifyIntentRequest) {
        Scenario scenario = scenarioService.getScenarioByName(scenarios);

        List<Knowledge> newIntents = new ArrayList<>();

        for (Intent intent : modifyIntentRequest.getIntents()) {
            Optional<Knowledge> k =
                    scenario.getKnowledgeList().stream().filter(i -> i.getKnowledge().equals(intent.getIntent()))
                            .findFirst();
            if (k.isEmpty()) {
                Knowledge newKnowledge = createNewKnowledge(scenarios, intent);
                newIntents.add(newKnowledge);
            } else {
                Knowledge kn = k.get();
                kn.setValues(intent.getValues());
                newIntents.add(kn);
            }
        }
        scenario.setKnowledgeList(newIntents);
        scenarioService.editScenario(scenario);
        return new ResponseEntity<>(modifyIntentRequest, HttpStatus.OK);
    }

    /**
     * endpoint to get all the intents for a scenario.
     *
     * @param scenarioName name of the knowledge file that the intents are requested for
     * @return array of all existing intents withing the context of the scenario
     */
    @GetMapping("/{scenarioName}/knowledge")
    public ResponseEntity<List<Intent>> getAllIntents(@PathVariable String scenarioName) {
        Scenario scenario = scenarioService.getScenarioByName(scenarioName);
        return new ResponseEntity<>(
                scenario.getKnowledgeList().stream()
                        .map(kn -> new Intent(kn.getIntentionName(), kn.getValues())).toList(),
                HttpStatus.OK);
    }

    /**
     * endpoint to create an intent.
     *
     * @param scenario name of the scenario.
     * @param intent   the intent to create.
     * @return the created intent.
     */
    @PostMapping("/{scenario}/knowledge")
    public ResponseEntity<Intent> createIntent(@PathVariable String scenario, @RequestBody Intent intent) {
        Scenario sc = scenarioService.getScenarioByName(scenario);
        if (sc.getKnowledgeList().stream().filter(k -> k.getIntentionName().equals(intent.getIntent())).findFirst()
                .isPresent()) {
            return new ResponseEntity<>(HttpStatus.CONFLICT);
        }
        Knowledge k = createNewKnowledge(scenario, intent);
        sc.getKnowledgeList().add(k);
        scenarioService.editScenario(sc);
        return new ResponseEntity<>(intent, HttpStatus.CREATED);

    }

    private Knowledge createNewKnowledge(String scenario, Intent intent) {
        Knowledge k = new Knowledge();
        k.setKnowledge(scenario);
        knowledgeService.addKnowledge(k, intent.getIntent());
        k.setValues(intent.getValues());
        return k;
    }

    /**
     * Delete an intent.
     *
     * @param scenarioName The name of the scenario
     * @param intentId     The name of the intent
     * @return Empty
     */
    @DeleteMapping("/{scenarioName}/knowledge/{intentId}")
    public ResponseEntity<Void> deleteIntent(@PathVariable String scenarioName, @PathVariable String intentId) {
        Scenario scenario = scenarioService.getScenarioByName(scenarioName);
        if (!scenario.getKnowledgeList().removeIf(k -> k.getIntentionName().equals(intentId))) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        scenarioService.editScenario(scenario);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    /**
     * Get endpoint for returning all existing scenario names.
     *
     * @return array of names of different scenario files
     */
    @GetMapping
    public ResponseEntity<Scenarios> getAllScenarios() {
        //        tempScenarios.forEach(scenario -> scenarioService.commitScenario(scenario));
        List<String> scenarios = scenarioService.getAllScenarios();
        //        List<String> allLoadedScenarios = new ArrayList<>();
        //        for (String x : scenarios) {
        //            try {
        //                scenarioService.getScenarioByNameFromJson(x);
        //                allLoadedScenarios.add(x);
        //            } catch (Exception e) {
        //                System.out.println("Warning: scenario " + x + " has failed to be deserialized");
        //            }
        //        }
        return new ResponseEntity<>(
                new Scenarios(scenarios),
                HttpStatus.OK);
    }

    /**
     * Get all beliefs of a scenario.
     *
     * @param scenario The scenario to get beliefs from
     * @return The beliefs
     */
    @GetMapping("/{scenario}/beliefs")
    public ResponseEntity<List<Belief>> getAllBeliefs(@PathVariable String scenario) {
        Scenario sc = scenarioService.getScenarioByName(scenario);
        return new ResponseEntity<>(sc.getBeliefs().stream().map(b -> new Belief(b.getId(), b.getFullName(),
                b.getValue())).toList(),
                HttpStatus.OK);
    }

    /**
     * Set all the beliefs for a scenario.
     *
     * @param scenario The scenario to set them for
     * @param beliefs  The list of beliefs
     * @return The list of beliefs
     */
    @PutMapping("/{scenario}/beliefs")
    public ResponseEntity<List<Belief>> editBeliefs(@PathVariable String scenario, @RequestBody List<Belief> beliefs) {
        Scenario sc = scenarioService.getScenarioByName(scenario);
        for (Belief b : beliefs) {
            Optional<com.bdi.agent.model.Belief> be =
                    sc.getBeliefs().stream().filter(belief -> belief.getFullName().equals(b.getName())).findFirst();
            if (be.isEmpty()) {
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }
            com.bdi.agent.model.Belief belief = be.get();
            belief.setFullName(b.getName());
            belief.setValue(b.getValue());
        }
        scenarioService.editScenario(sc);
        return new ResponseEntity<>(beliefs, HttpStatus.OK);
    }

    /**
     * Create a new belief.
     *
     * @param scenario The scenario to create a new belief
     * @param belief   The new belief
     * @return The new belief
     */
    @PostMapping("/{scenario}/beliefs")
    public ResponseEntity<Belief> createBelief(@PathVariable String scenario, @RequestBody Belief belief) {
        Scenario sc = scenarioService.getScenarioByName(scenario);
        sc.getBeliefs().removeIf(c -> c.getFullName().equals(belief.getName()));
        com.bdi.agent.model.Belief b = new com.bdi.agent.model.Belief();
        b.setName(belief.getName());
        b.setFullName(belief.getName());
        b.setValue(belief.getValue());
        sc.getBeliefs().add(b);
        scenarioService.editScenario(sc);
        scenarioService.flushRepository();
        belief.setId(sc.getBeliefs().get(sc.getBeliefs().size() - 1).getId());

        return new ResponseEntity<>(belief, HttpStatus.CREATED);
    }

    /**
     * Remove a belief.
     *
     * @param scenario The scenario to delete it from.
     * @param belief   The belief id to delete
     * @return Empty
     */
    @DeleteMapping("/{scenario}/beliefs/{belief}")
    public ResponseEntity<Void> deleteBelief(@PathVariable String scenario, @PathVariable String belief) {
        Scenario s = scenarioService.getScenarioByName(scenario);
        if (!s.getBeliefs().removeIf(b -> b.getFullName().equals(belief))) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        scenarioService.editScenario(s);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    /**
     * Get all desires for a scenario.
     *
     * @param scenario The scenario to get desires for
     * @return The desires
     */
    @GetMapping("/{scenario}/desires")
    public ResponseEntity<List<Desire>> getDesires(@PathVariable String scenario) {
        Scenario sc = scenarioService.getScenarioByName(scenario);
        return new ResponseEntity<>(sc.getDesires().stream()
                .map(d -> {
                    List<Action> actions = new ArrayList<>();
                    if (d.getActions() != null) {
                        actions = d.getActions().stream().map(x -> new Action(x.getName())).toList();
                    }

                    return new Desire(
                            d.getId(),
                            d.getFullName(),
                            sc.getConditions()
                                    .stream()
                                    .filter(cond -> cond.getDesire().equals(d))
                                    .map(cond -> cond
                                            .getConditions()
                                            .stream()
                                            .map(c -> new Constraint(
                                                    c.getBelief().getFullName(),
                                                    c.getBoundaryCheck(),
                                                    c.getGoalValue()
                                            ))
                                            .toList())
                                    .toList(),
                            d.getPhase(),
                            actions);
                })
                .toList(), HttpStatus.OK);
    }

    /**
     * Set all desires for a scenario.
     *
     * @param scenario The scenario to set desires for
     * @param desires  The desires to set
     * @return The desires that have been set
     */
    @PutMapping("/{scenario}/desires")
    public ResponseEntity<List<Desire>> editDesires(@PathVariable String scenario, @RequestBody List<Desire> desires) {
        Scenario sc = scenarioService.getScenarioByName(scenario);
        Map<String, com.bdi.agent.model.Belief> beliefMap =
                sc.getBeliefs().stream().collect(Collectors.toMap(b -> b.getFullName(), b -> b));

        List<PhaseConditions> mutableConditions = new ArrayList<>(sc.getConditions());
        for (Desire d : desires) {
            Optional<com.bdi.agent.model.Desire> desire =
                    sc.getDesires().stream().filter(de -> de.getFullName().equals(d.getName())).findFirst();
            com.bdi.agent.model.Desire des;
            if (desire.isEmpty()) {
                des = new com.bdi.agent.model.Desire();
                des.setFullName(d.getName());
                des.setName(d.getName());
                des.setPhase(d.getPhase());
                des.setActions(new HashSet<>());
                sc.getDesires().add(des);
            } else {
                des = desire.get();
            }

            if (d.getActions() != null) {
                Map<String, com.bdi.agent.model.Action> actionMap = sc.getActions().stream()
                        .collect(Collectors.toMap(action -> action.getName(), action -> action));
                Set<com.bdi.agent.model.Action> actions =
                        new HashSet<>(d.getActions().stream().map(action -> actionMap.get(action.getName())).collect(
                                Collectors.toSet()));
                des.setActions(actions);
            }
            des.setName(d.getName());
            des.setPhase(d.getPhase());
            List<PhaseConditions> toBeDeleted =
                    mutableConditions.stream().filter(cond -> cond.getDesire().equals(des)).toList();
            mutableConditions.removeAll(toBeDeleted);
            List<PhaseConditions> phaseConditions = new ArrayList<>(d.getConstraints()
                    .stream()
                    .map(constraints -> constraints
                            .stream()
                            .map(singleContraint -> new BeliefConstraint(singleContraint.getBoundary(),
                                    beliefMap.get(singleContraint.getBelief()), singleContraint.getValue()))
                            .toList())
                    .map(beliefConstraints -> new PhaseConditions(des, beliefConstraints))
                    .toList());

            mutableConditions.addAll(phaseConditions);
        }
        sc.setConditions(mutableConditions);
        scenarioService.editScenario(sc);
        sc = scenarioService.getScenarioByName(sc.getName());

        return new ResponseEntity<>(desires, HttpStatus.OK);
    }

    /**
     * Create a new desire for a scenario.
     *
     * @param scenario The scenario to create it for
     * @param desire   The new desire
     * @return The new desire
     */
    @PostMapping("/{scenario}/desires")
    public ResponseEntity<Desire> createDesire(@PathVariable String scenario, @RequestBody Desire desire) {
        Scenario sc = scenarioService.getScenarioByName(scenario);
        if (sc.getDesires().stream().filter(x -> x.getFullName().equals(desire.getName())).findAny().isPresent()) {
            return new ResponseEntity<>(HttpStatus.OK);
        }
        com.bdi.agent.model.Desire d = new com.bdi.agent.model.Desire();
        d.setName(desire.getName());
        d.setFullName(desire.getName());
        sc.getDesires().add(d);
        sc.getConditions().addAll(desire.getConstraints().stream().map(c -> new PhaseConditions(d, c.stream()
                .map(con -> new BeliefConstraint(con.getBoundary(),
                        sc.getBeliefs().stream().filter(b -> b.getId().equals(con.getBelief())).findFirst()
                                .orElseThrow(() -> new HttpClientErrorException(HttpStatus.NOT_FOUND)), con.getValue()))
                .toList())).toList());
        scenarioService.editScenario(sc);
        scenarioService.flushRepository();
        desire.setId(sc.getDesires().get(sc.getDesires().size() - 1).getId());

        return new ResponseEntity<>(desire, HttpStatus.CREATED);
    }

    /**
     * modifies actions for the scenario.
     *
     * @param scenario scenario to add actions for
     * @param actions  list of actions to add
     * @return 200 OK, 404 if not found else 500
     */
    @PutMapping("/{scenario}/actions")
    public ResponseEntity<Void> postActions(@PathVariable String scenario,
                                            @RequestBody List<com.bdi.agent.model.api
                                                    .scenarios.actions.Action> actions) {
        Scenario sc = scenarioService.getScenarioByName(scenario);

        List<com.bdi.agent.model.Action> scenarioActions =
                new ArrayList<>(actions
                        .stream()
                        .map(newAction -> {
                            com.bdi.agent.model.Action action = new com.bdi.agent.model.Action();
                            action.setName(newAction.getName());
                            return action;
                        })
                        .toList());
        sc.setActions(scenarioActions);
        scenarioService.editScenario(sc);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    /**
     * Gets actions.
     *
     * @param scenario name of scenario.
     * @return response object which contains list of actions
     */
    @GetMapping("{scenario}/actions")
    public ResponseEntity<List<Action>> getActions(@PathVariable String scenario) {
        Scenario sc = scenarioService.getScenarioByName(scenario);
        return new ResponseEntity<>(sc.getActions().stream().map(action -> new Action(action.getName())).toList(),
                HttpStatus.OK);
    }

    /**
     * Remove a desire from a scenario.
     *
     * @param scenario The scenario to delete it from
     * @param desire   The id of the desire to delete
     * @return Empty
     */
    @DeleteMapping("/{scenario}/desires/{desire}")
    public ResponseEntity<Void> deleteDesire(@PathVariable String scenario, @PathVariable String desire) {
        Scenario s = scenarioService.getScenarioByName(scenario);
        if (!s.getDesires().removeIf(d -> d.getFullName().equals(desire))) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        s.getConditions().removeIf(c -> c.getDesire().getFullName().equals(desire));
        scenarioService.editScenario(s);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    /**
     * Get the action conditions for an intent.
     *
     * @param scenario The scenario to get them for
     * @param intent   The intent to get them for
     * @return The conditions
     */
    @GetMapping("/{scenario}/intents/{intent}/mapping/actions")
    public ResponseEntity<List<String>> getActionConditions(@PathVariable String scenario,
                                                            @PathVariable String intent) {
        Scenario s = scenarioService.getScenarioByName(scenario);
        return new ResponseEntity<>(s.getIntentionMapping()
                .computeIfAbsent(intent, a -> new BeliefMap()).getActionConditions()
                .stream().map(x -> x.getName()).collect(
                        Collectors.toList()), HttpStatus.OK);
    }

    /**
     * Set the action conditions for an intent.
     *
     * @param scenario The scenario to set them for
     * @param intent   The intent to set them for
     * @return The conditions
     */
    @PutMapping("/{scenario}/intents/{intent}/mapping/actions")
    public ResponseEntity<List<String>> setActionConditions(@PathVariable String scenario, @PathVariable String intent,
                                                            @RequestBody List<String> actions) {
        Scenario s = scenarioService.getScenarioByName(scenario);
        s.getIntentionMapping().computeIfAbsent(intent, a -> new BeliefMap())
                .setActionConditions(actions.stream().map(a -> s.getActions()
                        .stream().filter(act -> act.getName().equals(a)).findFirst()
                        .orElseThrow(() -> new HttpClientErrorException(HttpStatus.NOT_FOUND,
                                "Could not find action"))).toList());
        scenarioService.editScenario(s);
        return new ResponseEntity<>(actions, HttpStatus.OK);
    }

    /**
     * Get the conditions for an intent to update beliefs.
     *
     * @param scenario The scenario to get them for
     * @param intent   The intent to get them for
     * @return The conditions
     */
    @GetMapping("/{scenario}/intents/{intent}/mapping/conditions")
    public ResponseEntity<List<GenericBelief<BoundaryCheck>>> getConditions(@PathVariable String scenario,
                                                                            @PathVariable String intent) {
        Scenario s = scenarioService.getScenarioByName(scenario);
        if (!s.getIntentionMapping().containsKey(intent)) {
            return new ResponseEntity<>(new ArrayList<>(), HttpStatus.OK);
        }

        BeliefMap beliefMap = s.getIntentionMapping().get(intent);

        List<GenericBelief<BoundaryCheck>> mappings = beliefMap
                .getBeliefConditions()
                .keySet()
                .stream().map(belief -> {
                    Float value = beliefMap.getBeliefConditionValues().get(belief);
                    BoundaryCheck beliefUpdateType = beliefMap.getBeliefConditions().get(belief);
                    return new GenericBelief<BoundaryCheck>(belief.getName(), beliefUpdateType, value);
                })
                .toList();

        return new ResponseEntity<>(mappings, HttpStatus.OK);
    }

    /**
     * Set the conditions for an intent to update beliefs.
     *
     * @param scenario The scenario to set them for
     * @param intent   The intent to set them for
     * @return The conditions
     */
    @PutMapping("/{scenario}/intents/{intent}/mapping/conditions")
    public ResponseEntity<List<GenericBelief<BoundaryCheck>>> setConditions(@PathVariable String scenario,
                                                                            @PathVariable String intent,
                                                                            @RequestBody List<GenericBelief<
                                                                                    BoundaryCheck>> conditions) {
        Scenario s = scenarioService.getScenarioByName(scenario);
        Knowledge k = scenarioService.getKnowledge(s, intent);
        BeliefMap bm = s.getIntentionMapping().computeIfAbsent(intent, a -> new BeliefMap());
        bm.setBeliefConditions(conditions.stream()
                .collect(Collectors.toMap(
                        b -> s.getBeliefs().stream().filter(
                                        be -> be.getFullName().equals(b.getName())).findFirst()
                                .orElseThrow(() -> new HttpClientErrorException(HttpStatus.NOT_FOUND,
                                        "Could not find belief")),
                        GenericBelief<BoundaryCheck>::getType)));
        bm.setBeliefConditionValues(conditions.stream()
                .collect(Collectors.toMap(
                        b -> s.getBeliefs().stream().filter(
                                        be -> be.getFullName().equals(b.getName())).findFirst()
                                .orElseThrow(() -> new HttpClientErrorException(HttpStatus.NOT_FOUND,
                                        "Could not find belief")),
                        GenericBelief<BoundaryCheck>::getValue)));

        scenarioService.editScenario(s);

        return new ResponseEntity<>(conditions, HttpStatus.OK);
    }

    /**
     * Get the update values for an intent.
     *
     * @param scenario The scenario to get them for
     * @param intent   The intent to get them for
     * @return The update values
     */
    @GetMapping("/{scenario}/intents/{intent}/mapping")
    public ResponseEntity<List<GenericBelief<BeliefUpdateType>>> getMapping(@PathVariable String scenario,
                                                                            @PathVariable String intent) {
        Scenario s = scenarioService.getScenarioByName(scenario);
        if (!s.getIntentionMapping().containsKey(intent)) {
            return new ResponseEntity<>(new ArrayList<>(), HttpStatus.OK);
        }

        BeliefMap beliefMap = s.getIntentionMapping().get(intent);

        List<GenericBelief<BeliefUpdateType>> mappings = beliefMap
                .getBeliefMapping()
                .keySet()
                .stream().map(belief -> {
                    Float value = beliefMap.getBeliefMapping().get(belief);
                    BeliefUpdateType beliefUpdateType = beliefMap.getBeliefMod().get(belief);
                    return new GenericBelief<BeliefUpdateType>(belief.getFullName(), beliefUpdateType, value);
                })
                .toList();

        return new ResponseEntity<>(mappings, HttpStatus.OK);
    }

    /**
     * Set the update values for an intent.
     *
     * @param scenario The scenario to set them for
     * @param intent   The intent to set them for
     * @return The update values
     */
    @PutMapping("/{scenario}/intents/{intent}/mapping")
    public ResponseEntity<List<GenericBelief<BeliefUpdateType>>> setMapping(@PathVariable String scenario,
                                                                            @PathVariable String intent,
                                                                            @RequestBody List<GenericBelief<
                                                                                    BeliefUpdateType>> conditions) {
        Scenario s = scenarioService.getScenarioByName(scenario);

        BeliefMap bm = s.getIntentionMapping().computeIfAbsent(intent, a -> new BeliefMap());

        bm.setBeliefMod(conditions.stream()
                .collect(Collectors.toMap(
                        b -> s.getBeliefs().stream().filter(
                                        be -> be.getFullName().equals(b.getName())).findFirst()
                                .orElseThrow(() -> new HttpClientErrorException(HttpStatus.NOT_FOUND,
                                        "Could not find belief")),
                        GenericBelief<BeliefUpdateType>::getType)));
        bm.setBeliefMapping(conditions.stream()
                .collect(Collectors.toMap(
                        b -> s.getBeliefs().stream().filter(
                                        be -> be.getFullName().equals(b.getName())).findFirst()
                                .orElseThrow(() -> new HttpClientErrorException(HttpStatus.NOT_FOUND,
                                        "Could not find belief")),
                        GenericBelief<BeliefUpdateType>::getValue)));

        scenarioService.editScenario(s);

        return new ResponseEntity<>(conditions, HttpStatus.OK);
    }

    /**
     * Delete an intent mapping.
     *
     * @param scenario The scenario to delete it for
     * @param intent The intent to delete it for
     * @return Empty response
     */
    @DeleteMapping("/{scenario}/intents/{intent}/mapping")
    public ResponseEntity<Void> deleteIntentMapping(@PathVariable String scenario, @PathVariable String intent) {
        Scenario s = scenarioService.getScenarioByName(scenario);
        if (s == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        if (!s.getIntentionMapping().containsKey(intent)) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        s.getIntentionMapping().remove(intent);
        scenarioService.editScenario(s);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    /**
     * read scenario from file and load it in the database.
     * all changes to the scenario will be made in the database till it is committed.
     * while editing the scenario should stay in the database
     *
     * @param scenarioName name of scenario to load
     * @return 200 if went well, 404 if not found
     */
    @PostMapping("/{scenarioName}/session")
    public ResponseEntity<Void> startScenarioSession(@PathVariable String scenarioName) {
        scenarioService.getScenarioByNameFromJson(scenarioName);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    /**
     * commit any changes made during editing process to file.
     *
     * @param scenario name of scenario to commit
     * @return 200 if went well, 404 if not found
     */
    @PostMapping("/{scenario}/commit")
    public ResponseEntity<Void> commitScenarioChanges(@PathVariable String scenario) {
        Scenario sc = scenarioService.getScenarioByName(scenario);
        scenarioService.commitScenario(sc);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    /**
     * Method to handle exception.
     *
     * @param e the exception.
     * @return the exception to be returned.
     */
    @ExceptionHandler({Exception.class})
    public ResponseEntity<String> handleException(Exception e) {
        if (e instanceof HttpClientErrorException) {
            return new ResponseEntity<>(e.getMessage(), ((HttpClientErrorException) e).getStatusCode());
        }
        return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
