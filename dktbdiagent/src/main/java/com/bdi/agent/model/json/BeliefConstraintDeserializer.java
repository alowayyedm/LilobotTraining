package com.bdi.agent.model.json;

import com.bdi.agent.model.Belief;
import com.bdi.agent.model.enums.BoundaryCheck;
import com.bdi.agent.model.util.BeliefConstraint;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import java.lang.reflect.Type;
import java.util.Map;

public class BeliefConstraintDeserializer implements JsonDeserializer<BeliefConstraint> {
    private Map<String, Belief> beliefMapping;

    public BeliefConstraintDeserializer(Map<String, Belief> beliefMapping) {
        this.beliefMapping = beliefMapping;
    }

    @Override
    public BeliefConstraint deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context)
            throws JsonParseException {
        JsonObject jsonObject = json.getAsJsonObject();

        BoundaryCheck boundaryCheck = context.deserialize(jsonObject.get("boundaryCheck"), BoundaryCheck.class);

        Belief belief = null;

        if (jsonObject.has("belief")) {
            belief = beliefMapping.get(jsonObject.get("belief").getAsString());
        }

        float goalValue = jsonObject.get("goalValue").getAsFloat();

        return new BeliefConstraint(boundaryCheck, belief, goalValue);
    }
}

