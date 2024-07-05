package com.bdi.agent.api;

import com.bdi.agent.model.Agent;
import com.bdi.agent.model.Conversation;
import com.bdi.agent.model.Desire;
import com.bdi.agent.model.Perception;
import com.bdi.agent.model.Scenario;
import com.bdi.agent.model.User;
import com.bdi.agent.model.api.BeliefChangeClientModel;
import com.bdi.agent.model.api.MessageModel;
import com.bdi.agent.model.util.MessageLogEntry;
import com.bdi.agent.service.AgentService;
import com.bdi.agent.service.LogEntryService;
import com.bdi.agent.service.ScenarioService;
import com.bdi.agent.service.UserService;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * The Agent Controller.
 */
@CrossOrigin(origins = "*")
@RestController
public class AgentController {

    private final AgentService agentService;
    private final LogEntryService logEntryService;
    private final UserService userService;
    private final ScenarioService scenarioService;

    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    /**
     * Instantiates a new Agent Controller.
     *
     * @param agentService    the agentService for accessing agents.
     * @param logEntryService the logEntryService for accessing logs.
     * @param userService     the userService for accessing users.
     * @param scenarioService the scenarioService for accessing scenarios.
     */
    @Autowired
    public AgentController(AgentService agentService, LogEntryService logEntryService, UserService userService,
            ScenarioService scenarioService) {
        this.agentService = agentService;
        this.logEntryService = logEntryService;
        this.userService = userService;
        this.scenarioService = scenarioService;
    }

    /**
     * Adds a perception to the agent and returns the chatbot's reply.
     * Causes the agent to reason and update its beliefs.
     * The perception is sent from rasa.
     *
     * @param userId     the user id connected to the agent
     * @param perception the perception (intent parsed by Rasa and message from user)
     * @return the chatbot's reply
     */
    @PostMapping(path = "/agent/{userId}")
    public ResponseEntity<String> addPerception(@PathVariable("userId") String userId,
            @RequestBody Perception perception) {
        if (!agentService.containsUserId(userId)) {
            agentService.createAgent(userId);
        }

        Agent agent = agentService.getByUserId(userId);

        if (!agent.isActive()) {
            return new ResponseEntity<>(null, HttpStatus.OK);
        }

        String response = agentService.reason(agent, perception);

        if (response == null) {
            return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
        }

        if (!agent.isTrainerResponding()) {
            Desire intention = agentService.getIntention(agent.getId());
            logEntryService.addLogEntry(new MessageLogEntry(response, false,
                    LocalDateTime.now().plusSeconds((long) (Math.max(Math.min(response.length() * 50, 3000),
                            1000) * 0.001)), agent, intention.getFullName()));
        }

        // Send response to user via websocket
        // If the trainer is intercepting messages, send the response to the trainer instead
        String path = agentService.isTrainerResponding(userId) ? "/topic/trainer/"
                + userId : "/topic/session/" + userId;
        messagingTemplate.convertAndSend(path, List.of(new MessageModel(response, false)));

        // Send response to Rasa
        return new ResponseEntity<>(response, HttpStatus.OK);
    }


