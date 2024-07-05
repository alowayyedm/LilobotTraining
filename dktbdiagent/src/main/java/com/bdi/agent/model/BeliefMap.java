package com.bdi.agent.model;

import com.bdi.agent.model.enums.BeliefUpdateType;
import com.bdi.agent.model.enums.BoundaryCheck;
import com.fasterxml.jackson.annotation.JsonSetter;
import com.google.gson.annotations.Expose;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.persistence.CascadeType;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Entity
@AllArgsConstructor
@Getter
@Setter
public class BeliefMap {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    // The action's names that can be set to completed
    @Getter
    @Setter
    @OneToMany(cascade = CascadeType.ALL)
    @Expose
    private List<Action> actionConditions;

    // The belief that needs to be active with a certain boundary
    @ElementCollection
    @Getter
    @Setter
    @Expose
    private Map<Belief, BoundaryCheck> beliefConditions;

    // The boundary to evaluate the belief on
    @ElementCollection
    @Expose
    private Map<Belief, Float> beliefConditionValues;

    // The beliefs to set a value for
    @ElementCollection
    @Getter
    @Setter
    @Expose
    private Map<Belief, Float> beliefMapping;

    // The way to update the belief with the value (inc, dec, set...)
    @ElementCollection
    @Expose
    private Map<Belief, BeliefUpdateType> beliefMod;

    @JsonSetter
    public void setBeliefConditions(Map<Belief, BoundaryCheck> m) {
        this.beliefConditions = m;
    }

    /**
     * Sets belief conditions.
     *
     * @param conditions the conditions to set.
     */
    public void setBeliefConditions(List<BeliefCondition> conditions) {
        for (BeliefCondition c : conditions) {
            this.getBeliefConditions().put(c.getBelief(), c.getBoundaryCheck());
            this.getBeliefConditionValues().put(c.getBelief(), c.getValue());
        }
    }

    /**
     * Constructor for Belief map.
     *
     */
    public BeliefMap() {
        this.beliefMapping = new HashMap<>();
        this.beliefMod = new HashMap<>();
        this.beliefConditions = new HashMap<>();
        this.beliefConditionValues = new HashMap<>();
        this.actionConditions = new ArrayList<>();
    }
}
