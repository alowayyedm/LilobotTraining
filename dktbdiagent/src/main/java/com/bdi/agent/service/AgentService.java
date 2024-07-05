package com.bdi.agent.service;

import com.bdi.agent.exceptions.SizeMismatchException;
import com.bdi.agent.model.Action;
import com.bdi.agent.model.Agent;
import com.bdi.agent.model.Belief;
import com.bdi.agent.model.BeliefMap;
import com.bdi.agent.model.Desire;
import com.bdi.agent.model.Knowledge;
import com.bdi.agent.model.Perception;
import com.bdi.agent.model.Scenario;
import com.bdi.agent.model.api.BeliefChangeClientModel;
import com.bdi.agent.model.api.BeliefChangeModel;
import com.bdi.agent.model.api.MessageModel;
import com.bdi.agent.model.api.PhaseChangeResponse;
import com.bdi.agent.model.enums.BeliefName;
import com.bdi.agent.model.enums.BeliefUpdateType;
import com.bdi.agent.model.enums.Phase;
import com.bdi.agent.model.util.BeliefUpdateLogEntry;
import com.bdi.agent.model.util.DesireUpdateLogEntry;
import com.bdi.agent.model.util.LogEntry;
import com.bdi.agent.model.util.MessageLogEntry;
import com.bdi.agent.model.util.PhaseTransitionConstraints;
import com.bdi.agent.repository.ActionRepository;
import com.bdi.agent.repository.AgentRepository;
import com.bdi.agent.repository.BeliefRepository;
import com.bdi.agent.repository.KnowledgeRepository;
import com.bdi.agent.utils.ConstraintProvider;
import com.bdi.agent.utils.FloatComparer;
import com.bdi.agent.utils.ValueConfiguration;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.locks.Lock;
import javax.persistence.EntityNotFoundException;
import javax.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

/**
 * author:  Sharon Afua Grundmann
 * as part of MSc Thesis "A BDI-based virtual agent for training child helpline counsellors"
 * This is the main class of the BDI application. Core functionalities are implemented here with the other services
 * handling the BDI data structures and database querying.
 */
@Service
@PropertySource("classpath:config.properties")
public class AgentService {

    private final AgentRepository agentRepository;

    private final KnowledgeRepository knowledgeRepository;

    private final BeliefService beliefService;
    private final DesireService desireService;
    private final ActionService actionService;
    private final ReportService reportService;
    private final LogEntryService logEntryService;
    private final KnowledgeService knowledgeService;
    private final FloatComparer floatComparer;
    private final ConstraintService constraintService;
    private final ConstraintProvider constraintProvider;
    private final SimpMessagingTemplate messagingTemplate;
    private final ValueConfiguration valueConfiguration;

    @Value("${relatednessBeliefs}")
    String[] relatednessBeliefs;    //for estimating value of B3

    private final ActionRepository actionRepository;
    private final BeliefRepository beliefRepository;
    private final ScenarioService scenarioService;
    @Value("${ktPrefix}")
    String ktPrefix;   //  for printing transcript
    @Value("${liloPrefix}")
    String liloPrefix;
    private Lock lock;

    /**
     * Constructor for the agent service. Injects the other services and the initialises the knowledge base
     * (agent responses).
     *
     * @param agentRepository    repository for agent data
     * @param beliefService      the belief service
     * @param desireService      the desire service
     * @param reportService      the report service
     * @param knowledgeService   the knowledge service
     * @param floatComparer      the float comparer to compare floats safely
     * @param constraintService  the constraint service used to check desire constraints
     * @param constraintProvider the constraint provider used to retrieve phase constraints
     * @param messagingTemplate  the template used for websocket communication
     */
    @Autowired
    public AgentService(AgentRepository agentRepository, BeliefService beliefService, DesireService desireService,
                        ActionService actionService, ReportService reportService, LogEntryService logEntryService,
                        KnowledgeService knowledgeService, FloatComparer floatComparer,
                        ConstraintService constraintService,
                        ConstraintProvider constraintProvider, SimpMessagingTemplate messagingTemplate,
                        KnowledgeRepository knowledgeRepository, ActionRepository actionRepository,
                        BeliefRepository beliefRepository, ScenarioService scenarioService,
                        ValueConfiguration valueConfiguration) {
        this.agentRepository = agentRepository;
        this.beliefService = beliefService;
        this.desireService = desireService;
        this.actionService = actionService;
        this.reportService = reportService;
        this.logEntryService = logEntryService;
        this.knowledgeService = knowledgeService;
        this.constraintService = constraintService;
        this.floatComparer = floatComparer;
        this.constraintProvider = constraintProvider;
        this.messagingTemplate = messagingTemplate;
        this.knowledgeRepository = knowledgeRepository;
        this.scenarioService = scenarioService;

        try {
            knowledgeService.initializeKnowledge();
        } catch (Exception e) {
            e.printStackTrace();
        }
        this.actionRepository = actionRepository;
        this.beliefRepository = beliefRepository;
        this.valueConfiguration = valueConfiguration;
    }

