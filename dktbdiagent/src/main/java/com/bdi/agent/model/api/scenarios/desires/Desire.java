package com.bdi.agent.model.api.scenarios.desires;

import com.bdi.agent.model.api.scenarios.actions.Action;
import com.bdi.agent.model.enums.Phase;
import java.util.List;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class Desire {
    private Long id;
    private Phase phase;
    private String name;
    private List<List<Constraint>> constraints;
    private List<Action> actions;

    /**
     * Construct a new desire.
     *
     * @param id The id of the desire
     * @param name The name of the desire
     * @param constraints The constraints of the desire
     */
    public Desire(Long id, String name, List<List<Constraint>> constraints) {
        this.id = id;
        this.name = name;
        this.constraints = constraints;
        this.phase = Phase.PHASE1;
    }

    /**
     * Construct a new desire.
     *
     * @param id The id of the desire
     * @param name The name of the desire
     * @param constraints The constraints of the desire
     * @param phase The phase of the desire
     */
    public Desire(Long id, String name, List<List<Constraint>> constraints, Phase phase) {
        this.id = id;
        this.name = name;
        this.constraints = constraints;
        this.phase = phase;
    }

    /**
     * Construct a new desire.
     *
     * @param id The id of the desire
     * @param name The name of the desire
     * @param constraints The constraints of the desire
     * @param phase The phase of the desire
     * @param actions The actions of the desire
     */
    public Desire(Long id, String name, List<List<Constraint>> constraints, Phase phase, List<Action> actions) {
        this.id = id;
        this.phase = phase;
        this.name = name;
        this.constraints = constraints;
        this.actions = actions;
    }
}
