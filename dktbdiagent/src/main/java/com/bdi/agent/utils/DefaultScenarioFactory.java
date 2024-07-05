package com.bdi.agent.utils;

import com.bdi.agent.model.Action;
import com.bdi.agent.model.Belief;
import com.bdi.agent.model.BeliefCondition;
import com.bdi.agent.model.BeliefMap;
import com.bdi.agent.model.Desire;
import com.bdi.agent.model.Knowledge;
import com.bdi.agent.model.PhaseConditions;
import com.bdi.agent.model.Scenario;
import com.bdi.agent.model.enums.BeliefName;
import com.bdi.agent.model.enums.BeliefUpdateType;
import com.bdi.agent.model.enums.BoundaryCheck;
import com.bdi.agent.model.util.BeliefConstraint;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@NoArgsConstructor
public class DefaultScenarioFactory {
    @NonNull
    private ValueConfiguration valueConfiguration;
    private Scenario scenario;

    private Set<Belief> beliefs;

    private List<Knowledge> knowledges;

    private Set<Desire> desires;

    @NonNull
    private Map<String, Belief> mapNameToBelief;

    @NonNull
    private Map<String, Action> mapNameToAction;

    @NonNull
    private Map<String, Desire> mapNameToDesire;

    public Scenario getDefaultScenario() {
        Scenario scenario = new Scenario();
        return scenario;
    }

    /**
     * Constructor for the class.
     *
     * @param valueConfiguration the value configuration.
     * @param beliefs            the set of beliefs.
     * @param desires            the set of desires.
     * @param knowledges         the set of intents.
     * @param actions            the list of actions.
     */
    public DefaultScenarioFactory(ValueConfiguration valueConfiguration,
                                  Set<Belief> beliefs,
                                  Set<Desire> desires,
                                  List<Knowledge> knowledges,
                                  List<Action> actions) {
        this.valueConfiguration = valueConfiguration;
        this.beliefs = beliefs;

        Map<String, Belief> beliefNameMap = new HashMap<>();
        for (Belief belief : beliefs) {
            beliefNameMap.put(belief.getName(), belief);
        }

        Map<String, Action> actionNameMap = new HashMap<>();
        actions.forEach(a -> actionNameMap.put(a.getName(), a));

        Map<String, Desire> desireMap = new HashMap<>();
        desires.forEach(d -> desireMap.put(d.getName(), d));

        this.mapNameToDesire = desireMap;

        this.mapNameToAction = actionNameMap;

        this.mapNameToBelief = beliefNameMap;

        this.desires = desires;
        this.knowledges = knowledges;
    }

    /**
     * Returns the default scenario (the original).
     *
     * @return The scenario
     */
    public Scenario createDefaultScenario(String scenarioName) {
        this.scenario = new Scenario();

        this.scenario.setName(scenarioName);
        this.scenario.setBeliefs(new ArrayList<>(this.beliefs.stream().toList()));
        this.scenario.setDesires(new ArrayList<>(this.desires.stream().toList()));
        this.scenario.setKnowledgeList(knowledges);
        this.scenario.setIntentionMapping(createDefaultBeliefs());
        this.scenario.setConditions(createDefaultPhaseConditions());
        this.scenario.setActions(new ArrayList<>(mapNameToAction.values().stream().toList()));
        //        this.scenario.setKnowledgeList(knowledgeService.getForScenario("knowledge_default"));
        //        this.scenario.setDesires(desireService.getByAgent(agent.getId()));

        return this.scenario;
    }

    //    public List<Knowledge> addBeliefMapsToKnowledge(List<Knowledge> knowledges, Map<String, BeliefMap> mapping) {
    //        List<Knowledge> updatedKnowledge = new ArrayList<>();
    //        for (Knowledge knowledge : knowledges) {
    //            String knowledgeName = knowledge.getIntentionName();
    //            knowledge.setBeliefMap(mapping.get(knowledgeName));
    //            updatedKnowledge.add(knowledge);
    //        }
    //        return updatedKnowledge;
    //    }

