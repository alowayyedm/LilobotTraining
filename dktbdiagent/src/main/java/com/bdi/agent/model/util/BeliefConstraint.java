package com.bdi.agent.model.util;

import com.bdi.agent.model.Belief;
import com.bdi.agent.model.enums.BeliefName;
import com.bdi.agent.model.enums.BoundaryCheck;
import com.google.gson.annotations.Expose;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToOne;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Entity
@AllArgsConstructor
@NoArgsConstructor
public class BeliefConstraint {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Expose
    private BoundaryCheck boundaryCheck;

    @OneToOne(cascade = CascadeType.ALL)
    @Expose
    private Belief belief;

    @Expose
    private BeliefName beliefName;

    /**
     * Create a new belief constraint.
     *
     * @param boundaryCheck The boundary check to use
     * @param belief The belief
     * @param goalValue The value to use in the boundary check
     */
    public BeliefConstraint(BoundaryCheck boundaryCheck, Belief belief, float goalValue) {
        this.boundaryCheck = boundaryCheck;
        this.belief = belief;
        this.goalValue = goalValue;
    }

    @Expose
    private float goalValue;

    /**
     * Constructor for belief constraints.
     *
     * @param boundaryCheck the boundary check.
     * @param beliefName the belief name.
     * @param goalValue the goal value.
     */
    public BeliefConstraint(BoundaryCheck boundaryCheck, BeliefName beliefName, float goalValue) {
        this.boundaryCheck = boundaryCheck;
        this.beliefName = beliefName;
        this.goalValue = goalValue;
    }
}
