package com.bdi.agent.service;

import com.bdi.agent.exceptions.SizeMismatchException;
import com.bdi.agent.model.*;
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
public class AgentService {

    private final AgentRepository agentRepository;

    private final BeliefService beliefService;
    private final DesireService desireService;
    private final HumanValuesService hValuesService;
    private final ConvictionService convictionService;
    private final ActionService actionService;
    private final ReportService reportService;
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
     * @param agentRepository       repository for agent data
     * @param beliefService         the belief service
     * @param desireService         the desire service
     * @param hValuesService        the human values service
     * @param convictionService     the conviction service
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
                        HumanValuesService hValuesService, ConvictionService convictionService, ActionService actionService, ReportService reportService, LogEntryService logEntryService,
                        KnowledgeService knowledgeService, FloatComparer floatComparer,
                        ConstraintService constraintService, ConstraintProvider constraintProvider,
                        SimpMessagingTemplate messagingTemplate) {
        this.agentRepository = agentRepository;
        this.beliefService = beliefService;
        this.desireService = desireService;
        this.hValuesService = hValuesService;
        this.convictionService = convictionService;
        this.actionService = actionService;
        this.reportService = reportService;
        this.logEntryService = logEntryService;
        this.knowledgeService = knowledgeService;
        this.constraintService = constraintService;
        this.floatComparer = floatComparer;
        this.constraintProvider = constraintProvider;
        this.messagingTemplate = messagingTemplate;

        try {
            knowledgeService.initializeKnowledge();
        } catch(Exception e) {
            e.printStackTrace();
        }
    }

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
                case "D5", "D3", "D7", "D9" -> Phase.PHASE3;
                case "D4", "D6", "D8", "D10" -> Phase.PHASE4;
                default -> throw new IllegalStateException("Unexpected desire name " + activeDesire.getName());
            };
        }

        agent.setPhase(setTo);
        agentRepository.save(agent);

        return setTo;
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
     * @param agent the agent
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

        return getResponseFromKnowledge(agent.getKnowledgeFile(), type, attribute);
    }

    /**
     * Gets a response from the database with certain subject and attribute.
     *
     * @param subject subject of the perception
     * @param attribute attribute of the perception
     * @return the response from the knowledge base
     */

    private String getResponseFromKnowledge(String knowledgeName, String subject, String attribute) {
        String err = "I do not understand what you mean";

        try {
            Knowledge knowledge = knowledgeService.getBySubjectAndAttribute(knowledgeName, subject, attribute);
            return knowledgeService.getResponse(knowledge);
        } catch (NullPointerException e) {
            System.err.println("getResponseFromKnowledge: could not find knowledge");
        }

        return err;
    }


    /**
     * Gets a response from the database corresponding to an uncompleted action related to a desire.
     *
     * @param agent the agent
     * @param desire the desire of the agent
     * @return the response from the knowledge base
     */
    private String saySomething(Agent agent, Desire desire) {

        try {
            Action action = actionService.getUncompletedAction(desire.getId());
            agent.setCurrentAction(action.getId());

            if (action.getType().equals("inform")) {
                action.setCompleted(true);
            }
            actionService.addAction(action);

            return getResponseFromKnowledge(agent.getKnowledgeFile(), action.getSubject(), action.getAttribute());

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
     * @param perception the perception
     * @param currentValue the current value of the belief
     * @param threshold the threshold for the belief
     */
    private void parseConfirmToAck(Perception perception, float currentValue, float threshold) {
        perception.setType("ack");

        if (floatComparer.greaterThan(currentValue, threshold)) {
            perception.setAttribute("positive");
        } else {
            perception.setAttribute("negative");
        }
    }




    /**
     * everytime the user sends a message, this method is called
     *
     * @param sessionId The session id of the agent to which the message should be sent.
     */
    public void sendIntent(String sessionId, Agent agent, String PerceptionName) {
        String message = PerceptionName + " ; " + agent.getPhase().name();
        messagingTemplate.convertAndSend("/topic/intent/" + sessionId, message);

    }
//
//    public String sendAdvice(String sessionId) {
//        return "This is an advice" + sessionId + " " + counter++;
//
//    }



    /**
     * Updates the beliefs of the agent based on the perception.
     *
     * @param agent the agent
     * @param perception the perception of the agent
     */
    private List<BeliefUpdateLogEntry> updateBeliefs(Agent agent, Perception perception) {
        List<BeliefUpdateLogEntry> beliefUpdateLogs = new ArrayList<>();

        Long agentId = agent.getId();
        String perceptionName = perception.getType() + "_" + perception.getSubject() + "_" + perception.getAttribute();

        if (perception.getSubject().equals("goal")) {
            addIfPresent(beliefUpdateLogs, beliefService.setBeliefValue(agent, "B10", maxValue));
        }

        if (perception.getSubject().equals("bullying")) {
            addIfPresent(beliefUpdateLogs, beliefService.setBeliefValue(agent, "B10", minValue));
        }

//        if (desireService.getActiveGoal(agentId)== null){
//            desireService.
//        }

        switch (perceptionName) {
            case "request_chitchat_greeting":
            case "request_chitchat_faring":
                addIfPresent(beliefUpdateLogs, beliefService.increaseBeliefValue(agent, "B4", oneStep));
                if (agent.getKnowledgeFile().contains("benevolence") && beliefService.getBeliefValue(agent.getBeliefs(),"B4")< 0.8) {  // workaround, needs updating. added this so that when the scenario is benevolence, the agent will set the correct values in the interface
                    addIfPresent(beliefUpdateLogs, beliefService.setBeliefValue(agent, "B18", (float) (minThreshold-0.1)));
                    addIfPresent(beliefUpdateLogs, beliefService.setBeliefValue(agent, "B19", minThreshold));
                    addIfPresent(beliefUpdateLogs, beliefService.setBeliefValue(agent, "B20", minThreshold));
                    addIfPresent(beliefUpdateLogs, beliefService.setBeliefValue(agent, "B21", (float) (minThreshold-0.1)));
                    addIfPresent(beliefUpdateLogs, beliefService.setBeliefValue(agent, "B22", (float) (maxThreshold+0.11)));
                    addIfPresent(beliefUpdateLogs, beliefService.setBeliefValue(agent, "B23", (float) (midThreshold+0.01)));
                    addIfPresent(beliefUpdateLogs, beliefService.setBeliefValue(agent, "B24", (float) (minThreshold+0.01)));
                    addIfPresent(beliefUpdateLogs, beliefService.setBeliefValue(agent, "B25", (float) (midThreshold+0.01)));
                }

                // the next is for assign the active goal based on value
//                String ActiveGoal = calculateValuesAction(agent);
//                addIfPresent(beliefUpdateLogs, beliefService.setBeliefValue(agent, ActiveGoal, maxValue));// to change the active goal
//                if (ActiveGoal.equals("B27") || ActiveGoal.equals("B29")) {
//                    addIfPresent(beliefUpdateLogs, beliefService.setBeliefValue(agent, "B26", minValue));
//                    addIfPresent(beliefUpdateLogs, beliefService.setBeliefValue(agent, "B28", minValue));
//                }

                    break;
            case "request_chitchat_goodbye":
                addIfPresent(beliefUpdateLogs, beliefService.setBeliefValue(agent, "B15", maxValue));
                break;
            case "confirm_bullying_summary": //change??
                float hasTalkedAboutBullying = beliefService.getByAgentIdAndName(agent.getId(), "B9").getValue();
                System.out.println("summary belief: " + hasTalkedAboutBullying);
                parseConfirmToAck(perception, hasTalkedAboutBullying, minValue);
                break;
            case "ack_contactingkt_compliment": //add b2?
            case "inform_goal_help":
                addIfPresent(beliefUpdateLogs, beliefService.increaseBeliefValue(agent, "B7", oneStep));
                break;
            case "ack_bullying_empathize":
            case "ack_goal_empathize":
                addIfPresent(beliefUpdateLogs, beliefService.increaseBeliefValue(agent, "B5", oneStep));
                break;
            case "ack_goal_compliment":
            case "ack_confidant_compliment":
                Desire currentDesiress = desireService.getActiveGoal(agentId);
                if(agent.getKnowledgeFile().contains("good") && currentDesiress.getName().equals("D3")){
                    float hasGoodRelationWithKt = beliefService.getByAgentIdAndName(agent.getId(), "B4").getValue();
                    if (floatComparer.greaterThan(hasGoodRelationWithKt, midThreshold)) {
                        addIfPresent(beliefUpdateLogs, beliefService.increaseBeliefValue(agent, "B7", oneStep));
                        addIfPresent(beliefUpdateLogs, beliefService.decreaseBeliefValue(agent, "B8", twoSteps));
                        addIfPresent(beliefUpdateLogs, beliefService.setBeliefValue(agent, "B16", maxValue));
                    }
                    parseConfirmToAck(perception, hasGoodRelationWithKt, midThreshold);
                }
                else {
                    addIfPresent(beliefUpdateLogs, beliefService.increaseBeliefValue(agent, "B1", oneStep));
                    addIfPresent(beliefUpdateLogs, beliefService.increaseBeliefValue(agent, "B2", oneStep)); // added this
                }
                break;
            case "ack_unknown_compliment":
                addIfPresent(beliefUpdateLogs, beliefService.increaseBeliefValue(agent, "B2", oneStep));
                break;
            case "request_confidant_when": //added new
            case "request_confidant_where": //added new
            case "request_confidant_feeling": //added new
            case "request_confidant_how": //added new
            case "request_confidant_say": //added new
            case "request_chitchat_end": //added new
                addIfPresent(beliefUpdateLogs, beliefService.increaseBeliefValue(agent, "B2", oneStep));
                break;
            case "request_goal_dream":
                addIfPresent(beliefUpdateLogs, beliefService.setBeliefValue(agent, "B11", maxValue));
                break;
            case "request_goal_effect":
            case "request_goal_feeling":
            case "request_goal_howchild":
                addIfPresent(beliefUpdateLogs, beliefService.increaseBeliefValue(agent, "B1", oneStep));
                addIfPresent(beliefUpdateLogs, beliefService.increaseBeliefValue(agent, "B11", oneStep));
                break;
            case "confirm_goal_summary":
                float hasTalkedAboutGoal = beliefService.getByAgentIdAndName(agent.getId(), "B10").getValue();
                parseConfirmToAck(perception, hasTalkedAboutGoal, minValue);
                break;
            case "confirm_goal_collaborate":
                float hasGoodRelationWithKt = beliefService.getByAgentIdAndName(agent.getId(), "B4").getValue();
                if (floatComparer.greaterThan(hasGoodRelationWithKt, midThreshold)) {
                    addIfPresent(beliefUpdateLogs, beliefService.increaseBeliefValue(agent, "B7", oneStep));
                    addIfPresent(beliefUpdateLogs, beliefService.decreaseBeliefValue(agent, "B8", twoSteps));
                    addIfPresent(beliefUpdateLogs, beliefService.setBeliefValue(agent, "B16", maxValue));
                }
                parseConfirmToAck(perception, hasGoodRelationWithKt, midThreshold);

                break;
            case "inform_goal_negative":
            case "inform_goalhitstop_negative":
                float isCurrentlyTalkingAboutGoal = beliefService.getByAgentIdAndName(agentId, "B10").getValue();
                if (floatComparer.equalTo(isCurrentlyTalkingAboutGoal, maxValue)) {
                    // This is reset by the setting below:
                    // - beliefService.decreaseBeliefValue(agent, "B8", twoSteps);
                    addIfPresent(beliefUpdateLogs, beliefService.decreaseBeliefValue(agent, "B7", oneStep));
                    addIfPresent(beliefUpdateLogs, beliefService.setBeliefValue(agent, "B8", minValue));
                }
                break;
            case "inform_goal_positive":
            case "inform_goalhitstop_positive":
                Desire currentDesirePos = desireService.getActiveGoal(agentId);
                if(agent.getKnowledgeFile().contains("good") && currentDesirePos.getName().equals("D3")){
                    float hasGoodRelationWithKts = beliefService.getByAgentIdAndName(agent.getId(), "B4").getValue();
                    if (floatComparer.greaterThan(hasGoodRelationWithKts, midThreshold)) {
                        addIfPresent(beliefUpdateLogs, beliefService.increaseBeliefValue(agent, "B7", oneStep));
                        addIfPresent(beliefUpdateLogs, beliefService.decreaseBeliefValue(agent, "B8", twoSteps));
                        addIfPresent(beliefUpdateLogs, beliefService.setBeliefValue(agent, "B16", maxValue));
                    }
                    else if( currentDesirePos.getName().equals("D8") || currentDesirePos.getName().equals("D10")){
                        addIfPresent(beliefUpdateLogs, beliefService.increaseBeliefValue(agent, "B2", oneStep));
                        addIfPresent(beliefUpdateLogs, beliefService.increaseBeliefValue(agent, "B4", oneStep));
                    }
                    parseConfirmToAck(perception, hasGoodRelationWithKts, midThreshold);
                    break;
                }
                    if (floatComparer.equalTo(beliefService.getByAgentIdAndName(agentId, "B10").getValue(),
                            maxValue)) {
                        addIfPresent(beliefUpdateLogs, beliefService.setBeliefValue(agent, "B17", maxValue));
                        actionService.getActionById(agent.getCurrentAction()).setCompleted(true);
                    }
                break;
            case "request_confidant_who":
                addIfPresent(beliefUpdateLogs, beliefService.increaseBeliefValue(agent, "B20", oneStep));
                addIfPresent(beliefUpdateLogs, beliefService.increaseBeliefValue(agent, "B21", oneStep));
                addIfPresent(beliefUpdateLogs, beliefService.increaseBeliefValue(agent, "B24", oneStep));
                addIfPresent(beliefUpdateLogs, beliefService.increaseBeliefValue(agent, "B25", oneStep));
                addIfPresent(beliefUpdateLogs, beliefService.setBeliefValue(agent, "B12", maxValue));
                break;
            case "inform_confidant_help":
            case "inform_confidant_say":
                addIfPresent(beliefUpdateLogs, beliefService.setBeliefValue(agent, "B13", maxValue));
                Action currentAction = actionService.getActionById(agent.getCurrentAction());
                if (currentAction.getName().equals("A6") || currentAction.getName().equals("A7")) {
                    actionService.getActionById(agent.getCurrentAction()).setCompleted(true);
                    addIfPresent(beliefUpdateLogs, beliefService.increaseBeliefValue(agent, "B2", oneStep));
                }
                break;
            case "confirm_confidant_teacher":
                float confidantCanHelp = beliefService.getByAgentIdAndName(agentId, "B13").getValue();
                if (floatComparer.greaterThan(confidantCanHelp, midThreshold)) {
                    addIfPresent(beliefUpdateLogs, beliefService.setBeliefValue(agent, "B8", minValue));
                    addIfPresent(beliefUpdateLogs, beliefService.setBeliefValue(agent, "B16", maxValue));
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

                if (currentDesire != null) {
                    System.out.println(currentDesire.getName());
                }

                if (currentDesire != null && currentDesire.getName().equals("D6")) { // changed this to D6
                    perception.setAttribute("helpful");
                } else {
                    perception.setAttribute("negative");
                }
                break;
            case "confirm_chitchat_questions":
                perception.setType("ack");
                Desire currentDesires = desireService.getActiveGoal(agentId);

                if (currentDesires != null) {
                    System.out.println(currentDesires.getName());
                }

                if (currentDesires != null && currentDesires.getName().equals("D6")) { // changed this to D6
                    perception.setAttribute("noquestions");
                } else {
                    perception.setAttribute("unhelpful");
                }
                break;

            case "confirm_focusing_valuesPower":
            case "confirm_focusing_valuesBenev":
                perception.setType("ack");
                perception.setAttribute("positive");
                addIfPresent(beliefUpdateLogs, beliefService.increaseBeliefValue(agent, "B2", oneStep));
                addIfPresent(beliefUpdateLogs, beliefService.increaseBeliefValue(agent, "B5", oneStep));
                break;


            case "request_evoking_valuesPower":
            case "request_evoking_valuesBenev":
            case "request_evoking_misconceptionPower":
            case "request_evoking_misconceptionBenev":
                perception.setAttribute("misconception");
                addIfPresent(beliefUpdateLogs, beliefService.increaseBeliefValue(agent, "B18", oneStep));
                addIfPresent(beliefUpdateLogs, beliefService.increaseBeliefValue(agent, "B21", oneStep));
                addIfPresent(beliefUpdateLogs, beliefService.increaseBeliefValue(agent, "B22", oneStep));
                addIfPresent(beliefUpdateLogs, beliefService.increaseBeliefValue(agent, "B25", oneStep));

                // the next is for assign the active goal based on value
                String ActiveGoal = calculateValuesAction(agent);
                addIfPresent(beliefUpdateLogs, beliefService.setBeliefValue(agent, ActiveGoal, maxValue));// to change the active goal
                if (ActiveGoal.equals("B27") || ActiveGoal.equals("B29")) {
                    addIfPresent(beliefUpdateLogs, beliefService.setBeliefValue(agent, "B26", minValue));
                    addIfPresent(beliefUpdateLogs, beliefService.setBeliefValue(agent, "B28", minValue));
                }
                break;
            case "confirm_focusing_misconceptionPower":
            case "confirm_focusing_misconceptionBenev":
                perception.setType("ack");
                addIfPresent(beliefUpdateLogs, beliefService.increaseBeliefValue(agent, "B2", oneStep));
                addIfPresent(beliefUpdateLogs, beliefService.increaseBeliefValue(agent, "B5", oneStep));

                Desire currentDesire3 = desireService.getActiveGoal(agentId);
                if (currentDesire3 != null && (currentDesire3.getName().equals("D7") || currentDesire3.getName().equals("D9") )) { // to check if they have the goal of hitting back
                    perception.setAttribute("positive");
                    addIfPresent(beliefUpdateLogs, beliefService.increaseBeliefValue(agent, "B18", oneStep));
                    addIfPresent(beliefUpdateLogs, beliefService.increaseBeliefValue(agent, "B22", oneStep));
                } else {
                    perception.setAttribute("negative");
                }

                // the next is for assign the active goal based on value
                String ActiveGoal1 = calculateValuesAction(agent);
                addIfPresent(beliefUpdateLogs, beliefService.setBeliefValue(agent, ActiveGoal1, maxValue));// to change the active goal
                if (ActiveGoal1.equals("B27") || ActiveGoal1.equals("B29")) {
                    addIfPresent(beliefUpdateLogs, beliefService.setBeliefValue(agent, "B26", minValue));
                    addIfPresent(beliefUpdateLogs, beliefService.setBeliefValue(agent, "B28", minValue));
                }

                break;

            case "request_reflecting_misconceptionPower":
            case "request_reflecting_misconceptionBenev":
                perception.setAttribute("misconception");
                addIfPresent(beliefUpdateLogs, beliefService.decreaseBeliefValue(agent, "B18", twoSteps));
                addIfPresent(beliefUpdateLogs, beliefService.decreaseBeliefValue(agent, "B19", twoSteps));
                addIfPresent(beliefUpdateLogs, beliefService.decreaseBeliefValue(agent, "B22", twoSteps));
                addIfPresent(beliefUpdateLogs, beliefService.decreaseBeliefValue(agent, "B23", twoSteps));
                // the next is for assign the active goal based on value
                String ActiveGoal2 = calculateValuesAction(agent);
                addIfPresent(beliefUpdateLogs, beliefService.setBeliefValue(agent, ActiveGoal2, maxValue));// to change the active goal
                if (ActiveGoal2.equals("B27") || ActiveGoal2.equals("B29")) {
                    addIfPresent(beliefUpdateLogs, beliefService.setBeliefValue(agent, "B26", minValue));
                    addIfPresent(beliefUpdateLogs, beliefService.setBeliefValue(agent, "B28", minValue));
                }

                break;
            case "confirm_reflecting_misconceptionPower":
            case "confirm_reflecting_misconceptionBenev":
                perception.setType("ack");
                perception.setAttribute("positive");
                addIfPresent(beliefUpdateLogs, beliefService.decreaseBeliefValue(agent, "B19", twoSteps));
                addIfPresent(beliefUpdateLogs, beliefService.decreaseBeliefValue(agent, "B23", twoSteps));

                // the next is for assign the active goal based on value
                String ActiveGoal3 = calculateValuesAction(agent);
                addIfPresent(beliefUpdateLogs, beliefService.setBeliefValue(agent, ActiveGoal3, maxValue));// to change the active goal
                if (ActiveGoal3.equals("B27") || ActiveGoal3.equals("B29")) {
                    addIfPresent(beliefUpdateLogs, beliefService.setBeliefValue(agent, "B26", minValue));
                    addIfPresent(beliefUpdateLogs, beliefService.setBeliefValue(agent, "B28", minValue));
                }

                break;
            case "request_evoking_alternativePower":
            case "request_evoking_alternativeBenev":
                perception.setAttribute("alternative");
                addIfPresent(beliefUpdateLogs, beliefService.decreaseBeliefValue(agent, "B18", twoSteps));
                addIfPresent(beliefUpdateLogs, beliefService.decreaseBeliefValue(agent, "B22", twoSteps));

                addIfPresent(beliefUpdateLogs, beliefService.increaseBeliefValue(agent, "B20", oneStep));
                addIfPresent(beliefUpdateLogs, beliefService.increaseBeliefValue(agent, "B21", oneStep));
                addIfPresent(beliefUpdateLogs, beliefService.increaseBeliefValue(agent, "B24", oneStep));
                addIfPresent(beliefUpdateLogs, beliefService.increaseBeliefValue(agent, "B25", oneStep));

                // the next is for assign the active goal based on value
                String ActiveGoal4 = calculateValuesAction(agent);
                addIfPresent(beliefUpdateLogs, beliefService.setBeliefValue(agent, ActiveGoal4, maxValue));// to change the active goal
                if (ActiveGoal4.equals("B27") || ActiveGoal4.equals("B29")) {
                    addIfPresent(beliefUpdateLogs, beliefService.setBeliefValue(agent, "B26", minValue));
                    addIfPresent(beliefUpdateLogs, beliefService.setBeliefValue(agent, "B28", minValue));
                }

                break;
            case "request_reflecting_alternativePower":
            case "request_reflecting_alternativeBenev":
                perception.setAttribute("alternative");
                addIfPresent(beliefUpdateLogs, beliefService.decreaseBeliefValue(agent, "B19", twoSteps));
                addIfPresent(beliefUpdateLogs, beliefService.decreaseBeliefValue(agent, "B23", twoSteps));

                addIfPresent(beliefUpdateLogs, beliefService.increaseBeliefValue(agent, "B20", oneStep));
                addIfPresent(beliefUpdateLogs, beliefService.increaseBeliefValue(agent, "B24", oneStep));
                // the next is for assign the active goal based on value
                String ActiveGoal5 = calculateValuesAction(agent);
                addIfPresent(beliefUpdateLogs, beliefService.setBeliefValue(agent, ActiveGoal5, maxValue));// to change the active goal
                if (ActiveGoal5.equals("B27") || ActiveGoal5.equals("B29")) {
                    addIfPresent(beliefUpdateLogs, beliefService.setBeliefValue(agent, "B26", minValue));
                    addIfPresent(beliefUpdateLogs, beliefService.setBeliefValue(agent, "B28", minValue));
                }

                break;
            case "confirm_planning_alternativePower":
            case "confirm_planning_alternativeBenev":
                perception.setType("ack");
//                addIfPresent(beliefUpdateLogs, beliefService.increaseBeliefValue(agent, "B2", oneStep));
                addIfPresent(beliefUpdateLogs, beliefService.increaseBeliefValue(agent, "B5", oneStep));

                Desire currentDesire4 = desireService.getActiveGoal(agentId);
                if (currentDesire4 != null && (currentDesire4.getName().equals("D8") || currentDesire4.getName().equals("D10") )) { // to check if they have the goal of hitting back
                    perception.setAttribute("positive");
                    addIfPresent(beliefUpdateLogs, beliefService.increaseBeliefValue(agent, "B21", oneStep));
                    addIfPresent(beliefUpdateLogs, beliefService.increaseBeliefValue(agent, "B25", oneStep));
                } else {
                    perception.setAttribute("negative");
                }

                // the next is for assign the active goal based on value
                String ActiveGoal6 = calculateValuesAction(agent);
                addIfPresent(beliefUpdateLogs, beliefService.setBeliefValue(agent, ActiveGoal6, maxValue));// to change the active goal
                if (ActiveGoal6.equals("B27") || ActiveGoal6.equals("B29")) {
                    addIfPresent(beliefUpdateLogs, beliefService.setBeliefValue(agent, "B26", minValue));
                    addIfPresent(beliefUpdateLogs, beliefService.setBeliefValue(agent, "B28", minValue));
                }

                break;
            case "request_planning_alternativePower":
            case "request_planning_alternativeBenev":
                Desire currentDesire5 = desireService.getActiveGoal(agentId);
                if (currentDesire5 != null && (currentDesire5.getName().equals("D7") || currentDesire5.getName().equals("D9") )) { // to check if they have the goal of hitting back
                    perception.setAttribute("misconception");

                }
                else {
                    perception.setAttribute("alternative");

                    addIfPresent(beliefUpdateLogs, beliefService.increaseBeliefValue(agent, "B2", oneStep));
                    addIfPresent(beliefUpdateLogs, beliefService.increaseBeliefValue(agent, "B5", oneStep));
                    addIfPresent(beliefUpdateLogs, beliefService.increaseBeliefValue(agent, "B21", oneStep));
                    addIfPresent(beliefUpdateLogs, beliefService.increaseBeliefValue(agent, "B25", oneStep));
                }
                    // the next is for assign the active goal based on value
                    String ActiveGoal7 = calculateValuesAction(agent);
                    addIfPresent(beliefUpdateLogs, beliefService.setBeliefValue(agent, ActiveGoal7, maxValue));// to change the active goal
                    if (ActiveGoal7.equals("B27") || ActiveGoal7.equals("B29")) {
                        addIfPresent(beliefUpdateLogs, beliefService.setBeliefValue(agent, "B26", minValue));
                        addIfPresent(beliefUpdateLogs, beliefService.setBeliefValue(agent, "B28", minValue));
                    }



                break;


            case "request_planning_feelingPower":
            case "request_planning_feelingBenev":
                perception.setAttribute("feeling");

                addIfPresent(beliefUpdateLogs, beliefService.increaseBeliefValue(agent, "B2", oneStep));
                addIfPresent(beliefUpdateLogs, beliefService.increaseBeliefValue(agent, "B5", oneStep));
                addIfPresent(beliefUpdateLogs, beliefService.increaseBeliefValue(agent, "B21", oneStep));
                addIfPresent(beliefUpdateLogs, beliefService.increaseBeliefValue(agent, "B25", oneStep));

                // the next is for assign the active goal based on value
                String ActiveGoal8 = calculateValuesAction(agent);
                addIfPresent(beliefUpdateLogs, beliefService.setBeliefValue(agent, ActiveGoal8, maxValue));// to change the active goal
                if (ActiveGoal8.equals("B27") || ActiveGoal8.equals("B29")) {
                    addIfPresent(beliefUpdateLogs, beliefService.setBeliefValue(agent, "B26", minValue));
                    addIfPresent(beliefUpdateLogs, beliefService.setBeliefValue(agent, "B28", minValue));
                }

                break;

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
    private void updateDesires(Agent agent) {

        List<Desire> desires = desireService.getByAgent(agent.getId());

        for (Desire desire : desires) {
            boolean currentActiveValue = updateActiveValue(agent.getId(), desire.getName());
            if (desire.isActive() != currentActiveValue) {
                logEntryService.addLogEntry(
                        new DesireUpdateLogEntry(currentActiveValue, DesireName.valueOf(desire.getName()), agent));
            }
            desire.setActiveValue(currentActiveValue);
            desireService.addDesire(desire);
        }

        Desire intention = desireService.getActiveGoal(agent.getId());

        if (intention != null) {
            agent.setIntentionId(intention.getId());
        }

        agentRepository.save(agent);
    }


    /**
     * Gets the response of the agent based on the agent's desires. If the agent's desire is to end the conversation,
     * the function will return goodbye and the agent will be set to inactive.
     *
     * @param agent the agent
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
            return getResponseFromKnowledge(agent.getKnowledgeFile(), "chitchat", "goodbye");
        }

        switch (perception.getType()) {
            case "request" -> {
                return getResponseFromKnowledge(agent.getKnowledgeFile(), perception.getSubject(), perception.getAttribute());
            }
            case "ack" -> {
                return respondToAck(agent, perception);
            }
            case "inform" -> {
                if (perception.getAttribute().equals("negative")) {
                    return getResponseFromKnowledge(agent.getKnowledgeFile(), "ack", "unhelpful");
                }
                return getResponseFromKnowledge(agent.getKnowledgeFile(), "ack", "neutral");
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
     * @param agent the agent
     * @param perception the perception of the agent
     * @return response of the agent
     */
    public String reason(Agent agent, Perception perception) {
        String response;
        List<BeliefUpdateLogEntry> beliefUpdateLogs = new ArrayList<>();

        //Send advice and perception to give advice.
        String perceptionName = perception.getType() + "_" + perception.getSubject() + "_" + perception.getAttribute();
        sendIntent(agent.getUserId(), agent, perceptionName);



        if (perception.getType().equals("trigger") || (perception.getAttribute().equals("details") && desireService.getById(agent.getIntentionId()).getName().equals("D1") ) ) { //if they asked about what they want to talk about (condition 1) or what happened (condition 2).
            Desire intention = desireService.getById(agent.getIntentionId());

            if (intention.getName().equals("D1")) {
                agent.setCurrentSubject("bullying");
                addIfPresent(beliefUpdateLogs, beliefService.increaseBeliefValue(agent, "B9", oneStep));
            }
            response = saySomething(agent, intention);

            if(agent.getKnowledgeFile().contains("good") || !intention.getName().equals("D5")) {//check if knowledge file contains good idea about solution.
                setLogCauses(beliefUpdateLogs, perception.getText());

                addLogs(beliefUpdateLogs, agent);

                beliefService.sendBeliefsToClientAndLog(agent, beliefUpdateLogs);

                return response;
            }
            else{
                return null;
            }
        }

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

        return response;
    }

    /**
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
    public Agent createAgent(String userId, String username) {
        Agent agent = new Agent();
        agent.setUserId(userId);
        agent.isActive(true);
        if (username.equals("testaccount")) { //if the account is test account, then only activate the most stable scenario
            agent.setKnowledgeFile("knowledge_Lilo_benevolence.csv");
        }
        else {
            agent.setKnowledgeFile(knowledgeService.getKnowledge(userId, username));
        }
//        if(agent.getKnowledgeFile().contains("good")) {
//            System.out.print("it workssss");
//            beliefService.setBeliefValue(agent, "B16", maxValue);
//        }
        // maybe check if they have goal, then increase b16 to 1
        // addIfPresent(beliefUpdateLogs, beliefService.setBeliefValue(agent, "B16", maxValue));
        Set<Belief> initialBeliefs;
        if(agent.getKnowledgeFile().contains("power")) {
            initialBeliefs = beliefService.readBeliefsFromCsv(agent, 1);

        }
        else {
            initialBeliefs = beliefService.readBeliefsFromCsv(agent, 2);
//            System.out.println("agent2");
        }
        beliefService.addBeliefs(initialBeliefs); // update this as well, look at how we did it downstairs
        agent.setBeliefs(initialBeliefs);

        Set<Desire> initialDesires = desireService.readDesiresFromCsv(agent);
        desireService.addDesires(initialDesires);
        agent.getDesires().addAll(initialDesires);

//        agent.getDesires().addAll(initialHValues); ///update this

        updateDesires(agent);
        sendPhaseOfAgent(agent.getPhase(), updatePhaseOfAgent(agent), true, agent.getUserId());

        Set<HumanValues> initialHValues = hValuesService.readHumanValuesFromCsv(agent);
        if(agent.getKnowledgeFile().contains("power")) { //Update later with the actual files, e.g., is it the one with power achievement or the one with  benevolence care for others
        hValuesService.addHumanValue(initialHValues,1);}
        else if (agent.getKnowledgeFile().contains("benevolence")){
            hValuesService.addHumanValue(initialHValues, 2);
        }
//        agent.getHumanValues().addAll(initialHValues);
//        agent.getHumanValues().addAll(initialHValues);


        Set<Conviction> initialConviction = convictionService.readConvictionsFromCsv(agent);
        if(agent.getKnowledgeFile().contains("power")) { //Update later with the actual files, e.g., is it the one with power achievement or the one with  benevolence care for others
            convictionService.addConviction(initialConviction,1);}
        else if (agent.getKnowledgeFile().contains("benevolence")){
            convictionService.addConviction(initialConviction, 2);
        }


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
            case "D6" -> checkDesireConstraints(agentId, DesireName.D6);
            case "D7" -> checkDesireConstraints(agentId, DesireName.D7);
            case "D8" -> checkDesireConstraints(agentId, DesireName.D8);
            case "D9" -> checkDesireConstraints(agentId, DesireName.D9);
            case "D10" -> checkDesireConstraints(agentId, DesireName.D10);
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




    /**
     * Calculates which desire to adapt (linked to a flag belief). 4 equations are computed where
     * each corresponds to a different goal the agent will adapt (hitting back, walking away, telling
     * someone, not telling anyone). This should only be used with the human values scenarios.
     *
     * @param agent the agent
     */
    private String calculateValuesAction(Agent agent) {
        double hitBackScore = beliefService.getBeliefValue(agent.getBeliefs(),"B18") *
        convictionService.getConvictionValue(agent.getConvictions(),"C1") *
        hValuesService.gethumanValueValue(agent.getHumanValues(),"V1") +
                beliefService.getBeliefValue(agent.getBeliefs(),"B19") *
                        convictionService.getConvictionValue(agent.getConvictions(),"C2") *
                        hValuesService.gethumanValueValue(agent.getHumanValues(),"V3");
        //if highest, activate B26
        double walkAwayScore = beliefService.getBeliefValue(agent.getBeliefs(),"B20") *
                convictionService.getConvictionValue(agent.getConvictions(),"C3") *
                hValuesService.gethumanValueValue(agent.getHumanValues(),"V3") +
                beliefService.getBeliefValue(agent.getBeliefs(),"B21") *
                        convictionService.getConvictionValue(agent.getConvictions(),"C4") *
                        hValuesService.gethumanValueValue(agent.getHumanValues(),"V1");
        //if highest, activate B27

        double dontTellScore = beliefService.getBeliefValue(agent.getBeliefs(),"B22") *
                convictionService.getConvictionValue(agent.getConvictions(),"C5") *
                hValuesService.gethumanValueValue(agent.getHumanValues(),"V1") +
                beliefService.getBeliefValue(agent.getBeliefs(),"B23") *
                        convictionService.getConvictionValue(agent.getConvictions(),"C6") *
                        hValuesService.gethumanValueValue(agent.getHumanValues(),"V3");
        //if highest, activate B28
        double tellSomeoneScore = beliefService.getBeliefValue(agent.getBeliefs(),"B24") *
                convictionService.getConvictionValue(agent.getConvictions(),"C7") *
                hValuesService.gethumanValueValue(agent.getHumanValues(),"V3") +
                beliefService.getBeliefValue(agent.getBeliefs(),"B25") *
                        convictionService.getConvictionValue(agent.getConvictions(),"C8") *
                        hValuesService.gethumanValueValue(agent.getHumanValues(),"V1");
        //if highest, activate B29

        // Determine the highest score
        double maxScore = Math.max(Math.max(hitBackScore, walkAwayScore), Math.max(dontTellScore, tellSomeoneScore));

        if (maxScore == hitBackScore) {
            return "B26";
        } else if (maxScore == walkAwayScore) {
            return "B27";
        } else if (maxScore == dontTellScore) {
            return "B28";
        } else {
            return "B29";
        }

    }




}
