package com.bdi.agent.service;

<<<<<<< HEAD
import com.bdi.agent.model.*;
import com.bdi.agent.repository.AgentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * author:  Sharon Afua Grundmann
 * as part of MSc Thesis "A BDI-based virtual agent for training child helpline counsellors"
 *
 * This is the main class of the BDI application. Core functionalities are implemented here with the other services handling the BDI data structures and database querying.
 */

@Service
=======
import com.bdi.agent.exceptions.SizeMismatchException;
import com.bdi.agent.model.Action;
import com.bdi.agent.model.Agent;
import com.bdi.agent.model.Belief;
import com.bdi.agent.model.Desire;
import com.bdi.agent.model.Knowledge;
import com.bdi.agent.model.Perception;
import com.bdi.agent.model.api.BeliefChangeClientModel;
import com.bdi.agent.model.api.BeliefChangeModel;
import com.bdi.agent.model.api.MessageModel;
import com.bdi.agent.model.api.PhaseChangeResponse;
import com.bdi.agent.model.enums.BeliefName;
import com.bdi.agent.model.enums.BeliefUpdateType;
import com.bdi.agent.model.enums.DesireName;
import com.bdi.agent.model.enums.Phase;
import com.bdi.agent.model.util.BeliefUpdateLogEntry;
import com.bdi.agent.model.util.DesireUpdateLogEntry;
import com.bdi.agent.model.util.LogEntry;
import com.bdi.agent.model.util.MessageLogEntry;
import com.bdi.agent.model.util.PhaseTransitionConstraints;
import com.bdi.agent.repository.AgentRepository;
import com.bdi.agent.utils.ConstraintProvider;
import com.bdi.agent.utils.FloatComparer;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.locks.Lock;
import javax.persistence.EntityNotFoundException;
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
>>>>>>> origin/updatedLilo
public class AgentService {

    private final AgentRepository agentRepository;

    private final BeliefService beliefService;
    private final DesireService desireService;
    private final ActionService actionService;
    private final ReportService reportService;
<<<<<<< HEAD
    private final KnowledgeService knowledgeService;

    float oneStep = (float) 0.1;
    float twoSteps = (float) 0.2;
    float minThreshold = (float) 0.3;
    float midThreshold = (float) 0.5;
    float maxThreshold = (float) 0.7;
    float minValue = (float) 0;
    float maxValue = (float) 1;

    String[] relatednessBeliefs = {"B4", "B5", "B6", "B7"};     //for estimating value of B3

