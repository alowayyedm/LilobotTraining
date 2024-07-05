package com.bdi.agent.model.api.scenarios.desires;

import com.bdi.agent.model.enums.BoundaryCheck;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class Constraint {
    private String belief;

    private BoundaryCheck boundary;

    private Float value;
}
