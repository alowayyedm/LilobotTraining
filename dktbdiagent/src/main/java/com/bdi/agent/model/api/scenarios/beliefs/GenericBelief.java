package com.bdi.agent.model.api.scenarios.beliefs;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class GenericBelief<T> {
    private String name;
    private T type;
    private Float value;
}
