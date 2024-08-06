package com.bdi.agent.model;

import javax.persistence.*;

@Entity
@Table(name = "MetaExperiment")
public class MetaExperiment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String username;

    private int condition;
    private int knowledge;
    private int sessNum;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public int getCondition() {
        return condition;
    }

    public void setCondition(int condition) {
        this.condition = condition;
    }

    public int getKnowledge() {
        return knowledge;
    }

    public void setKnowledge(int knowledge) {
        this.knowledge = knowledge;
    }

    public int getSessNum() {
        return sessNum;
    }

    public void setSessNum(int SessNum) {
        this.sessNum = SessNum;
    }
}
