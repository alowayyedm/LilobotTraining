package com.bdi.agent.model;

import lombok.AllArgsConstructor;

import javax.persistence.*;
import java.util.Set;

@Entity
@Table
@AllArgsConstructor
public class HumanValues {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name="agent_id", nullable=false)
    private Agent agent;
    private String name;
    private String fullName;
    private Float value;
    private int ValueSet;

//    @OneToMany(mappedBy = "humanValues", cascade = CascadeType.ALL)
//    private Set<Action> actions;

    public HumanValues() {

    }

    public HumanValues(Agent agent, String name, String fullName, Float value, int ValueSet){
        this.agent = agent;
        this.name = name;
        this.fullName = fullName;
        this.value = value;
        this.ValueSet = ValueSet;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Agent getAgent() {
        return agent;
    }

    public void setAgent(Agent agent) {
        this.agent = agent;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public Float getValue() {
        return value;
    }

    public void setValue(Float value) {
        this.value = value;
    }

    public int getValueSet() {
        return ValueSet;
    }

    public void setValueSet(int valueSet) {
        ValueSet = valueSet;
    }
}