    /**
     * Given a list of belief update logs and an Optional belief update log, adds the log to the list if it is present.
     *
     * @param logs      the list of belief update logs
     * @param updateLog the optional log to add if present
     */
    private static void addIfPresent(List<BeliefUpdateLogEntry> logs, Optional<BeliefUpdateLogEntry> updateLog) {
        updateLog.ifPresent(logs::add);
    }

    /**
     * For each given BeliefUpdateLogEntry, sets the cause to the message within the given perception.
     *
     * @param beliefUpdateLogs the logs to set the causes of
     * @param cause            the cause of the belief update
     */
    public static void setLogCauses(List<BeliefUpdateLogEntry> beliefUpdateLogs, String cause) {
        for (BeliefUpdateLogEntry log : beliefUpdateLogs) {
            log.setCause(cause);
        }
    }

    /**
     * Sets the beliefs of an agent to be of a specified phase, if possible.
     *
     * @param sessionId The session id of the agent.
     * @param phase     The phase to set to.
     * @return A list of BeliefChangeModels, which contain the values and names of all beliefs which were set.
     * @throws Exception If the agent does not exist, the phase is null, or the belief value sizes are not matched.
     */
    public List<BeliefChangeModel> setAgentStateToPhase(String sessionId, Phase phase) throws Exception {
        if (!agentRepository.existsByUserId(sessionId)) {
            throw new EntityNotFoundException("Agent not found");
        }
        if (phase == null) {
            throw new NullPointerException("Phase cannot be null");
        }

        // Retrieve the constraints of the phase. These also contain an example belief value array
        // which sets the agents state to that phase
        PhaseTransitionConstraints constraints = constraintProvider.getPhaseTransitionConstraints(phase);

        Agent agent = agentRepository.getByUserId(sessionId);
        List<Belief> beliefs = beliefService.sortBeliefsByName(agent.getScenario().getBeliefs());

        // Check size constraint
        if (constraints.getExampleBeliefValues().length != beliefs.size()) {
            throw new SizeMismatchException("Belief set size " + beliefs.size() + " does not " + "match size "
                    + constraints.getExampleBeliefValues().length);
        }

        // Set belief values, this already updates desires
        List<BeliefChangeModel> beliefUpdates = new ArrayList<>();
        for (int i = 0; i < beliefs.size(); i++) {
            String name = beliefs.get(i).getName();
            float value = constraints.getExampleBeliefValues()[i];

            beliefService.setBeliefValue(agent, name, value);
            beliefUpdates.add(new BeliefChangeModel(name, value));
        }

        // Update desires and set/send phase
        updateDesires(agent);
        sendPhaseOfAgent(agent.getPhase(), updatePhaseOfAgent(agent), true, agent.getUserId());

        // If the agent was not active before, set it to be active again
        setAgentActive(sessionId, true);
        // Set all actions of the agent to be uncompleted
        actionService.setActionsUncompleted(agent.getScenario().getDesires());

        return beliefUpdates;
    }

    /**
     * Sets the current phase of an agent, considering the active goal.
     *
     * @param agent The agent.
     * @return The phase the agent is currently in.
     */
    public Phase updatePhaseOfAgent(Agent agent) {
        Desire activeDesire = desireService.getActiveGoal(agent.getScenario().getDesires());

        Phase setTo = Phase.PHASE1;

        if (activeDesire != null) {
            setTo = activeDesire.getPhase();
        }

        agent.setPhase(setTo);
        agentRepository.save(agent);
        return setTo;
    }

    public void save(Agent agent) {
        agentRepository.save(agent);
    }

    public boolean containsUserId(String userId) {
        return agentRepository.existsByUserId(userId);
    }

    public Agent getByUserId(String userId) {
        return agentRepository.getByUserId(userId);
    }

    private String resolvePerceptionSubject(Agent agent) throws NullPointerException {
        return agent.getCurrentSubject();
    }

    /**
     * Sets the subject of the perception to the current subject of the agent.
     *
     * @param agent      the agent
     * @param perception the perception of the agent
     */
    private void parsePerception(Agent agent, Perception perception) {
        try {
            perception.setSubject(resolvePerceptionSubject(agent));
        } catch (NullPointerException e) {
            System.err.println("parsePerception: cannot resolve perception subject");
            perception.setSubject("");
        }
    }

    /**
     * Gets a response from the database for an acknowledgement with attribute of the perception.
     *
     * @param perception the perception of the agent
     * @return the response from the knowledge base
     */
    private String respondToAck(Agent agent, Perception perception) {
        String type = "ack";
        String attribute = perception.getAttribute();

        return getResponseFromKnowledge(agent.getScenario(), type, attribute);
    }

