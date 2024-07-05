package com.bdi.agent.model;

import com.bdi.agent.model.enums.Phase;
import com.bdi.agent.model.util.BeliefConstraint;
import com.google.gson.annotations.Expose;
import java.util.List;
import javax.persistence.CascadeType;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.Cascade;

@Entity
@RequiredArgsConstructor
@Getter
@Setter
public class PhaseConditions {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    // The phase of this condition
    @Getter
    @Setter
    @ManyToOne(cascade = CascadeType.ALL)
    @Expose
    private Desire desire;

    // The conditions of this phase to be active, all need to be satisfied
    // For multiple PhaseConditions for one phase, only one needs to be activated to activate the phase
    @OneToMany(cascade = CascadeType.ALL)
    @Getter
    @Setter
    @Expose
    private List<BeliefConstraint> conditions;

    public PhaseConditions(Desire desire, List<BeliefConstraint> conditions) {
        this.desire = desire;
        this.conditions = conditions;
    }
}
