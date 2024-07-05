package com.bdi.agent.model;

import com.bdi.agent.model.enums.BoundaryCheck;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import lombok.Data;
import org.apache.commons.lang3.tuple.Triple;

@Data
@Entity
public class BeliefCondition {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name = "belief_id", nullable = false)
    private Belief belief;

    @Column(name = "boundary_check", nullable = false)
    private BoundaryCheck boundaryCheck;

    @Column(name = "value", nullable = false)
    private Float value;

    /**
     * Creates a belief condition used to define boundaries for belief values.
     * For example before some beliefs can be updated they need to have another belief be greater than some value.
     * This class can be used for that.
     *
     * @param belief belief to check with
     * @param boundaryCheck type of check to be done
     * @param value value checked with
     */
    public BeliefCondition(Belief belief, BoundaryCheck boundaryCheck, Float value) {
        this.belief = belief;
        this.boundaryCheck = boundaryCheck;
        this.value = value;
    }

    // Default constructor for JPA
    public BeliefCondition() {
    }
}
