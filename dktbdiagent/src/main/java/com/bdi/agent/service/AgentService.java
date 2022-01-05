package com.bdi.agent.service;

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
public class AgentService {

    private final AgentRepository agentRepository;

    private final BeliefService beliefService;
    private final DesireService desireService;
    private final ActionService actionService;
    private final ReportService reportService;
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
        this.agentRepository = agentRepository;
        this.beliefService = beliefService;
        this.desireService = desireService;
        this.actionService = actionService;
        this.reportService = reportService;
        this.knowledgeService = knowledgeService;

        knowledgeService.initializeKnowledge();
    }

    public boolean containsUserId(String userId) {
        return agentRepository.existsByUserId(userId);
    }

    public Agent getByUserId(String userId) {
        return agentRepository.getByUserId(userId);
    }

    public List<Agent> getAll() {
        return agentRepository.findAll();
    }

    private String resolvePerceptionSubject(Agent agent) throws NullPointerException {
        return agent.getCurrentSubject();
    }


    private void parsePerception(Agent agent, Perception perception) {
        try {
             perception.setSubject(resolvePerceptionSubject(agent));
        } catch (NullPointerException e) {
            System.err.println("parsePerception: cannot resolve perception subject");
            perception.setSubject("");
        }
    }

    private String respondToAck(Perception perception) {
        String type = "ack";
        String attribute = perception.getAttribute();

        return getResponseFromKnowledge(type, attribute);
    }

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


    private String saySomething(Agent agent, Desire desire) {

        try {
            Action action = actionService.getUncompletedAction(desire.getId());
            agent.setCurrentAction(action.getId());

            if (action.getType().equals("inform")) {
                action.setIsCompleted(true);
            }
            actionService.addAction(action);

            return getResponseFromKnowledge(action.getSubject(), action.getAttribute());

        } catch (NullPointerException e) {
            System.err.println("saySomething: could not get action");
        }

        return null;
    }

    private void parseConfirmToAck(Perception perception, float currentValue, float threshold) {
        perception.setType("ack");

        if (currentValue > threshold) {
            perception.setAttribute("positive");
        } else {
            perception.setAttribute("negative");
        }
    }

    private void updateBeliefs(Agent agent, Perception perception) {
        Long agentId = agent.getId();
        String perceptionName = perception.getType() + "_" + perception.getSubject() + "_" + perception.getAttribute();

        if (perception.getSubject().equals("goal")) {
            beliefService.setBeliefValue(agent, "B10", maxValue);
        }

        if (perception.getSubject().equals("bullying")) {
            beliefService.setBeliefValue(agent, "B10", minValue);
        }

        switch (perceptionName) {
            case "request_chitchat_greeting":
            case "request_chitchat_faring":
                beliefService.increaseBeliefValue(agent, "B4", oneStep);
                break;
            case "request_chitchat_goodbye":
                beliefService.setBeliefValue(agent, "B15", maxValue);
                break;
            case "confirm_bullying_summary":
                float hasTalkedAboutBullying = beliefService.getByAgentIdAndName(agent.getId(), "B9").getValue();
                System.out.println("summary belief: " + hasTalkedAboutBullying);
                parseConfirmToAck(perception, hasTalkedAboutBullying, minValue);
                break;
            case "ack_contactingkt_compliment":
            case "inform_goal_help":
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
                break;
            case "request_goal_effect":
            case "request_goal_feeling":
            case "request_goal_howchild":
                beliefService.increaseBeliefValue(agent, "B1", oneStep);
                beliefService.increaseBeliefValue(agent, "B11", oneStep);
                break;
            case "confirm_goal_summary":
                float hasTalkedAboutGoal = beliefService.getByAgentIdAndName(agent.getId(), "B10").getValue();
                parseConfirmToAck(perception, hasTalkedAboutGoal, minValue);
                break;
            case "confirm_goal_collaborate":
                float hasGoodRelationWithKt = beliefService.getByAgentIdAndName(agent.getId(), "B4").getValue();
                if (hasGoodRelationWithKt> midThreshold) {
                    beliefService.increaseBeliefValue(agent, "B7", oneStep);
                    beliefService.decreaseBeliefValue(agent, "B8", twoSteps);
                    beliefService.setBeliefValue(agent, "B16", maxValue);
                }
                parseConfirmToAck(perception, hasGoodRelationWithKt, midThreshold);

                break;
            case "inform_goal_negative":
                float isCurrentlyTalkingAboutGoal = beliefService.getByAgentIdAndName(agentId, "B10").getValue();
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
                }
                break;
            case "confirm_confidant_teacher":
                float confidantCanHelp = beliefService.getByAgentIdAndName(agentId, "B13").getValue();
                if (confidantCanHelp > midThreshold) {
                    beliefService.setBeliefValue(agent, "B8", minValue);
                    beliefService.setBeliefValue(agent, "B16", maxValue);
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
                System.out.println(currentDesire.getName());
                if (currentDesire.getName().equals("D4")) {
                    perception.setAttribute("helpful");
                } else {
                    perception.setAttribute("negative");
                }
                break;

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


    private void updateDesires(Agent agent) {

        List<Desire> desires = desireService.getByAgent(agent.getId());

        for (Desire desire : desires) {
            boolean currentActiveValue = updateActiveValue(agent.getId(), desire.getName());
            desire.setActiveValue(currentActiveValue);
            desireService.addDesire(desire);
        }

        Desire intention = desireService.getActiveGoal(agent.getId());
        agent.setIntention(intention.getId());
        agentRepository.save(agent);
    }


    private String updateIntentions(Agent agent, Perception perception) {
        agent.setCurrentSubject(perception.getSubject());
        agentRepository.save(agent);

        Desire intention = desireService.getById(agent.getIntention());

        agent.addLog(String.format("%-20s %s", "Intentie:", intention.getFullName()));
        System.out.printf("%-20s %s%n", "Intentie:", intention.getFullName());

        if (intention.getName().equals("D2")) { //if the agent's desire to end the conversation, just return goodbye
            agent.setActive(false);
            agentRepository.save(agent);
            return getResponseFromKnowledge("chitchat", "goodbye");
        }

        switch (perception.getType()) {
            case "request":
                return getResponseFromKnowledge(perception.getSubject(), perception.getAttribute());
            case "ack":
                return respondToAck(perception);
            case "inform":
                if (perception.getAttribute().equals("negative")) {
                    return getResponseFromKnowledge("ack", "unhelpful");
                }
                return getResponseFromKnowledge("ack", "neutral");
        }

        return null;
    }

    /**
     * Main method of this class. The function first parses the perception, resolving any missing values.
     * Then, it checks the type of perception. If it's a trigger, the beliefs and desires are not updated.
     * The agent just returns an action associated with the current intention.
     * Otherwise, it updates the agent's beliefs and desires and returns a response based on these.
     * @return response
     */
    public String reason(Agent agent, Perception perception) {
        String response;


        if (perception.getType().equals("trigger")) {
            Desire intention = desireService.getById(agent.getIntention());

            if (intention.getName().equals("D1")) {
                agent.setCurrentSubject("bullying");
                beliefService.increaseBeliefValue(agent, "B9", oneStep);
            }

            response = saySomething(agent, intention);

            if (response != null) {
                agent.addLog(liloPrefix.concat(response));
                agentRepository.save(agent);
            }

            return response;
        }

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

        return response;
    }

    /**
     * This function creates a new agent using the user or session id provided by Rasa.
     * It populates the beliefs and desires of the agent with the initial values found in the .csv files provided.
     *
     */
    public void createAgent(String userId) {

        Agent agent = new Agent();
        agent.setUser(userId);
        agent.setActive(true);

        Set<Belief> initialBeliefs = beliefService.readBeliefsFromCsv(agent);
        beliefService.addBeliefs(initialBeliefs);
        agent.setBeliefs(initialBeliefs);

        Set<Desire> initialDesires = desireService.readDesiresFromCsv(agent);
        desireService.addDesires(initialDesires);
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



}