    /**
     * Gets a response from the database with certain subject and attribute.
     *
     * @param subject   subject of the perception
     * @param attribute attribute of the perception
     * @return the response from the knowledge base
     */
    private String getResponseFromKnowledge(Scenario scenario, String subject, String attribute) {
        String err = "Ik begrijp niet wat je bedoelt";

        Optional<Knowledge> knowledgeOptional = scenario.getKnowledgeList().stream()
                .filter(knowledge -> knowledge.getSubject().equals(subject)
                        && knowledge.getAttribute().equals(attribute)).findFirst();
        if (knowledgeOptional.isEmpty()) {
            System.err.println("getResponseFromKnowledge: could not find knowledge");
            return err;
        }
        return knowledgeService.getResponse(knowledgeOptional.get());
        //        try {
        ////            Knowledge knowledge = knowledgeService
        //                  .getBySubjectAndAttribute(knowledgeName, subject, attribute);
        ////            return knowledgeService.getResponse(knowledge);
        //        } catch (NullPointerException e) {
        //            System.err.println("getResponseFromKnowledge: could not find knowledge");
        //        }
        //
        //        return err;
    }

    /**
     * Gets a response from the database corresponding to an uncompleted action related to a desire.
     *
     * @param agent  the agent
     * @param desire the desire of the agent
     * @return the response from the knowledge base
     */
    private String saySomething(Agent agent, Desire desire) {

        try {
            Action action = actionService.getUncompletedAction(desire);
            if (action != null) {
                agent.setCurrentAction(action.getId());

                if (action.getType().equals("inform")) {
                    action.setCompleted(true);
                }
                actionService.addAction(action);
            }

            return getResponseFromKnowledge(agent.getScenario(), action.getSubject(), action.getAttribute());

        } catch (NullPointerException e) {
            e.printStackTrace();
            System.err.println("saySomething: could not get action");
        }

        return null;
    }

    /**
     * Sets the perception type to "ack" and the attribute to "positive" or "negative" if the current value is above
     * or below the threshold.
     *
     * @param perception   the perception
     * @param currentValue the current value of the belief
     * @param threshold    the threshold for the belief
     */
    private void parseConfirmToAck(Perception perception, float currentValue, float threshold) {
        perception.setType("ack");

        if (floatComparer.greaterThan(currentValue, threshold)) {
            perception.setAttribute("positive");
        } else {
            perception.setAttribute("negative");
        }
    }

    private List<BeliefUpdateLogEntry> updateBeliefsV2(Agent agent, Perception perception) {
        Scenario scenario = agent.getScenario();

        //this will get all knowledge classes in the scenario and filter based on the intention
        //that the user agent received from rasa, the beliefs related to this intention can then be updated
        BeliefMap beliefUpdatingRequest = scenario.getIntentionMapping()
                .getOrDefault(perception.getIntentionName(), null);

        List<BeliefMap> beliefUpdatingRequests =
                beliefUpdatingRequest == null ? new ArrayList<>() : List.of(beliefUpdatingRequest);
        List<BeliefUpdateLogEntry> updates = new ArrayList<>();
        for (BeliefMap beliefMap : beliefUpdatingRequests) {
            List<String> actionsToBeCompleted = beliefService.updateBeliefValue(beliefMap, updates);
            if (agent.getCurrentAction() == null) {
                continue;
            }
            Action activeAction = actionService.getActionById(agent.getCurrentAction());
            if (activeAction != null && actionsToBeCompleted.contains(activeAction.getName())) {
                activeAction.setCompleted(true);
                actionRepository.save(activeAction);
            }
        }

        return updates;
    }

