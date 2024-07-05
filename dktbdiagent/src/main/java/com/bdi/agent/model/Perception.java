package com.bdi.agent.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class Perception {

    private String type;
    private String subject;
    private String attribute;
    private String text;

    /**
     * Construct a new perception object for integration testing.
     *
     * @param type The type of the response
     * @param subject The subject to send
     * @param attribute The attribute to send
     * @param text The text to send
     */
    public Perception(@JsonProperty("type") String type, @JsonProperty("subject") String subject,
                      @JsonProperty("attribute") String attribute,  @JsonProperty("text") String text) {
        this.type = type;
        this.subject = subject;
        this.attribute = attribute;
        this.text = text;
    }

    public String getIntentionName() {
        return this.getType() + "_" + this.getSubject() + "_" + this.getAttribute();
    }

}
