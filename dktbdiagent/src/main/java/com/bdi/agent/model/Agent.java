package com.bdi.agent.model;

import javax.persistence.*;
import java.util.*;

@Entity
@Table
public class Agent {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true)
    private String userId;  //conversation id from Rasa tracker

    @OneToMany(mappedBy="agent")
    private Set<Belief> beliefs = new HashSet<>();

    @OneToMany(mappedBy="agent")
    private Set<Desire> desires = new HashSet<>();

    private Long intentionId;

    public String currentSubject;

    public Boolean active;

    public Long currentAction;

    public float score;

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

    public void setUser(String userId) {
        this.userId = userId;
    }

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

}