    /**
     * Creates default phase conditions.
     *
     * @return a list of phase conditions.
     */
    public List<PhaseConditions> createDefaultPhaseConditions() {
        PhaseConditions firstDesireCondition = new PhaseConditions();
        firstDesireCondition.setDesire(mapNameToDesire.get("D1"));

        firstDesireCondition.setConditions(new ArrayList<>(List.of(
                new BeliefConstraint(BoundaryCheck.EQ, mapNameToBelief.get(BeliefName.B10.toString()),
                        valueConfiguration.minValue),
                new BeliefConstraint(BoundaryCheck.EQ, mapNameToBelief.get(BeliefName.B12.toString()),
                        valueConfiguration.minValue),
                new BeliefConstraint(BoundaryCheck.LT, mapNameToBelief.get(BeliefName.B9.toString()),
                        valueConfiguration.maxThreshold),
                new BeliefConstraint(BoundaryCheck.GT, mapNameToBelief.get(BeliefName.B3.toString()),
                        valueConfiguration.minThreshold))));

        PhaseConditions secondDesireCondition = new PhaseConditions();
        secondDesireCondition.setDesire(mapNameToDesire.get("D2"));

        secondDesireCondition.setConditions(new ArrayList<>(List.of(
                new BeliefConstraint(BoundaryCheck.LT, mapNameToBelief.get(BeliefName.B1.toString()),
                        valueConfiguration.minThreshold),
                new BeliefConstraint(BoundaryCheck.EQ, mapNameToBelief.get(BeliefName.B17.toString()),
                        valueConfiguration.maxValue),
                new BeliefConstraint(BoundaryCheck.LT, mapNameToBelief.get(BeliefName.B3.toString()),
                        valueConfiguration.midThreshold),
                new BeliefConstraint(BoundaryCheck.EQ, mapNameToBelief.get(BeliefName.B12.toString()),
                        valueConfiguration.maxValue),
                new BeliefConstraint(BoundaryCheck.EQ, mapNameToBelief.get(BeliefName.B2.toString()),
                        valueConfiguration.maxValue))));

        PhaseConditions thirdDesireCondition = new PhaseConditions();
        thirdDesireCondition.setDesire(mapNameToDesire.get("D3"));

        thirdDesireCondition.setConditions(new ArrayList<>(List.of(
                new BeliefConstraint(BoundaryCheck.EQ, mapNameToBelief.get(BeliefName.B10.toString()),
                        valueConfiguration.maxValue),
                new BeliefConstraint(BoundaryCheck.GT, mapNameToBelief.get(BeliefName.B8.toString()),
                        valueConfiguration.maxThreshold),
                new BeliefConstraint(BoundaryCheck.LT, mapNameToBelief.get(BeliefName.B2.toString()),
                        valueConfiguration.maxThreshold))));

        PhaseConditions fourthDesireCondition = new PhaseConditions();
        fourthDesireCondition.setDesire(mapNameToDesire.get("D4"));

        fourthDesireCondition.setConditions(new ArrayList<>(List.of(
                new BeliefConstraint(BoundaryCheck.EQ, mapNameToBelief.get(BeliefName.B12.toString()),
                        valueConfiguration.maxValue),
                new BeliefConstraint(BoundaryCheck.GEQ, mapNameToBelief.get(BeliefName.B13.toString()),
                        valueConfiguration.midThreshold),
                new BeliefConstraint(BoundaryCheck.GEQ, mapNameToBelief.get(BeliefName.B3.toString()),
                        valueConfiguration.midThreshold))));

        PhaseConditions fifthDesireCondition = new PhaseConditions();
        fifthDesireCondition.setDesire(mapNameToDesire.get("D5"));

        fifthDesireCondition.setConditions(new ArrayList<>(List.of(
                new BeliefConstraint(BoundaryCheck.GEQ, mapNameToBelief.get(BeliefName.B4.toString()),
                        valueConfiguration.midThreshold),
                new BeliefConstraint(BoundaryCheck.EQ, mapNameToBelief.get(BeliefName.B10.toString()),
                        valueConfiguration.maxValue),
                new BeliefConstraint(BoundaryCheck.EQ, mapNameToBelief.get(BeliefName.B12.toString()),
                        valueConfiguration.minValue))));
        return new ArrayList<>(List.of(
                firstDesireCondition, secondDesireCondition,
                thirdDesireCondition, fourthDesireCondition, fifthDesireCondition));
    }

