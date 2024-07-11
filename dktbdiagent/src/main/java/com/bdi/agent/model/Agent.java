package com.bdi.agent.model;

<<<<<<< HEAD
=======
import com.bdi.agent.model.enums.BeliefUpdateType;
import com.bdi.agent.model.enums.Phase;
import com.bdi.agent.model.util.LogEntry;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

>>>>>>> origin/updatedLilo
import javax.persistence.*;
import java.util.*;

@Entity
@Table
<<<<<<< HEAD
=======
@AllArgsConstructor
@Getter
@Setter
>>>>>>> origin/updatedLilo
public class Agent {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
<<<<<<< HEAD
=======
    @Setter(AccessLevel.NONE)
>>>>>>> origin/updatedLilo
    private Long id;

    @Column(unique = true)
    private String userId;  //conversation id from Rasa tracker

<<<<<<< HEAD
    @OneToMany(mappedBy="agent")
    private Set<Belief> beliefs = new HashSet<>();

    @OneToMany(mappedBy="agent")
    private Set<Desire> desires = new HashSet<>();

    private Long intentionId;

    public String currentSubject;

    public Boolean active;
=======
    @OneToMany(mappedBy="agent", cascade = CascadeType.ALL)
    private Set<Belief> beliefs;

    @OneToMany(mappedBy="agent", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<Desire> desires;

    @Enumerated(EnumType.STRING)
    private Phase phase;

    private Long intentionId;

    // This value should not be used in the parsing of perceptions to influence the response. It is to prevent
    // NullPointerExceptions on the handling of unknown subjects.
    public String currentSubject = "DEFAULT_SUBJECT";

    @Accessors(fluent = true)
    public Boolean isActive;
>>>>>>> origin/updatedLilo

    public Long currentAction;

    public float score;

<<<<<<< HEAD
    @ElementCollection
    @CollectionTable(name = "log", joinColumns = @JoinColumn(name = "id"))
    @Column(name = "log")
    @OrderColumn(name = "order_idx")
    private List<String> log = new ArrayList<>();

    public Agent() {

    }

    public Agent(String userId) {
        this.userId = userId;
    }

    public Long getId() {
        return id;
    }

    public Boolean isActive() {
        return active;
    }

    public void setActive(Boolean active) {
        this.active = active;
    }

    public String getUserId() {
        return userId;
    }

=======
    @OneToMany(mappedBy = "agent", cascade = CascadeType.ALL)
    private List<LogEntry> logEntries = new ArrayList<>();

    // Flag used to indicate if the trainer is sending the responses
    @Accessors(fluent = true)
    private Boolean isTrainerResponding;

    @OneToOne(mappedBy="agent", cascade = CascadeType.ALL)
    private Conversation conversation;

    public Agent() {
        this.desires = new HashSet<>();
        this.beliefs = new HashSet<>();
        this.isTrainerResponding = false;
    }

    public Agent(String userId) {
        this.desires = new HashSet<>();
        this.beliefs = new HashSet<>();
        this.isTrainerResponding = false;
        this.userId = userId;
    }

>>>>>>> origin/updatedLilo
    public void setUser(String userId) {
        this.userId = userId;
    }

<<<<<<< HEAD
    public String getCurrentSubject() {
        return currentSubject;
    }

    public void setCurrentSubject(String currentSubject) {
        this.currentSubject = currentSubject;
    }

    public void setDesires(Set<Desire> desires) {
        this.desires = desires;
    }

    public Set<Belief> getBeliefs() {
        return beliefs;
    }

    public void setBeliefs(Set<Belief> beliefs) {
        this.beliefs = beliefs;
    }

    public Long getIntention() {
        return intentionId;
    }

    public void setIntention(Long intentionId) {
        this.intentionId = intentionId;
    }

    public List<String> getLog() {
        return log;
    }

    public void addLog(String chat) {
        this.log.add(chat);
    }

    public Long getCurrentAction() {
        return currentAction;
    }

    public void setCurrentAction(Long currentAction) {
        this.currentAction = currentAction;
    }

    public void setScore(float score) {
        this.score = score;
    }

=======
>>>>>>> origin/updatedLilo
}