    String ktPrefix = "KT: ";   //  for printing transcript
    String liloPrefix = "Lilo: ";

    
    @Autowired
    public AgentService(AgentRepository agentRepository, BeliefService beliefService, DesireService desireService, ActionService actionService, ReportService reportService, KnowledgeService knowledgeService) {
=======
    private final LogEntryService logEntryService;
    private final KnowledgeService knowledgeService;
    private final FloatComparer floatComparer;
    private final ConstraintService constraintService;
    private final ConstraintProvider constraintProvider;
    private final SimpMessagingTemplate messagingTemplate;

    @Value("${oneStep}")
    private float oneStep;
    @Value("${twoSteps}")
    private float twoSteps;
    @Value("${minThreshold}")
    private float minThreshold;
    @Value("${midThreshold}")
    private float midThreshold;
    @Value("${maxThreshold}")
    private float maxThreshold;
    @Value("${minValue}")
    private float minValue;
    @Value("${maxValue}")
    private float maxValue;

    @Value("${relatednessBeliefs}")
    String[] relatednessBeliefs;    //for estimating value of B3

    @Value("${ktPrefix}")
    String ktPrefix;   //  for printing transcript
    @Value("${liloPrefix}")
    String liloPrefix;

    private Lock lock;

    /**
     * Constructor for the agent service. Injects the other services and the initialises the knowledge base
     * (agent responses).
     *
     * @param agentRepository       repository for agent data
     * @param beliefService         the belief service
     * @param desireService         the desire service
     * @param actionService         the action service
     * @param reportService         the report service
     * @param knowledgeService      the knowledge service
     * @param floatComparer         the float comparer to compare floats safely
     * @param constraintService     the constraint service used to check desire constraints
     * @param constraintProvider    the constraint provider used to retrieve phase constraints
     * @param messagingTemplate     the template used for websocket communication
     */
    @Autowired
    public AgentService(AgentRepository agentRepository, BeliefService beliefService, DesireService desireService,
                        ActionService actionService, ReportService reportService, LogEntryService logEntryService,
                        KnowledgeService knowledgeService, FloatComparer floatComparer,
                        ConstraintService constraintService, ConstraintProvider constraintProvider,
                        SimpMessagingTemplate messagingTemplate) {
>>>>>>> origin/updatedLilo
        this.agentRepository = agentRepository;
        this.beliefService = beliefService;
        this.desireService = desireService;
        this.actionService = actionService;
        this.reportService = reportService;
<<<<<<< HEAD
        this.knowledgeService = knowledgeService;
=======
        this.logEntryService = logEntryService;
        this.knowledgeService = knowledgeService;
        this.constraintService = constraintService;
        this.floatComparer = floatComparer;
        this.constraintProvider = constraintProvider;
        this.messagingTemplate = messagingTemplate;
>>>>>>> origin/updatedLilo

        knowledgeService.initializeKnowledge();
    }

<<<<<<< HEAD
=======
    /**
     * Sets the beliefs of an agent to be of a specified phase, if possible.
     *
     * @param sessionId The session id of the agent.
     * @param phase The phase to set to.
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
        List<Belief> beliefs = beliefService.sortBeliefsByName(agent.getBeliefs());

        // Check size constraint
        if (constraints.getExampleBeliefValues().length != beliefs.size()) {
            throw new SizeMismatchException("Belief set size " + beliefs.size() + " does not "
                    + "match size " + constraints.getExampleBeliefValues().length);
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
        actionService.setActionsUncompleted(desireService.getByAgent(agent.getId()));

        return beliefUpdates;
    }

    /**
     * Sets the current phase of an agent, considering the active goal.
     *
     * @param agent The agent.
     * @return The phase the agent is currently in.
     */
    public Phase updatePhaseOfAgent(Agent agent) {
        Desire activeDesire = desireService.getActiveGoal(agent.getId());

        Phase setTo = Phase.PHASE1;
        if (activeDesire != null) {
            setTo = switch (activeDesire.getName()) {
                case "D1" -> Phase.PHASE2;
                case "D2" -> Phase.PHASE5;
                case "D5", "D3" -> Phase.PHASE3;
                case "D4" -> Phase.PHASE4;
                default -> throw new IllegalStateException("Unexpected desire name " + activeDesire.getName());
            };
        }

        agent.setPhase(setTo);
        agentRepository.save(agent);

        return setTo;
    }

>>>>>>> origin/updatedLilo
    public boolean containsUserId(String userId) {
        return agentRepository.existsByUserId(userId);
    }

    public Agent getByUserId(String userId) {
        return agentRepository.getByUserId(userId);
    }

<<<<<<< HEAD
    public List<Agent> getAll() {
        return agentRepository.findAll();
    }

=======
>>>>>>> origin/updatedLilo
    private String resolvePerceptionSubject(Agent agent) throws NullPointerException {
        return agent.getCurrentSubject();
    }

<<<<<<< HEAD

    private void parsePerception(Agent agent, Perception perception) {
        try {
             perception.setSubject(resolvePerceptionSubject(agent));
=======
    /**
     * Sets the subject of the perception to the current subject of the agent.
     *
     * @param agent the agent
     * @param perception the perception of the agent
     */
    private void parsePerception(Agent agent, Perception perception) {
        try {
            perception.setSubject(resolvePerceptionSubject(agent));
>>>>>>> origin/updatedLilo
        } catch (NullPointerException e) {
            System.err.println("parsePerception: cannot resolve perception subject");
            perception.setSubject("");
        }
    }

<<<<<<< HEAD
=======
    /**
     * Gets a response from the database for an acknowledgement with attribute of the perception.
     *
     * @param perception the perception of the agent
     * @return the response from the knowledge base
     */
>>>>>>> origin/updatedLilo
    private String respondToAck(Perception perception) {
        String type = "ack";
        String attribute = perception.getAttribute();

        return getResponseFromKnowledge(type, attribute);
    }

<<<<<<< HEAD
=======
    /**
     * Gets a response from the database with certain subject and attribute.
     *
     * @param subject subject of the perception
     * @param attribute attribute of the perception
     * @return the response from the knowledge base
     */
>>>>>>> origin/updatedLilo
    private String getResponseFromKnowledge(String subject, String attribute) {
        String err = "Ik begrijp niet wat je bedoelt";

        try {
            Knowledge knowledge = knowledgeService.getBySubjectAndAttribute(subject, attribute);
            return knowledgeService.getResponse(knowledge);
        } catch (NullPointerException e) {
            System.err.println("getResponseFromKnowledge: could not find knowledge");
        }

        return err;
    }


<<<<<<< HEAD
=======
    /**
     * Gets a response from the database corresponding to an uncompleted action related to a desire.
     *
     * @param agent the agent
     * @param desire the desire of the agent
     * @return the response from the knowledge base
     */
>>>>>>> origin/updatedLilo
    private String saySomething(Agent agent, Desire desire) {

        try {
            Action action = actionService.getUncompletedAction(desire.getId());
            agent.setCurrentAction(action.getId());

            if (action.getType().equals("inform")) {
<<<<<<< HEAD
                action.setIsCompleted(true);
=======
                action.setCompleted(true);
>>>>>>> origin/updatedLilo
            }
            actionService.addAction(action);

            return getResponseFromKnowledge(action.getSubject(), action.getAttribute());

        } catch (NullPointerException e) {
<<<<<<< HEAD
=======
            e.printStackTrace();
>>>>>>> origin/updatedLilo
            System.err.println("saySomething: could not get action");
        }

        return null;
    }

<<<<<<< HEAD
    private void parseConfirmToAck(Perception perception, float currentValue, float threshold) {
        perception.setType("ack");

        if (currentValue > threshold) {
=======
    /**
     * Sets the perception type to "ack" and the attribute to "positive" or "negative" if the current value is above
     * or below the threshold.
     *
     * @param perception the perception
     * @param currentValue the current value of the belief
     * @param threshold the threshold for the belief
     */
    private void parseConfirmToAck(Perception perception, float currentValue, float threshold) {
        perception.setType("ack");

        if (floatComparer.greaterThan(currentValue, threshold)) {
>>>>>>> origin/updatedLilo
            perception.setAttribute("positive");
        } else {
            perception.setAttribute("negative");
        }
    }

<<<<<<< HEAD
    private void updateBeliefs(Agent agent, Perception perception) {
=======
    /**
     * Updates the beliefs of the agent based on the perception.
     *
     * @param agent the agent
     * @param perception the perception of the agent
     */
    private List<BeliefUpdateLogEntry> updateBeliefs(Agent agent, Perception perception) {

        List<BeliefUpdateLogEntry> beliefUpdateLogs = new ArrayList<>();

>>>>>>> origin/updatedLilo
        Long agentId = agent.getId();
        String perceptionName = perception.getType() + "_" + perception.getSubject() + "_" + perception.getAttribute();

        if (perception.getSubject().equals("goal")) {
<<<<<<< HEAD
            beliefService.setBeliefValue(agent, "B10", maxValue);
        }

        if (perception.getSubject().equals("bullying")) {
            beliefService.setBeliefValue(agent, "B10", minValue);
=======
            addIfPresent(beliefUpdateLogs, beliefService.setBeliefValue(agent, "B10", maxValue));
        }

        if (perception.getSubject().equals("bullying")) {
            addIfPresent(beliefUpdateLogs, beliefService.setBeliefValue(agent, "B10", minValue));
>>>>>>> origin/updatedLilo
        }

        switch (perceptionName) {
            case "request_chitchat_greeting":
            case "request_chitchat_faring":
<<<<<<< HEAD
                beliefService.increaseBeliefValue(agent, "B4", oneStep);
                break;
            case "request_chitchat_goodbye":
                beliefService.setBeliefValue(agent, "B15", maxValue);
=======
                addIfPresent(beliefUpdateLogs, beliefService.increaseBeliefValue(agent, "B4", oneStep));
                break;
            case "request_chitchat_goodbye":
                addIfPresent(beliefUpdateLogs, beliefService.setBeliefValue(agent, "B15", maxValue));
>>>>>>> origin/updatedLilo
                break;
            case "confirm_bullying_summary":
                float hasTalkedAboutBullying = beliefService.getByAgentIdAndName(agent.getId(), "B9").getValue();
                System.out.println("summary belief: " + hasTalkedAboutBullying);
                parseConfirmToAck(perception, hasTalkedAboutBullying, minValue);
                break;
            case "ack_contactingkt_compliment":
            case "inform_goal_help":
<<<<<<< HEAD
                beliefService.increaseBeliefValue(agent, "B7", oneStep);
                break;
            case "ack_bullying_empathize":
            case "ack_goal_empathize":
                beliefService.increaseBeliefValue(agent, "B5", oneStep);
                break;
            case "ack_goal_compliment":
            case "ack_confidant_compliment":
                beliefService.increaseBeliefValue(agent, "B2", oneStep);
                break;
            case "request_goal_dream":
                beliefService.setBeliefValue(agent, "B11", maxValue);
=======
                addIfPresent(beliefUpdateLogs, beliefService.increaseBeliefValue(agent, "B7", oneStep));
                break;
            case "ack_bullying_empathize":
            case "ack_goal_empathize":
                addIfPresent(beliefUpdateLogs, beliefService.increaseBeliefValue(agent, "B5", oneStep));
                break;
            case "ack_goal_compliment":
            case "ack_confidant_compliment":
                addIfPresent(beliefUpdateLogs, beliefService.increaseBeliefValue(agent, "B1", oneStep));
                addIfPresent(beliefUpdateLogs, beliefService.increaseBeliefValue(agent, "B2", oneStep)); // added this
                break;
            case "ack_unknown_compliment":
                addIfPresent(beliefUpdateLogs, beliefService.increaseBeliefValue(agent, "B2", oneStep));
                break;
            case "request_confidant_when": //added new
            case "request_confidant_feeling": //added new
            case "request_confidant_how": //added new
            case "request_confidant_say": //added new
                addIfPresent(beliefUpdateLogs, beliefService.increaseBeliefValue(agent, "B2", oneStep));
                break;
            case "request_goal_dream":
                addIfPresent(beliefUpdateLogs, beliefService.setBeliefValue(agent, "B11", maxValue));
>>>>>>> origin/updatedLilo
                break;
            case "request_goal_effect":
            case "request_goal_feeling":
            case "request_goal_howchild":
<<<<<<< HEAD
                beliefService.increaseBeliefValue(agent, "B1", oneStep);
                beliefService.increaseBeliefValue(agent, "B11", oneStep);
=======
                addIfPresent(beliefUpdateLogs, beliefService.increaseBeliefValue(agent, "B1", oneStep));
                addIfPresent(beliefUpdateLogs, beliefService.increaseBeliefValue(agent, "B11", oneStep));
>>>>>>> origin/updatedLilo
                break;
            case "confirm_goal_summary":
                float hasTalkedAboutGoal = beliefService.getByAgentIdAndName(agent.getId(), "B10").getValue();
                parseConfirmToAck(perception, hasTalkedAboutGoal, minValue);
                break;
            case "confirm_goal_collaborate":
                float hasGoodRelationWithKt = beliefService.getByAgentIdAndName(agent.getId(), "B4").getValue();
<<<<<<< HEAD
                if (hasGoodRelationWithKt> midThreshold) {
                    beliefService.increaseBeliefValue(agent, "B7", oneStep);
                    beliefService.decreaseBeliefValue(agent, "B8", twoSteps);
                    beliefService.setBeliefValue(agent, "B16", maxValue);
=======
                if (floatComparer.greaterThan(hasGoodRelationWithKt, midThreshold)) {
                    addIfPresent(beliefUpdateLogs, beliefService.increaseBeliefValue(agent, "B7", oneStep));
                    addIfPresent(beliefUpdateLogs, beliefService.decreaseBeliefValue(agent, "B8", twoSteps));
                    addIfPresent(beliefUpdateLogs, beliefService.setBeliefValue(agent, "B16", maxValue));
>>>>>>> origin/updatedLilo
                }
                parseConfirmToAck(perception, hasGoodRelationWithKt, midThreshold);

                break;
            case "inform_goal_negative":
                float isCurrentlyTalkingAboutGoal = beliefService.getByAgentIdAndName(agentId, "B10").getValue();
<<<<<<< HEAD
                if (isCurrentlyTalkingAboutGoal == maxValue) {
                    beliefService.decreaseBeliefValue(agent, "B8", twoSteps);
                    beliefService.decreaseBeliefValue(agent, "B7", oneStep);
                    beliefService.setBeliefValue(agent, "B8", minValue);
                }
                break;
            case "inform_goal_positive":
                if (beliefService.getByAgentIdAndName(agentId, "B10").getValue() == maxValue) {
                    beliefService.setBeliefValue(agent, "B17", maxValue);
                    actionService.getActionById(agent.getCurrentAction()).setIsCompleted(true);
                }
                break;
            case "request_confidant_who":
                beliefService.setBeliefValue(agent, "B12", maxValue);
                break;
            case "inform_confidant_help":
            case "inform_confidant_say":
                beliefService.setBeliefValue(agent, "B13", maxValue);
                Action currentAction = actionService.getActionById(agent.getCurrentAction());
                if (currentAction.getName().equals("A6") || currentAction.getName().equals("A7")) {
                    actionService.getActionById(agent.getCurrentAction()).setIsCompleted(true);
=======
                if (floatComparer.equalTo(isCurrentlyTalkingAboutGoal, maxValue)) {
                    // This is reset by the setting below:
                    // - beliefService.decreaseBeliefValue(agent, "B8", twoSteps);
                    addIfPresent(beliefUpdateLogs, beliefService.decreaseBeliefValue(agent, "B7", oneStep));
                    addIfPresent(beliefUpdateLogs, beliefService.setBeliefValue(agent, "B8", minValue));
                }
                break;
            case "inform_goal_positive":
                if (floatComparer.equalTo(beliefService.getByAgentIdAndName(agentId, "B10").getValue(),
                        maxValue)) {
                    addIfPresent(beliefUpdateLogs, beliefService.setBeliefValue(agent, "B17", maxValue));
                    actionService.getActionById(agent.getCurrentAction()).setCompleted(true);
                }
                break;
            case "request_confidant_who":
                addIfPresent(beliefUpdateLogs, beliefService.setBeliefValue(agent, "B12", maxValue));
                break;
            case "inform_confidant_help":
            case "inform_confidant_say":
                addIfPresent(beliefUpdateLogs, beliefService.setBeliefValue(agent, "B13", maxValue));
                Action currentAction = actionService.getActionById(agent.getCurrentAction());
                if (currentAction.getName().equals("A6") || currentAction.getName().equals("A7")) {
                    actionService.getActionById(agent.getCurrentAction()).setCompleted(true);
>>>>>>> origin/updatedLilo
                }
                break;
            case "confirm_confidant_teacher":
                float confidantCanHelp = beliefService.getByAgentIdAndName(agentId, "B13").getValue();
<<<<<<< HEAD
                if (confidantCanHelp > midThreshold) {
                    beliefService.setBeliefValue(agent, "B8", minValue);
                    beliefService.setBeliefValue(agent, "B16", maxValue);
=======
                if (floatComparer.greaterThan(confidantCanHelp, midThreshold)) {
                    addIfPresent(beliefUpdateLogs, beliefService.setBeliefValue(agent, "B8", minValue));
                    addIfPresent(beliefUpdateLogs, beliefService.setBeliefValue(agent, "B16", maxValue));
>>>>>>> origin/updatedLilo
                }
                parseConfirmToAck(perception, confidantCanHelp, midThreshold);
                break;
            case "confirm_confidant_parent":
                perception.setType("ack");
                perception.setAttribute("negative");
                break;
            case "confirm_confidant_summary":
            case "confirm_chitchat_satisfaction":
                perception.setType("ack");
                Desire currentDesire = desireService.getActiveGoal(agentId);
<<<<<<< HEAD
                System.out.println(currentDesire.getName());
                if (currentDesire.getName().equals("D4")) {
=======

                if (currentDesire != null) {
                    System.out.println(currentDesire.getName());
                }

                if (currentDesire != null && currentDesire.getName().equals("D4")) {
>>>>>>> origin/updatedLilo
                    perception.setAttribute("helpful");
                } else {
                    perception.setAttribute("negative");
                }
                break;
<<<<<<< HEAD

        }

        if (perception.getType().equals("request") && perception.getSubject().equals("bullying")) {
            beliefService.increaseBeliefValue(agent, "B6", oneStep);
        }

        if (perception.getType().equals("ack") && perception.getAttribute().equals("neutral")) {
            beliefService.increaseBeliefValue(agent, "B5", (float) 0.05);

        }

        float relatedness= beliefService.averageBeliefValue(agentId, relatednessBeliefs);
        beliefService.setBeliefValue(agent, "B3", relatedness);

    }


=======
            default:
                break;
        }

        if (perception.getType().equals("request") && perception.getSubject().equals("bullying")) {
            addIfPresent(beliefUpdateLogs, beliefService.increaseBeliefValue(agent, "B6", oneStep));
        }

        if (perception.getType().equals("ack") && perception.getAttribute().equals("neutral")) {
            addIfPresent(beliefUpdateLogs, beliefService.increaseBeliefValue(agent, "B5", (float) 0.05));

        }

        beliefUpdateLogs.addAll(updateDependentBeliefs(agent));

        return beliefUpdateLogs;
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

        float relatedness = beliefService.averageBeliefValue(agentId, relatednessBeliefs);
        addIfPresent(beliefUpdateLogs, beliefService.setBeliefValue(agent, "B3", relatedness));

        return beliefUpdateLogs;
    }

    /**
     * Given a list of belief update logs and an Optional belief update log, adds the log to the list if it is present.
     *
     * @param logs the list of belief update logs
     * @param updateLog the optional log to add if present
     */
    private static void addIfPresent(List<BeliefUpdateLogEntry> logs, Optional<BeliefUpdateLogEntry> updateLog) {
        updateLog.ifPresent(logs::add);
    }

    /**
     * Update the active value (boolean) of the desires of the agent.
     *
     * @param agent the agent
     */
>>>>>>> origin/updatedLilo
    private void updateDesires(Agent agent) {

        List<Desire> desires = desireService.getByAgent(agent.getId());

        for (Desire desire : desires) {
            boolean currentActiveValue = updateActiveValue(agent.getId(), desire.getName());
<<<<<<< HEAD
=======
            if (desire.isActive() != currentActiveValue) {
                logEntryService.addLogEntry(
                        new DesireUpdateLogEntry(currentActiveValue, DesireName.valueOf(desire.getName()), agent));
            }
>>>>>>> origin/updatedLilo
            desire.setActiveValue(currentActiveValue);
            desireService.addDesire(desire);
        }

        Desire intention = desireService.getActiveGoal(agent.getId());
<<<<<<< HEAD
        agent.setIntention(intention.getId());
=======

        if (intention != null) {
            agent.setIntentionId(intention.getId());
        }

>>>>>>> origin/updatedLilo
        agentRepository.save(agent);
    }


<<<<<<< HEAD
=======
    /**
     * Gets the response of the agent based on the agent's desires. If the agent's desire is to end the conversation,
     * the function will return goodbye and the agent will be set to inactive.
     *
     * @param agent the agent
     * @param perception the perception of the agent
     * @return the response of the agent
     */
>>>>>>> origin/updatedLilo
    private String updateIntentions(Agent agent, Perception perception) {
        agent.setCurrentSubject(perception.getSubject());
        agentRepository.save(agent);

<<<<<<< HEAD
        Desire intention = desireService.getById(agent.getIntention());

        agent.addLog(String.format("%-20s %s", "Intentie:", intention.getFullName()));
        System.out.printf("%-20s %s%n", "Intentie:", intention.getFullName());

        if (intention.getName().equals("D2")) { //if the agent's desire to end the conversation, just return goodbye
            agent.setActive(false);
=======
        Desire intention = desireService.getById(agent.getIntentionId());

        System.out.printf("%-20s %s%n", "Intentie:", intention.getFullName());

        if (intention.getName().equals("D2")) { //if the agent's desire to end the conversation, just return goodbye
            agent.isActive(false);
>>>>>>> origin/updatedLilo
            agentRepository.save(agent);
            return getResponseFromKnowledge("chitchat", "goodbye");
        }

        switch (perception.getType()) {
<<<<<<< HEAD
            case "request":
                return getResponseFromKnowledge(perception.getSubject(), perception.getAttribute());
            case "ack":
                return respondToAck(perception);
            case "inform":
=======
            case "request" -> {
                return getResponseFromKnowledge(perception.getSubject(), perception.getAttribute());
            }
            case "ack" -> {
                return respondToAck(perception);
            }
            case "inform" -> {
>>>>>>> origin/updatedLilo
                if (perception.getAttribute().equals("negative")) {
                    return getResponseFromKnowledge("ack", "unhelpful");
                }
                return getResponseFromKnowledge("ack", "neutral");
<<<<<<< HEAD
        }

        return null;
=======
            }
            default -> {
                return null;
            }
        }
>>>>>>> origin/updatedLilo
    }

    /**
     * Main method of this class. The function first parses the perception, resolving any missing values.
     * Then, it checks the type of perception. If it's a trigger, the beliefs and desires are not updated.
     * The agent just returns an action associated with the current intention.
     * Otherwise, it updates the agent's beliefs and desires and returns a response based on these.
<<<<<<< HEAD
     * @return response
     */
    public String reason(Agent agent, Perception perception) {
        String response;


        if (perception.getType().equals("trigger")) {
            Desire intention = desireService.getById(agent.getIntention());

            if (intention.getName().equals("D1")) {
                agent.setCurrentSubject("bullying");
                beliefService.increaseBeliefValue(agent, "B9", oneStep);
=======
     *
     * @param agent the agent
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
                addIfPresent(beliefUpdateLogs, beliefService.increaseBeliefValue(agent, "B9", oneStep));
>>>>>>> origin/updatedLilo
            }

            response = saySomething(agent, intention);

<<<<<<< HEAD
            if (response != null) {
                agent.addLog(liloPrefix.concat(response));
                agentRepository.save(agent);
            }
=======
            setLogCauses(beliefUpdateLogs, perception.getText());

            addLogs(beliefUpdateLogs, agent);

            beliefService.sendBeliefsToClientAndLog(agent, beliefUpdateLogs);
>>>>>>> origin/updatedLilo

            return response;
        }

<<<<<<< HEAD
        System.out.println("received perception: " + perception.getType() + " " + perception.getSubject() + " " + perception.getAttribute());
        if (perception.getSubject().equals("unknown")) {
            parsePerception(agent, perception);
        }
        System.out.println("parsed into perception: " + perception.getType() + " " + perception.getSubject() + " " + perception.getAttribute());

        agent.addLog(ktPrefix.concat(perception.getText()));

        updateBeliefs(agent, perception);
        updateDesires(agent);

        response = updateIntentions(agent,perception);

        if (response != null) {
            agent.addLog(liloPrefix.concat(response));
            agentRepository.save(agent);
        }
=======
        System.out.println("received perception: " + perception.getType() + " " + perception.getSubject() + " "
                + perception.getAttribute());
        if (perception.getSubject().equals("unknown")) {
            parsePerception(agent, perception);
        }
        System.out.println("parsed into perception: " + perception.getType() + " " + perception.getSubject() + " "
                + perception.getAttribute());

        beliefUpdateLogs.addAll(updateBeliefs(agent, perception));
        updateDesires(agent);

        response = updateIntentions(agent, perception);

        setLogCauses(beliefUpdateLogs, perception.getText());

        addLogs(beliefUpdateLogs, agent);

        sendPhaseOfAgent(agent.getPhase(), updatePhaseOfAgent(agent), true, agent.getUserId());

        beliefService.sendBeliefsToClientAndLog(agent, beliefUpdateLogs);
>>>>>>> origin/updatedLilo

        return response;
    }

    /**
<<<<<<< HEAD
     * This function creates a new agent using the user or session id provided by Rasa.
     * It populates the beliefs and desires of the agent with the initial values found in the .csv files provided.
     *
     */
    public void createAgent(String userId) {

        Agent agent = new Agent();
        agent.setUser(userId);
        agent.setActive(true);
=======
     * Sends the phase of the agent as well as the previous phase, to the websocket subscription.
     *
     * @param phaseFrom The phase before.
     * @param phaseTo The phase now.
     * @param onlyIfDifferent Send only if the phases are different.
     * @param sessionId The session id of the agent.
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
    public Agent createAgent(String userId) {
        Agent agent = new Agent();
        agent.setUserId(userId);
        agent.isActive(true);
>>>>>>> origin/updatedLilo

        Set<Belief> initialBeliefs = beliefService.readBeliefsFromCsv(agent);
        beliefService.addBeliefs(initialBeliefs);
        agent.setBeliefs(initialBeliefs);

        Set<Desire> initialDesires = desireService.readDesiresFromCsv(agent);
        desireService.addDesires(initialDesires);
<<<<<<< HEAD
        agent.setDesires(initialDesires);

        agentRepository.save(agent);
        updateDesires(agent);

    }


    /**
    * This method retrieves a report containing a transcript of the conversation and BDI status of the agent.
    *
    * */
    public String getReport(Agent agent) {
        agent.setActive(false);
        agentRepository.save(agent);
        return reportService.createReport(agent);

    }

    private Boolean updateActiveValue(Long agentId, String desireName) {
        switch (desireName) {
            case "D1":
                return d1Context(agentId);
            case "D2":
                return d2Context(agentId);
            case "D3":
                return d3Context(agentId);
            case "D4":
                return d4Context(agentId);
            case "D5":
                return d5Context(agentId);
        }

        return false;
    }

    private Boolean d1Context(Long agentId) {    //wil over zijn probleem hebben
        return beliefService.getByAgentIdAndName(agentId, "B10").getValue() == minValue &
                beliefService.getByAgentIdAndName(agentId, "B12").getValue() == minValue &&
                beliefService.getByAgentIdAndName(agentId, "B9").getValue() < maxValue &&
                beliefService.getByAgentIdAndName(agentId, "B3").getValue() > minThreshold;
    }

    private Boolean d2Context(Long agentId) {   //wil het gesprek beëindigen
        return beliefService.getByAgentIdAndName(agentId, "B1").getValue() < minThreshold ||
                beliefService.getByAgentIdAndName(agentId, "B17").getValue() == maxValue ||
                (beliefService.getByAgentIdAndName(agentId, "B3").getValue() < midThreshold &&
                        beliefService.getByAgentIdAndName(agentId, "B12").getValue() == maxValue);
    }


    private Boolean d3Context(Long agentId) {    //wil dat KT de pestkoppen van school haalt
        return beliefService.getByAgentIdAndName(agentId, "B10").getValue() == maxValue &&
                beliefService.getByAgentIdAndName(agentId, "B8").getValue() > maxThreshold &&
                beliefService.getByAgentIdAndName(agentId, "B2").getValue() < maxThreshold;
    }


    private Boolean d4Context(Long agentId) {    //wil met leraar praten over situatie
        return beliefService.getByAgentIdAndName(agentId, "B12").getValue() == maxValue &&
                beliefService.getByAgentIdAndName(agentId, "B13").getValue() >= midThreshold &&
                beliefService.getByAgentIdAndName(agentId, "B3").getValue() >= midThreshold;     //only activated if phase 2 is executed
    }

    private Boolean d5Context(Long agentId) {    //wil samen met KT een oplossing zoeken
        return beliefService.getByAgentIdAndName(agentId, "B4").getValue() >= midThreshold &&
                beliefService.getByAgentIdAndName(agentId, "B10").getValue() == maxValue &&
                beliefService.getByAgentIdAndName(agentId, "B12").getValue() == minValue;
    }



=======
        agent.getDesires().addAll(initialDesires);

        updateDesires(agent);
        sendPhaseOfAgent(agent.getPhase(), updatePhaseOfAgent(agent), true, agent.getUserId());
        agentRepository.save(agent);

        return agent;
    }

    /**
     * For each given BeliefUpdateLogEntry, sets the cause to the message within the given perception.
     *
     * @param beliefUpdateLogs the logs to set the causes of
     * @param cause the cause of the belief update
     */
    public static void setLogCauses(List<BeliefUpdateLogEntry> beliefUpdateLogs, String cause) {
        for (BeliefUpdateLogEntry log : beliefUpdateLogs) {
            log.setCause(cause);
        }
    }

    public void addLog(LogEntry logEntry) {
        logEntryService.addLogEntry(logEntry);
    }

    /**
     * Adds a list of BeliefUpdateLogEntry objects to an agent.
     *
     * @param beliefUpdateLogs the logs to add
     * @param agent the agent
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
     * @param agentId the id of the agent
     * @param desireName the name of the desire
     * @return the new active value of the desire
     */
    private Boolean updateActiveValue(Long agentId, String desireName) {
        return switch (desireName) {
            case "D1" -> checkDesireConstraints(agentId, DesireName.D1);
            case "D2" -> checkDesireConstraints(agentId, DesireName.D2);
            case "D3" -> checkDesireConstraints(agentId, DesireName.D3);
            case "D4" -> checkDesireConstraints(agentId, DesireName.D4);
            case "D5" -> checkDesireConstraints(agentId, DesireName.D5);
            default -> false;
        };
    }

    /**
     * Gets the active desire of an agent.
     *
     * @param agentId the id of the agent
     * @return the current intention of the agent
     */
    public Desire getIntention(Long agentId) {
        return desireService.getActiveGoal(agentId);
    }

    /**
     * Checks the activity of a desire, given the name of the desire and the agent id.
     *
     * @param agentId       The id of the agent.
     * @param desireName    The name of the desire.
     * @return Whether the desire of the agent is active.
     */
    public boolean checkDesireConstraints(Long agentId, DesireName desireName) {
        float[] values = beliefService.getValuesByAgentSortByName(agentId);
        return constraintService.checkDesireConstraints(desireName, values);
    }

    /**
     * Updates the belief value of a specific belief of an agent.
     *
     * @param conversationId conversation ID to find agent.
     * @param belief key of belief in database.
     * @param value value of belief to update to.
     */
    public void updateBelief(String conversationId, String belief, float value) throws IllegalArgumentException,
            EntityNotFoundException {
        if (floatComparer.lessThan(value, minValue) || floatComparer.greaterThan(value, maxValue)) {
            throw new IllegalArgumentException("New value must be in [" + minValue + "," + maxValue + "]");
        }
        if (!agentRepository.existsByUserId(conversationId)) {
            throw new EntityNotFoundException("Agent not found");
        }
        Agent agent = agentRepository.getByUserId(conversationId);

        System.out.println("TRAINER IS UPDATING " + belief + " TO " + value);

        Belief beliefObj = beliefService.getByAgentIdAndName(agent.getId(), belief);
        float oldValue = (beliefObj != null) ? beliefService.getByAgentIdAndName(agent.getId(), belief).getValue() : 0;

        float difference = value - oldValue;
        BeliefUpdateType updateType = BeliefUpdateType.SET_TO;

        if (difference < 0) {
            updateType = BeliefUpdateType.DECREASE;
        } else if (difference > 0) {
            updateType = BeliefUpdateType.INCREASE;
        }

        BeliefUpdateLogEntry manualBeliefUpdate = new BeliefUpdateLogEntry(updateType, value,
                BeliefName.valueOf(belief), "Handmatige update", agent, true);

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
     * @param sessionId the session id of the agent
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
     * @param value the value to set to
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
>>>>>>> origin/updatedLilo
}