    /**
     * Updates the beliefs of the agent based on the perception.
     *
     * @param agent      the agent
     * @param perception the perception of the agent
     */
    private List<BeliefUpdateLogEntry> updateBeliefs(Agent agent, Perception perception) {
        return updateBeliefsV2(agent, perception);


        //        List<BeliefUpdateLogEntry> beliefUpdateLogs = new ArrayList<>();
        //
        //        Long agentId = agent.getId();
        //        String perceptionName = perception.getType() + "_"
        //          + perception.getSubject() + "_" + perception.getAttribute();
        //
        //        if (perception.getSubject().equals("goal")) {
        //            addIfPresent(beliefUpdateLogs, beliefService.setBeliefValue(agent, "B10", maxValue));
        //        }
        //
        //        if (perception.getSubject().equals("bullying")) {
        //            addIfPresent(beliefUpdateLogs, beliefService.setBeliefValue(agent, "B10", minValue));
        //        }
        //
        //        switch (perceptionName) {
        //            case "request_chitchat_greeting":
        //            case "request_chitchat_faring":
        //                addIfPresent(beliefUpdateLogs, beliefService.increaseBeliefValue(agent, "B4", oneStep));
        //                break; //done
        //            case "request_chitchat_goodbye":
        //                addIfPresent(beliefUpdateLogs, beliefService.setBeliefValue(agent, "B15", maxValue));
        //                break;//done
        //            case "confirm_bullying_summary":
        //                float hasTalkedAboutBullying = beliefService
        //                .getByAgentIdAndName(agent.getId(), "B9").getValue();
        //                System.out.println("summary belief: " + hasTalkedAboutBullying);
        //                parseConfirmToAck(perception, hasTalkedAboutBullying, minValue);
        //                break;
        //            case "ack_contactingkt_compliment":
        //            case "inform_goal_help":
        //                addIfPresent(beliefUpdateLogs, beliefService.increaseBeliefValue(agent, "B7", oneStep));
        //                break;
        //            case "ack_bullying_empathize":
        //            case "ack_goal_empathize":
        //                addIfPresent(beliefUpdateLogs, beliefService.increaseBeliefValue(agent, "B5", oneStep));
        //                break;
        //            case "ack_goal_compliment":
        //            case "ack_confidant_compliment":
        //                addIfPresent(beliefUpdateLogs, beliefService.increaseBeliefValue(agent, "B1", oneStep));
        //                addIfPresent(beliefUpdateLogs, beliefService.increaseBeliefValue(agent,
        //                "B2", oneStep)); // added this
        //                break;
        //            case "ack_unknown_compliment":
        //                addIfPresent(beliefUpdateLogs, beliefService.increaseBeliefValue(agent,
        //                "B2", oneStep));
        //                break;
        //            case "request_confidant_when": //added new
        //            case "request_confidant_feeling": //added new
        //            case "request_confidant_how": //added new
        //            case "request_confidant_say": //added new
        //                addIfPresent(beliefUpdateLogs, beliefService.increaseBeliefValue(agent,
        //                "B2", oneStep));
        //                break;
        //            case "request_goal_dream":
        //                addIfPresent(beliefUpdateLogs, beliefService.setBeliefValue(agent,
        //                "B11", maxValue));
        //                break;
        //            case "request_goal_effect":
        //            case "request_goal_feeling":
        //            case "request_goal_howchild":
        //                addIfPresent(beliefUpdateLogs, beliefService.increaseBeliefValue(agent,
        //                "B1", oneStep));
        //                addIfPresent(beliefUpdateLogs, beliefService.increaseBeliefValue(agent,
        //                "B11", oneStep));
        //                break;
        //            case "confirm_goal_summary":
        //                float hasTalkedAboutGoal = beliefService.getByAgentIdAndName(agent.getId(),
        //                "B10").getValue();
        //                parseConfirmToAck(perception, hasTalkedAboutGoal, minValue);
        //                break;
        //            case "confirm_goal_collaborate":
        //                float hasGoodRelationWithKt = beliefService.getByAgentIdAndName(agent.getId(),
        //                "B4").getValue();
        //                if (floatComparer.greaterThan(hasGoodRelationWithKt, midThreshold)) {
        //                    addIfPresent(beliefUpdateLogs, beliefService.increaseBeliefValue(agent,
        //                    "B7", oneStep));
        //                    addIfPresent(beliefUpdateLogs, beliefService.decreaseBeliefValue(agent,
        //                    "B8", twoSteps));
        //                    addIfPresent(beliefUpdateLogs, beliefService.setBeliefValue(agent,
        //                    "B16", maxValue));
        //                }
        //                parseConfirmToAck(perception, hasGoodRelationWithKt, midThreshold);
        //
        //                break;
        //            case "inform_goal_negative":
        //                float isCurrentlyTalkingAboutGoal = beliefService
        //                .getByAgentIdAndName(agentId, "B10").getValue();
        //                if (floatComparer.equalTo(isCurrentlyTalkingAboutGoal, maxValue)) {
        //                    // This is reset by the setting below:
        //                    // - beliefService.decreaseBeliefValue(agent, "B8", twoSteps);
        //                    addIfPresent(beliefUpdateLogs, beliefService.decreaseBeliefValue(agent, "B7", oneStep));
        //                    addIfPresent(beliefUpdateLogs, beliefService.setBeliefValue(agent, "B8", minValue));
        //                }
        //                break;
        //            case "inform_goal_positive":
        //                if (floatComparer.equalTo(beliefService.getByAgentIdAndName(agentId, "B10")
        //                .getValue(), maxValue)) {
        //                    addIfPresent(beliefUpdateLogs,
        //                    beliefService.setBeliefValue(agent, "B17",
        //                    maxValue));
        //                    actionService.getActionById(agent.getCurrentAction())
        //                    .setCompleted(true);
        //                }
        //                break;
        //            case "request_confidant_who":
        //                addIfPresent(beliefUpdateLogs, beliefService
        //                .setBeliefValue(agent, "B12", maxValue));
        //                break;
        //            case "inform_confidant_help":
        //            case "inform_confidant_say":
        //                addIfPresent(beliefUpdateLogs, beliefService
        //                .setBeliefValue(agent, "B13", maxValue));
        //                Action currentAction = actionService
        //                .getActionById(agent.getCurrentAction());
        //                if (currentAction.getName().equals("A6") || currentAction.getName()
        //                .equals("A7")) {
        //                    actionService.getActionById(agent.getCurrentAction())
        //                    .setCompleted(true);
        //                }
        //                break;
        //            case "confirm_confidant_teacher":
        //                float confidantCanHelp = beliefService.getByAgentIdAndName(agentId,
        //                "B13").getValue();
        //                if (floatComparer.greaterThan(confidantCanHelp, midThreshold)) {
        //                    addIfPresent(beliefUpdateLogs, beliefService.setBeliefValue(agent,
        //                    "B8", minValue));
        //                    addIfPresent(beliefUpdateLogs, beliefService.setBeliefValue(agent,
        //                    "B16", maxValue));
        //                }
        //                parseConfirmToAck(perception, confidantCanHelp, midThreshold);
        //                break;
        //            case "confirm_confidant_parent":
        //                perception.setType("ack");
        //                perception.setAttribute("negative");
        //                break;
        //            case "confirm_confidant_summary":
        //            case "confirm_chitchat_satisfaction":
        //                perception.setType("ack");
        //                Desire currentDesire = desireService.getActiveGoal(agentId);
        //
        //                if (currentDesire != null) {
        //                    System.out.println(currentDesire.getName());
        //                }
        //
        //                if (currentDesire != null && currentDesire.getName().equals("D4")) {
        //                    perception.setAttribute("helpful");
        //                } else {
        //                    perception.setAttribute("negative");
        //                }
        //                break;
        //            default:
        //                break;
        //        }
        //
        //        if (perception.getType().equals("request") && perception.getSubject()
        //        .equals("bullying")) {
        //            addIfPresent(beliefUpdateLogs, beliefService.increaseBeliefValue(agent,
        //            "B6", oneStep));
        //        }
        //
        //        if (perception.getType().equals("ack") && perception.getAttribute()
        //        .equals("neutral")) {
        //            addIfPresent(beliefUpdateLogs, beliefService.increaseBeliefValue(agent,
        //            "B5", (float) 0.05));
        //
        //        }
        //
        //        beliefUpdateLogs.addAll(updateDependentBeliefs(agent));
        //
        //        return beliefUpdateLogs;
    }

