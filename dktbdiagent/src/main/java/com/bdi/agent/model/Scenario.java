package com.bdi.agent.model;

import com.google.gson.annotations.Expose;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.persistence.OneToMany;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.Setter;

@Entity
@NoArgsConstructor
@Setter
@Getter
public class Scenario {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @NonNull
    @Expose
    private String name;

    @OneToMany(cascade = CascadeType.ALL)
    @Expose
    private List<Action> actions;

    // The list of knowledge (intents)
    @Expose
    @ManyToMany(cascade = CascadeType.ALL)
    private List<Knowledge> knowledgeList;

    //    public Map<String, BeliefMap> getIntentionMapping() {
    //        return intentionMapping.stream().collect(Collectors.
    //        toMap(x -> x.getIntentionName(), x -> x.getBeliefMap()));
    //    }
    //
    //    public void setIntentionMapping(Map<String, BeliefMap> intentionMapping) {
    //        List<IntentionMapping> intentionListMapping = new ArrayList<>();
    //        for (String intent : intentionMapping.keySet()) {
    //            intentionListMapping.add(new IntentionMapping(intent, intentionMapping.get(intent)));
    //        }
    //        this.intentionMapping = intentionListMapping;
    //  }

    //    @OneToMany(cascade = CascadeType.ALL)
    @OneToMany(cascade = CascadeType.ALL)
    @Expose
    private Map<String, BeliefMap> intentionMapping;

    // The list of beliefs of the scenario
    @OneToMany(cascade = CascadeType.ALL)
    @Expose
    private List<Belief> beliefs;

    // The list of desires for the scenario
    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @Expose
    private List<Desire> desires;

    // The conditions for each phase to be active
    @OneToMany(cascade = CascadeType.ALL)
    @Expose
    private List<PhaseConditions> conditions;

    /**
     * scenario creates empty lists rather than null values.
     *
     * @param name name of scenario
     */
    public Scenario(String name) {
        this.name = name;
        this.actions = new ArrayList<>();
        this.beliefs = new ArrayList<>();
        this.conditions = new ArrayList<>();
        this.desires = new ArrayList<>();
        this.intentionMapping = new HashMap<>();
        this.knowledgeList = new ArrayList<>();
    }

    /**
     * Get the phase conditions for a desire.
     *
     * @param desire The desire to get the phase conditions for
     * @return The phase conditions for the desire
     */
    public List<PhaseConditions> getPhaseConditions(Desire desire) {
        List<PhaseConditions> phaseConditions = new ArrayList<>();
        for (PhaseConditions condition : conditions) {
            if (condition.getDesire().equals(desire)) {
                phaseConditions.add(condition);
            }
        }
        return phaseConditions;
    }

    /**
     * Get the desire with the given name.
     *
     * @param desire The name of the desire to get
     * @return The desire with the given name
     */
    public Desire getDesire(String desire) {
        for (Desire d : desires) {
            if (d.getName().equals(desire)) {
                return d;
            }
        }
        return null;
    }
}