    /**
     * Gets the transcript of the conversation between the user and the agent.
     *
     * @param userId the user id connected to the agent
     * @return the transcript of the conversation
     */
    @GetMapping(path = "/report/{userId}")
    public ResponseEntity<String> getReport(@PathVariable("userId") String userId) {
        System.out.println("get BDI report for user :" + userId);
        String response;

        if (!agentService.containsUserId(userId)) {
            return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
        }

        Agent agent = agentService.getByUserId(userId);
        response = agentService.getReport(agent);

        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    /**
     * Gets the conversation between the user and the agent as a list of MessageModel objects.
     * Can be used at any point in the conversation to retrieve the conversation when the trainer joins the session.
     *
     * @param sessionId the user id connected to the agent
     * @return list of MessageModel objects representing the conversation so far
     */
    @CrossOrigin(origins = {"http://${server.web}"})
    @GetMapping(path = "/conversation/{sessionId}")
    public ResponseEntity<String> getConversation(@PathVariable("sessionId") String sessionId) {

        if (!agentService.containsUserId(sessionId)) {
            return new ResponseEntity<>("Session not found", HttpStatus.NOT_FOUND);
        }

        List<MessageModel> response = agentService.getConversation(sessionId);

        return new ResponseEntity(response, HttpStatus.OK);
    }

    /**
     * Gets the past transitions of the agent as a list of BeliefChangeClientModel objects.
     * Can be used at any point in the conversation to retrieve the past transitions when the trainer joins the session.
     *
     * @param userId the user id connected to the agent
     * @return list of MessageModel objects representing the conversation so far
     */
    @CrossOrigin(origins = {"http://${server.web}"})
    @GetMapping(path = "/transitions/{userId}")
    public ResponseEntity<List<BeliefChangeClientModel>> getPastTransitions(@PathVariable("userId") String userId) {
        if (!agentService.containsUserId(userId)) {
            return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
        }

        List<BeliefChangeClientModel> response = agentService.getPastTransitions(userId);

        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    /**
     * This endpoint is used to set the active scenario for the agent.
     *
     * @param userId       the user id connected to the agent
     * @param scenarioName the name of the scenario
     * @return the name of the scenario
     */
    @CrossOrigin(origins = "http://localhost:5601/")
    @PutMapping(path = "/agent/scenario/{userId}/{scenarioName}")
    public ResponseEntity<String> setActiveScenario(@PathVariable("userId") String userId,
            @PathVariable("scenarioName") String scenarioName) {
        if (!agentService.containsUserId(userId)) {
            this.startSession(userId, null);
        }

        Scenario scenario = scenarioService.getScenarioByNameFromJson(scenarioName);
        if (scenario == null) {
            return new ResponseEntity<>("Scenario not found", HttpStatus.NOT_FOUND);
        }

        Agent agent = agentService.getByUserId(userId);
        agent.setScenario(scenario);
        agentService.save(agent);

        return new ResponseEntity<>(agent.getScenario().getName(), HttpStatus.OK);
    }

    /**
     * This endpoint is used to get the active scenario for the agent.
     *
     * @param userId the user id connected to the agent
     * @return the name of the scenario
     */
    @CrossOrigin(origins = "http://localhost:5601/")
    @GetMapping(path = "/agent/scenario/{userId}")
    public ResponseEntity<String> getActiveScenario(@PathVariable("userId") String userId) {
        if (!agentService.containsUserId(userId)) {
            startSession(userId, null);
        }

        Agent agent = agentService.getByUserId(userId);
        if (agent.getScenario() == null) {
            return new ResponseEntity<>("No active scenario", HttpStatus.NOT_FOUND);
        }

        return new ResponseEntity<>(agent.getScenario().getName(), HttpStatus.OK);
    }

    /**
     * This endpoint is used to change the agent mode from manual to automatic and vice versa.
     *
     * @param isTrainerResponding whether the trainer is manually sending messages
     * @param sessionId           the session id of the session/agent
     */
    @CrossOrigin(origins = "http://localhost:5601/")
    @PostMapping(path = "/agent/changeMode/{sessionId}")
    public ResponseEntity<String> changeAgentMode(@PathVariable("sessionId") String sessionId,
            @RequestBody boolean isTrainerResponding) {
        try {
            agentService.setTrainerResponding(sessionId, isTrainerResponding);
        } catch (Exception e) {
            return new ResponseEntity<>("Session not found", HttpStatus.NOT_FOUND);
        }
        System.out.println(sessionId + "Agent trainer mode changed to " + isTrainerResponding);
        return new ResponseEntity<>("Agent trainer mode changed to " + isTrainerResponding, HttpStatus.OK);
    }

    /**
     * Creates a new agent with the given session id if it doesn't already exist. If the new agent was made by a valid
     * user of the application, a new Conversation object is created that binds to the agent and the owning user. This
     * Conversation represents the chat as well as some metadata that allows users to access their own past chats on
     * the frontend.
     *
     * @param sessionId the session id
     * @return HTTP OK
     */
    @CrossOrigin(origins = {"http://${server.web}"})
    @PostMapping(path = "/create/{sessionId}")
    public ResponseEntity<String> startSession(@PathVariable("sessionId") String sessionId,
            @RequestParam(required = false) String username) {
        Agent agent = (agentService.containsUserId(sessionId))
                ? (agentService.getByUserId(sessionId)) : (agentService.createAgent(sessionId));

        if (username != null && userService.containsUsername(username)) {
            User user = userService.getByUsername(username);

            userService.addConversation(user, new Conversation("Chat with Lilobot "
                    + user.getConversationNumber(), LocalDateTime.now(), agent, user));
        }

        return new ResponseEntity<>("Session has been created", HttpStatus.OK);
    }

}