    /**
     * Updates all beliefs that are dependent on other beliefs. These are beliefs that should be updated after other
     * updates happened, regardless of whether it was manual or a normal update. E.g., B3 is dependent on relatedness
     * beliefs. When creating more dependent beliefs in future versions, update and log them in this method.
     *
     * @param agent the agent
     * @return the list of logs that should be added as a result of the updates
     */
    private List<BeliefUpdateLogEntry> updateDependentBeliefs(Agent agent) {
        List<BeliefUpdateLogEntry> beliefUpdateLogs = new ArrayList<>();

        Long agentId = agent.getId();

        List<Belief> beliefs = agent.getScenario().getBeliefs();

        //todo make this not hard coded
        beliefs = beliefs.stream().filter(x -> Arrays.stream(relatednessBeliefs).toList()
                .contains(x.getName())).toList();

        float relatedness = beliefService.averageBeliefValue(agentId, beliefs);
        addIfPresent(beliefUpdateLogs, beliefService.setBeliefValue(agent, "B3", relatedness));

        return beliefUpdateLogs;
    }

    /**
     * Update the active value (boolean) of the desires of the agent.
     *
     * @param agent the agent
     */
    private void updateDesires(Agent agent) {
        List<Desire> desires = agent.getScenario().getDesires();

        for (Desire desire : desires) {
            boolean currentActiveValue = updateActiveValue(agent, desire.getName());
            if (desire.isActive() != currentActiveValue) {
                logEntryService.addLogEntry(new DesireUpdateLogEntry(currentActiveValue, desire.getName(), agent));
            }
            desire.setActive(currentActiveValue);
            desireService.addDesire(desire);
        }

        Desire intention = desireService.getActiveGoal(agent.getScenario().getDesires());

        if (intention != null) {
            agent.setIntentionId(intention.getId());
            agentRepository.save(agent);
        }

    }

