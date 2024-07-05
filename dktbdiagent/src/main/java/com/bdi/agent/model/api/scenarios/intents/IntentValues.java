package com.bdi.agent.model.api.scenarios.intents;

import javax.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class IntentValues {
    @NotNull
    private String[] intentNames;
}
