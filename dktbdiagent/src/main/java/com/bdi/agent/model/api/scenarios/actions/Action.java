package com.bdi.agent.model.api.scenarios.actions;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class Action {
    private String name;

    public Action(String name) {
        this.name = name;
    }
}
