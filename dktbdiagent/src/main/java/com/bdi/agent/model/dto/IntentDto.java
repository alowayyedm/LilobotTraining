package com.bdi.agent.model.dto;

import java.util.List;
import javax.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class IntentDto {
    @NotBlank
    private String intentionName;

    @NotBlank
    private List<String> examples;
}