    /**
     * Creates a default set off values for the belief mapping,
     * This method could eventually be moved to read from a csv file if that is implemented.
     *
     * @return map from intention name to BeliefMap object
     */
    public Map<String, BeliefMap> createDefaultBeliefs() {
        Map<String, BeliefMap> intentionToBeliefMapping = new HashMap<>();
        // Example configuration for various perception names

        // request_chitchat_greeting and request_chitchat_faring
        BeliefMap chitchatGreeting = new BeliefMap();
        chitchatGreeting.setBeliefMod(new HashMap<>(Map.of(mapNameToBelief.get("B4"), BeliefUpdateType.INCREASE)));
        chitchatGreeting.setBeliefMapping(
                new HashMap<>(Map.of(mapNameToBelief.get("B4"), valueConfiguration.maxValue)));
        //        intentionToBeliefMapping.put("request_chitchat_greeting", chitchatGreeting);
        intentionToBeliefMapping.put("request_chitchat_faring", chitchatGreeting);

        // request_chitchat_goodbye
        BeliefMap chitchatGoodbye = new BeliefMap();
        chitchatGoodbye.setBeliefMod(new HashMap<>(Map.of(mapNameToBelief.get("B15"), BeliefUpdateType.SET_TO)));
        chitchatGoodbye.setBeliefMapping(
                new HashMap<>(Map.of(mapNameToBelief.get("B15"), valueConfiguration.maxValue)));
        intentionToBeliefMapping.put("request_chitchat_goodbye", chitchatGoodbye);


        // ack_contactingkt_compliment and inform_goal_help
        BeliefMap goalHelp = new BeliefMap();
        goalHelp.setBeliefMod(new HashMap<>(Map.of(mapNameToBelief.get("B7"), BeliefUpdateType.INCREASE)));
        goalHelp.setBeliefMapping(new HashMap<>(Map.of(mapNameToBelief.get("B7"), valueConfiguration.oneStep)));
        intentionToBeliefMapping.put("ack_contactingkt_compliment", goalHelp);
        //        intentionToBeliefMapping.put("inform_goal_help", goalHelp);

        // ack_bullying_empathize and ack_goal_empathize
        BeliefMap empathize = new BeliefMap();
        empathize.setBeliefMod(new HashMap<>(Map.of(mapNameToBelief.get("B5"), BeliefUpdateType.INCREASE)));
        empathize.setBeliefMapping(new HashMap<>(Map.of(mapNameToBelief.get("B5"), valueConfiguration.oneStep)));
        intentionToBeliefMapping.put("ack_bullying_empathize", empathize);
        //        intentionToBeliefMapping.put("ack_goal_empathize", empathize);

        // ack_goal_compliment and ack_confidant_compliment
        BeliefMap goalCompliment = new BeliefMap();
        goalCompliment.setBeliefMod(new HashMap<>(
                Map.of(mapNameToBelief.get("B1"), BeliefUpdateType.INCREASE, mapNameToBelief.get("B2"),
                        BeliefUpdateType.INCREASE)));
        goalCompliment.setBeliefMapping(new HashMap<>(
                Map.of(mapNameToBelief.get("B1"), valueConfiguration.oneStep, mapNameToBelief.get("B2"),
                        valueConfiguration.oneStep)));
        //        intentionToBeliefMapping.put("ack_goal_compliment", goalCompliment);
        intentionToBeliefMapping.put("ack_confidant_compliment", goalCompliment);

        // ack_unknown_compliment
        BeliefMap unknownCompliment = new BeliefMap();
        unknownCompliment.setBeliefMod(new HashMap<>(Map.of(mapNameToBelief.get("B2"), BeliefUpdateType.INCREASE)));
        unknownCompliment.setBeliefMapping(
                new HashMap<>(Map.of(mapNameToBelief.get("B2"), valueConfiguration.oneStep)));
        intentionToBeliefMapping.put("ack_unknown_compliment", unknownCompliment);

        // request_confidant_when, request_confidant_feeling, request_confidant_how, request_confidant_say
        BeliefMap confidantRequest = new BeliefMap();
        confidantRequest.setBeliefMod(new HashMap<>(Map.of(mapNameToBelief.get("B2"), BeliefUpdateType.INCREASE)));
        confidantRequest.setBeliefMapping(new HashMap<>(Map.of(mapNameToBelief.get("B2"), valueConfiguration.oneStep)));
        intentionToBeliefMapping.put("request_confidant_when", confidantRequest);
        //        intentionToBeliefMapping.put("request_confidant_feeling", confidantRequest);
        //        intentionToBeliefMapping.put("request_confidant_how", confidantRequest);
        //        intentionToBeliefMapping.put("request_confidant_say", confidantRequest);

        // request_goal_dream
        BeliefMap goalDream = new BeliefMap();
        goalDream.setBeliefMod(new HashMap<>(Map.of(mapNameToBelief.get("B11"), BeliefUpdateType.SET_TO)));
        goalDream.setBeliefMapping(new HashMap<>(Map.of(mapNameToBelief.get("B11"), valueConfiguration.maxValue)));
        intentionToBeliefMapping.put("request_goal_dream", goalDream);

        // request_goal_effect, request_goal_feeling, request_goal_howchild
        BeliefMap goalRequest = new BeliefMap();
        goalRequest.setBeliefMod(new HashMap<>(
                Map.of(mapNameToBelief.get("B1"), BeliefUpdateType.INCREASE, mapNameToBelief.get("B11"),
                        BeliefUpdateType.INCREASE)));
        goalRequest.setBeliefMapping(new HashMap<>(
                Map.of(mapNameToBelief.get("B1"), valueConfiguration.oneStep, mapNameToBelief.get("B11"),
                        valueConfiguration.oneStep)));
        intentionToBeliefMapping.put("request_goal_effect", goalRequest);
        //        intentionToBeliefMapping.put("request_goal_feeling", goalRequest);
        //        intentionToBeliefMapping.put("request_goal_howchild", goalRequest);

        // confirm_goal_summary
        BeliefMap goalSummary = new BeliefMap();
        goalSummary.setBeliefMod(new HashMap<>(Map.of(mapNameToBelief.get("B10"), BeliefUpdateType.SET_TO)));
        goalSummary.setBeliefMapping(new HashMap<>(Map.of(mapNameToBelief.get("B10"), valueConfiguration.minValue)));
        intentionToBeliefMapping.put("confirm_goal_summary", goalSummary);

        // confirm_goal_collaborate
        BeliefMap goalCollaborate = new BeliefMap();
        goalCollaborate.setBeliefMod(new HashMap<>(
                Map.of(mapNameToBelief.get("B7"), BeliefUpdateType.INCREASE, mapNameToBelief.get("B8"),
                        BeliefUpdateType.DECREASE, mapNameToBelief.get("B16"), BeliefUpdateType.SET_TO)));
        goalCollaborate.setBeliefMapping(new HashMap<>(
                Map.of(mapNameToBelief.get("B7"), valueConfiguration.oneStep, mapNameToBelief.get("B8"),
                        valueConfiguration.twoStep, mapNameToBelief.get("B16"), valueConfiguration.maxValue)));
        goalCollaborate.setBeliefConditions(new ArrayList<>(
                List.of(new BeliefCondition(mapNameToBelief.get("B4"), BoundaryCheck.GT,
                        valueConfiguration.midThreshold))));
        intentionToBeliefMapping.put("confirm_goal_collaborate", goalCollaborate);

        // inform_goal_negative
        BeliefMap goalNegative = new BeliefMap();
        goalNegative.setBeliefMod(new HashMap<>(
                Map.of(mapNameToBelief.get("B7"), BeliefUpdateType.DECREASE, mapNameToBelief.get("B8"),
                        BeliefUpdateType.SET_TO)));
        goalNegative.setBeliefMapping(new HashMap<>(
                Map.of(mapNameToBelief.get("B7"), valueConfiguration.oneStep, mapNameToBelief.get("B8"),
                        valueConfiguration.minValue)));
        goalNegative.setBeliefConditions(new ArrayList<>(
                List.of(new BeliefCondition(mapNameToBelief.get("B10"), BoundaryCheck.EQ,
                        valueConfiguration.maxValue))));
        intentionToBeliefMapping.put("inform_goal_negative", goalNegative);

        // inform_goal_positive
        BeliefMap goalPositive = new BeliefMap();
        goalPositive.setBeliefMod(new HashMap<>(Map.of(mapNameToBelief.get("B17"), BeliefUpdateType.SET_TO)));
        goalPositive.setBeliefMapping(new HashMap<>(Map.of(mapNameToBelief.get("B17"), valueConfiguration.maxValue)));
        goalPositive.setBeliefConditions(new ArrayList<>(
                List.of(new BeliefCondition(mapNameToBelief.get("B10"), BoundaryCheck.EQ,
                        valueConfiguration.maxValue))));
        intentionToBeliefMapping.put("inform_goal_positive", goalPositive);

        // request_confidant_who
        BeliefMap confidantWho = new BeliefMap();
        confidantWho.setBeliefMod(new HashMap<>(Map.of(mapNameToBelief.get("B12"), BeliefUpdateType.SET_TO)));
        confidantWho.setBeliefMapping(new HashMap<>(Map.of(mapNameToBelief.get("B12"), valueConfiguration.maxValue)));
        intentionToBeliefMapping.put("request_confidant_who", confidantWho);

        // inform_confidant_help and inform_confidant_say
        BeliefMap confidantInform = new BeliefMap();
        confidantInform.setBeliefMod(new HashMap<>(Map.of(mapNameToBelief.get("B13"), BeliefUpdateType.SET_TO)));
        confidantInform.setBeliefMapping(
                new HashMap<>(Map.of(mapNameToBelief.get("B13"), valueConfiguration.maxValue)));
        confidantInform.setActionConditions(
                new ArrayList<>(List.of(mapNameToAction.get("A6"), mapNameToAction.get("A7"))));
        intentionToBeliefMapping.put("inform_confidant_help", confidantInform);
        //        intentionToBeliefMapping.put("inform_confidant_say", confidantInform);

        // confirm_confidant_teacher
        BeliefMap confidantTeacher = new BeliefMap();
        confidantTeacher.setBeliefMod(new HashMap<>(
                Map.of(mapNameToBelief.get("B8"), BeliefUpdateType.SET_TO, mapNameToBelief.get("B16"),
                        BeliefUpdateType.SET_TO)));
        confidantTeacher.setBeliefMapping(new HashMap<>(
                Map.of(mapNameToBelief.get("B8"), valueConfiguration.minValue, mapNameToBelief.get("B16"),
                        valueConfiguration.maxValue)));
        confidantTeacher.setBeliefConditions(new ArrayList<>(
                List.of(new BeliefCondition(mapNameToBelief.get("B13"), BoundaryCheck.GT,
                        valueConfiguration.midThreshold))));
        intentionToBeliefMapping.put("confirm_confidant_teacher", confidantTeacher);

        // confirm_confidant_parent todo work on this one
        //        BeliefMap confidantParent = new BeliefMap();
        //        confidantParent.setBeliefMod(Map.of(
        //                mapNameToBelief.get("ack"), BeliefUpdateType.SET_TO
        //        ));
        //        confidantParent.setBeliefConditionValues(Map.of(
        //                mapNameToBelief.get("ack"), valueConfiguration.minValue
        //        ));
        //        intentionToBeliefMapping.put("confirm_confidant_parent", confidantParent);

        // confirm_confidant_summary and confirm_chitchat_satisfaction

        //todo work on this as well
        //        BeliefMap confidantSummary = new BeliefMap();
        //        confidantSummary.setBeliefMod(Map.of(
        //                mapNameToBelief.get("ack"), BeliefUpdateType.SET_TO
        //        ));
        //        confidantSummary.setBeliefConditionValues(Map.of(
        //                mapNameToBelief.get("ack"), valueConfiguration.minValue
        //        ));
        //        intentionToBeliefMapping.put("confirm_confidant_summary", confidantSummary);
        //        intentionToBeliefMapping.put("confirm_chitchat_satisfaction", confidantSummary);

        //        intentionToBeliefMapping
        //                .entrySet()
        //                .stream()
        //                .collect(Collectors.toMap(
        //                        entry -> new Intent(entry.getKey()), // transform key from String to Integer
        //                        Map.Entry::getValue
        //                ));

        return intentionToBeliefMapping;
        //        List<Pair<Belief, Float>> requestChitChatFaringMapping = new ArrayList<>();
        //        Map<Belief, BeliefUpdateType> requestChitChatFaringMod = new HashMap<>();
        //
        //        createBeliefMapping(requestChitChatFaringMapping, requestChitChatFaringMod,
        //        "B4", oneStep, BeliefUpdateType.INCREASE);
    }
}