    /**
     * Gets the response of the agent based on the agent's desires. If the agent's desire is to end the conversation,
     * the function will return goodbye and the agent will be set to inactive.
     *
     * @param agent      the agent
     * @param perception the perception of the agent
     * @return the response of the agent
     */
    private String updateIntentions(Agent agent, Perception perception) {
        agent.setCurrentSubject(perception.getSubject());
        agentRepository.save(agent);

        Desire intention = desireService.getById(agent.getIntentionId());

        System.out.printf("%-20s %s%n", "Intentie:", intention.getFullName());

        if (intention.getName().equals("D2")) { //if the agent's desire to end the conversation, just return goodbye
            agent.isActive(false);
            agentRepository.save(agent);
            return getResponseFromKnowledge(agent.getScenario(), "chitchat", "goodbye");
        }

        switch (perception.getType()) {
            case "request" -> {
                return getResponseFromKnowledge(agent.getScenario(), perception.getSubject(),
                        perception.getAttribute());
            }
            case "ack" -> {
                return respondToAck(agent, perception);
            }
            case "inform" -> {
                if (perception.getAttribute().equals("negative")) {
                    return getResponseFromKnowledge(agent.getScenario(), "ack", "unhelpful");
                }
                return getResponseFromKnowledge(agent.getScenario(), "ack", "neutral");
            }
            default -> {
                return null;
            }
        }
    }

    /**
     * Main method of this class. The function first parses the perception, resolving any missing values.
     * Then, it checks the type of perception. If it's a trigger, the beliefs and desires are not updated.
     * The agent just returns an action associated with the current intention.
     * Otherwise, it updates the agent's beliefs and desires and returns a response based on these.
     *
     * @param agent      the agent
     * @param perception the perception of the agent
     * @return response of the agent
     */
    public String reason(Agent agent, Perception perception) {
        String response;
        List<BeliefUpdateLogEntry> beliefUpdateLogs = new ArrayList<>();
        if (perception.getType().equals("trigger")) {
            Desire intention = desireService.getById(agent.getIntentionId());

            if (intention.getName().equals("D1")) {
                agent.setCurrentSubject("bullying");
                addIfPresent(beliefUpdateLogs, beliefService.increaseBeliefValue(agent,
                        "B9", valueConfiguration.oneStep));
            }

            response = saySomething(agent, intention);

            setLogCauses(beliefUpdateLogs, perception.getText());

            addLogs(beliefUpdateLogs, agent);

            beliefService.sendBeliefsToClientAndLog(agent, beliefUpdateLogs);

            return response;
        }

        System.out.println("received perception: " + perception.getType() + " "
                + perception.getSubject() + " " + perception.getAttribute());
        if (perception.getSubject().equals("unknown")) {
            parsePerception(agent, perception);
        }
        System.out.println("parsed into perception: " + perception.getType() + " "
                + perception.getSubject() + " " + perception.getAttribute());

        beliefUpdateLogs.addAll(updateBeliefs(agent, perception));
        updateDesires(agent);

        response = updateIntentions(agent, perception);

        setLogCauses(beliefUpdateLogs, perception.getText());

        addLogs(beliefUpdateLogs, agent);

        sendPhaseOfAgent(agent.getPhase(), updatePhaseOfAgent(agent), true,
                agent.getUserId());

        beliefService.sendBeliefsToClientAndLog(agent, beliefUpdateLogs);

        return response;
    }

    /**
     * Sends the phase of the agent as well as the previous phase, to the websocket subscription.
     *
     * @param phaseFrom       The phase before.
     * @param phaseTo         The phase now.
     * @param onlyIfDifferent Send only if the phases are different.
     * @param sessionId       The session id of the agent.
     * @return Whether the sending was successful.
     */
    public boolean sendPhaseOfAgent(Phase phaseFrom, Phase phaseTo, boolean onlyIfDifferent, String sessionId) {
        try {
            if (phaseFrom != phaseTo || !onlyIfDifferent) {
                String body = new ObjectMapper().writeValueAsString(new PhaseChangeResponse(phaseFrom, phaseTo));
                messagingTemplate.convertAndSend("/topic/phase/" + sessionId, body);
            }

            return true;
        } catch (JsonProcessingException exception) {
            System.err.println("sendPhaseOfAgent: Phase model could not be parsed");
        }

        return false;
    }

    /**
     * This function creates a new agent using the user or session id provided by Rasa.
     * It populates the beliefs and desires of the agent with the initial values found in the .csv files provided.
     *
     * @param userId the userId (aka session/conversation id)
     */
    @Transactional
    public Agent createAgent(String userId) {
        Agent agent = new Agent();
        String knowledgeFile = knowledgeService.getKnowledge();

        agent.setUserId(userId);
        agent.isActive(true);
        agent.setKnowledgeFile(knowledgeFile);

        Scenario defaultScenario = createScenario(knowledgeFile);
        //        List<String> allScenarios = scenarioService.getAllScenarios();
        //        Random rand = new Random();
        //        Scenario defaultScenario = scenarioService.getScenarioByName
        //        (allScenarios.get(rand.nextInt(allScenarios.size())));
        //        Scenario defaultScenario = scenarioService.getScenarioByName("test");
        agent.setScenario(defaultScenario);

        //agent.setIntentionId(defaultScenario.getConditions().get(0).getDesire().getId());
        updateDesires(agent);
        agent.setIntentionId(agent.getScenario().getDesires().stream().filter(Desire::isActive)
                .findAny().orElseThrow().getId());
        agentRepository.save(agent);

        sendPhaseOfAgent(agent.getPhase(), updatePhaseOfAgent(agent), true,
                agent.getUserId());

        return agent;
    }

