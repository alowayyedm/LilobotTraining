package com.bdi.agent.model;

import com.bdi.agent.model.enums.Phase;
import com.bdi.agent.model.util.LogEntry;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@Entity
@Table
@AllArgsConstructor
@Getter
@Setter
public class Agent {

    // This value should not be used in the parsing of perceptions to influence the response. It is to prevent
    // NullPointerExceptions on the handling of unknown subjects.
    public String currentSubject = "DEFAULT_SUBJECT";
    @Accessors(fluent = true)
    public Boolean isActive;
    public Long currentAction;
    public float score;

    //    @OneToMany(mappedBy = "agent", cascade = CascadeType.ALL)
    //    private Set<Belief> beliefs;
    //
    //    @OneToMany(mappedBy = "agent", cascade = CascadeType.ALL, orphanRemoval = true)
    //    private Set<Desire> desires;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Setter(AccessLevel.NONE)
    private Long id;
    @Column(unique = true)
    private String userId;  //conversation id from Rasa tracker
    private String knowledgeFile;
    @OneToOne(cascade = CascadeType.ALL)
    private Scenario scenario;
    @Enumerated(EnumType.STRING)
    private Phase phase;
    private Long intentionId;
    @OneToMany(mappedBy = "agent", cascade = CascadeType.ALL)
    private List<LogEntry> logEntries = new ArrayList<>();

    // Flag used to indicate if the trainer is sending the responses
    @Accessors(fluent = true)
    private Boolean isTrainerResponding;

    @OneToOne(mappedBy = "agent", cascade = CascadeType.ALL)
    private Conversation conversation;

    /**
     * Construct a new Agent class.
     */
    public Agent() {
        this.isTrainerResponding = false;
        this.scenario = new Scenario();
    }

    /**
     * Construct a new Agent class from a given user id.
     *
     * @param userId The user id to create from
     */
    public Agent(String userId) {
        this.isTrainerResponding = false;
        this.userId = userId;
    }

    /**
     * Construct new agent with an id.
     *
     * @param id The id of the agent
     */
    public Agent(Long id) {
        this.id = id;
    }

    /**
     * Construct a new agent.
     *
     * @param id The id of the agent
     * @param userId The user id the agent belongs too
     * @param knowledgeFile The knowledge file the agent uses
     * @param phase The phase the agent is in
     * @param intentionId The current intention id of the agent
     * @param currentSubject The current subject of the agent
     * @param isActive If the agent is active right now
     * @param currentAction The current action the agent is performing
     * @param score The score of the agent
     * @param logEntries The log entries of the agent
     * @param isTrainerResponding If the trainer is responding right now
     * @param conversation The conversation of the agent
     */
    public Agent(Long id, String userId, String knowledgeFile, Phase phase,
                 Long intentionId, String currentSubject, Boolean isActive, Long currentAction, float score,
                 List<LogEntry> logEntries, Boolean isTrainerResponding, Conversation conversation) {
        this.id = id;
        this.userId = userId;
        this.knowledgeFile = knowledgeFile;
        this.phase = phase;
        this.intentionId = intentionId;
        this.currentSubject = currentSubject;
        this.isActive = isActive;
        this.currentAction = currentAction;
        this.score = score;
        this.logEntries = logEntries;
        this.isTrainerResponding = isTrainerResponding;
        this.conversation = conversation;
    }

    public void setUser(String userId) {
        this.userId = userId;
    }

    @Override
    public String toString() {
        return "Agent{"
                + "id=" + id
                + ", userId='" + userId + '\''
                + ", knowledgeFile='" + knowledgeFile + '\''
                + ", scenario=" + scenario
                + ", phase=" + phase
                + ", intentionId=" + intentionId
                + ", currentSubject='" + currentSubject + '\''
                + ", isActive=" + isActive
                + ", currentAction=" + currentAction
                + ", score=" + score
                + ", logEntries=" + logEntries
                + ", isTrainerResponding=" + isTrainerResponding
                + ", conversation=" + conversation
                + '}';
    }
}
