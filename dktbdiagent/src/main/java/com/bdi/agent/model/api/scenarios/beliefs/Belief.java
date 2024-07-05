package com.bdi.agent.model.api.scenarios.beliefs;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class Belief {
    private Long id;
    private String name;
    private Float value;

    //    /**
    //     * Constructor without default value given.
    //     *
    //     * @param id the id of the Belief.
    //     * @param name the name of the Belief.
    //     */
    //    public Belief(Long id, String name) {
    //        this.id = id;
    //        this.name = name;
    //        this.value = 0.0f;
    //    }
}