    /**
     * Creates default scenarios.
     */
    @Transactional
    public void createDefaultScenarios() {
        for (String x : knowledgeService.getDefaultScenarios()) {
            createScenario(x);
        }
    }

    /**
     * Creates scenario from a knowledge file.
     *
     * @param knowledgeFile the name of the knowledge file.
     * @return the scenario.
     */
    @Transactional
    public Scenario createScenario(String knowledgeFile) {
        List<Knowledge> scenarioKnowledge = knowledgeService.getForScenario(knowledgeFile);

        Set<Belief> initialBeliefs = beliefService.readBeliefsFromCsv();
        beliefService.addBeliefs(initialBeliefs);

        Set<Desire> initialDesires = desireService.readDesiresFromCsv(knowledgeFile);
        desireService.addDesires(initialDesires);

        List<Action> actions = actionService.getActionsByDesires(initialDesires.stream().toList());

        String knowledgeName = knowledgeFile.split("\\.")[0];

        Scenario defaultScenario = scenarioService.createDefaultScenario(initialBeliefs,
                initialDesires, scenarioKnowledge, actions, knowledgeName);
        return defaultScenario;
    }

    /**
     * Adds a log.
     *
     * @param logEntry the entry.
     */
    public void addLog(LogEntry logEntry) {
        logEntryService.addLogEntry(logEntry);
    }

    /**
     * Adds a list of BeliefUpdateLogEntry objects to an agent.
     *
     * @param beliefUpdateLogs the logs to add
     * @param agent            the agent
     */
    public void addLogs(List<BeliefUpdateLogEntry> beliefUpdateLogs, Agent agent) {
        for (BeliefUpdateLogEntry log : beliefUpdateLogs) {
            addLog(log);
            agentRepository.save(agent);
        }
    }

    /**
     * This method retrieves a report containing a transcript of the conversation and BDI status of the agent.
     *
     * @param agent the agent to retrieve the report from
     * @return the path of the report
     */
    public String getReport(Agent agent) {
        agent.isActive(false);
        agentRepository.save(agent);
        return reportService.createBasicReport(agent);
    }

    /**
     * Sets the active value (boolean) of a desire of an agent.
     *
     * @param agent      the agent
     * @param desireName the name of the desire
     * @return the new active value of the desire
     */
    private Boolean updateActiveValue(Agent agent, String desireName) {
        return checkDesireConstraints(agent, desireName);
    }

    /**
     * Gets the active desire of an agent.
     *
     * @param agentId the id of the agent
     * @return the current intention of the agent
     */
    public Desire getIntention(Long agentId) {
        return desireService.getActiveGoal(agentRepository.getById(agentId).getScenario().getDesires());
    }

    /**
     * Checks the activity of a desire, given the name of the desire and the agent id.
     *
     * @param desireName The name of the desire.
     * @return Whether the desire of the agent is active.
     */
    public boolean checkDesireConstraints(Agent agent, String desireName) {
        Scenario sc = agent.getScenario();
        return constraintService.checkDesireConstraints(sc, desireName, agent.getScenario().getBeliefs());
    }

    /**
     * Updates the belief value of a specific belief of an agent.
     *
     * @param conversationId conversation ID to find agent.
     * @param belief         key of belief in database.
     * @param value          value of belief to update to.
     */
    public void updateBelief(String conversationId, String belief,
                             float value) throws IllegalArgumentException, EntityNotFoundException {
        if (floatComparer.lessThan(value, valueConfiguration.minValue)
                || floatComparer.greaterThan(value, valueConfiguration.maxValue)) {
            throw new IllegalArgumentException("New value must be in ["
                    + valueConfiguration.minValue + "," + valueConfiguration.maxValue + "]");
        }
        if (!agentRepository.existsByUserId(conversationId)) {
            throw new EntityNotFoundException("Agent not found");
        }
        Agent agent = agentRepository.getByUserId(conversationId);

        System.out.println("TRAINER IS UPDATING " + belief + " TO " + value);

        Belief beliefObj = agent.getScenario().getBeliefs().stream().filter(c -> c.getName().equals(belief)).findAny()
                .orElse(null);
        float oldValue = (beliefObj != null) ? beliefObj.getValue() : 0;

        float difference = value - oldValue;
        BeliefUpdateType updateType = BeliefUpdateType.SET_TO;

        if (difference < 0) {
            updateType = BeliefUpdateType.DECREASE;
        } else if (difference > 0) {
            updateType = BeliefUpdateType.INCREASE;
        }

        BeliefUpdateLogEntry manualBeliefUpdate =
                new BeliefUpdateLogEntry(updateType, value, BeliefName.valueOf(belief), "Handmatige update", agent,
                        true);

        addLog(manualBeliefUpdate);
        beliefService.setBeliefValue(agent, belief, value);

        List<BeliefUpdateLogEntry> beliefUpdateLogs = updateDependentBeliefs(agent);
        setLogCauses(beliefUpdateLogs, "Bijwerking van handmatige update van " + belief);

        for (BeliefUpdateLogEntry log : beliefUpdateLogs) {
            log.setIsManualUpdate(true);
        }

        addLogs(beliefUpdateLogs, agent);

        List<BeliefUpdateLogEntry> allNewBeliefUpdates = new ArrayList<>();
        allNewBeliefUpdates.add(manualBeliefUpdate);
        allNewBeliefUpdates.addAll(beliefUpdateLogs);

        beliefService.sendBeliefsToClientAndLog(agent, allNewBeliefUpdates);

        updateDesires(agent);
        sendPhaseOfAgent(agent.getPhase(), updatePhaseOfAgent(agent), true, agent.getUserId());

        // If the agent was not active before, set it to be active again
        setAgentActive(conversationId, true);
    }

