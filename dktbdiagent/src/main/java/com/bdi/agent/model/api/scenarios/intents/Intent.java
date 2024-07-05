package com.bdi.agent.model.api.scenarios.intents;

import java.util.List;
import javax.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class Intent {
    @NotNull
    private String intent;

    @NotNull
    private List<String> values;
}