    /**
     * Gets the conversation of the agent with the given conversationId.
     *
     * @param conversationId the conversationId of the agent
     * @return the conversation of the agent
     * @throws EntityNotFoundException if the agent is not found
     */
    public List<MessageModel> getConversation(String conversationId) throws EntityNotFoundException {
        if (!agentRepository.existsByUserId(conversationId)) {
            throw new EntityNotFoundException("Agent not found");
        }
        Agent agent = agentRepository.getByUserId(conversationId);
        List<MessageLogEntry> log = logEntryService.getMessageLogsByAgentChronological(agent.getId());
        List<MessageModel> conversation = new ArrayList<>();
        for (MessageLogEntry logEntry : log) {
            if (logEntry.getFromUser()) {
                conversation.add(new MessageModel(logEntry.getMessage(), true));
            } else {
                conversation.add(new MessageModel(logEntry.getMessage(), false));
            }
        }
        return conversation;
    }

    /**
     * Gets the past belief transitions of the agent with the given conversationId.
     *
     * @param conversationId the conversationId of the agent
     * @return the past belief transitions of the agent
     * @throws EntityNotFoundException if the agent is not found
     */
    public List<BeliefChangeClientModel> getPastTransitions(String conversationId) throws EntityNotFoundException {
        if (!agentRepository.existsByUserId(conversationId)) {
            throw new EntityNotFoundException("Agent not found");
        }
        Agent agent = agentRepository.getByUserId(conversationId);
        List<BeliefUpdateLogEntry> updateLogs = logEntryService.getBeliefUpdateLogsByAgent(agent.getId());
        List<BeliefChangeClientModel> transitions = new ArrayList<>();
        for (BeliefUpdateLogEntry logEntry : updateLogs) {
            transitions.add(beliefService.getBeliefChangeClientModelFromLog(logEntry, agent));
        }
        return transitions;
    }

    /**
     * Sets the flag for if the trainer is manually sending messages.
     *
     * @param sessionId           the session id of the agent
     * @param isTrainerResponding whether the trainer is manually sending messages
     * @throws EntityNotFoundException if the agent is not found
     */
    public void setTrainerResponding(String sessionId, boolean isTrainerResponding) throws EntityNotFoundException {
        if (!agentRepository.existsByUserId(sessionId)) {
            throw new EntityNotFoundException("Agent not found");
        }
        Agent agent = agentRepository.getByUserId(sessionId);
        agent.isTrainerResponding(isTrainerResponding);
        agentRepository.save(agent);
    }

    /**
     * Gets the boolean flag for if the trainer is manually sending messages.
     *
     * @param sessionId the session id of the agent
     * @return true if the trainer is manually sending messages
     * @throws EntityNotFoundException if the agent is not found
     */
    public boolean isTrainerResponding(String sessionId) throws EntityNotFoundException {
        if (!agentRepository.existsByUserId(sessionId)) {
            throw new EntityNotFoundException("Agent not found");
        }
        Agent agent = agentRepository.getByUserId(sessionId);
        return agent.isTrainerResponding();
    }

    /**
     * Sets activity of an agent, and saves the update.
     *
     * @param sessionId the session id of the agent
     * @param value     the value to set to
     * @throws EntityNotFoundException if the agent is not found
     */
    public void setAgentActive(String sessionId, boolean value) throws EntityNotFoundException {
        if (!agentRepository.existsByUserId(sessionId)) {
            throw new EntityNotFoundException("Agent not found");
        }

        Agent agent = agentRepository.getByUserId(sessionId);
        if (agent.isActive() != value) {
            agent.isActive(value);
        }
        agentRepository.save(agent);
    }

    /**
     * Gets the agent by its id.
     *
     * @param agentId the id of the agent
     * @return the Agent instance
     */
    public Agent getById(long agentId) {
        return agentRepository.getById(agentId);
    }
}
