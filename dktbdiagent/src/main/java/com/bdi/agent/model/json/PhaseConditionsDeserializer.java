package com.bdi.agent.model.json;

import com.bdi.agent.model.Belief;
import com.bdi.agent.model.Desire;
import com.bdi.agent.model.PhaseConditions;
import com.bdi.agent.model.util.BeliefConstraint;
import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class PhaseConditionsDeserializer implements JsonDeserializer<PhaseConditions> {
    private Map<String, Desire> desireMapping;
    private Map<String, Belief> beliefMapping;

    public PhaseConditionsDeserializer(Map<String, Desire> desireMapping, Map<String, Belief> beliefMapping) {
        this.desireMapping = desireMapping;
        this.beliefMapping = beliefMapping;
    }

    @Override
    public PhaseConditions deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context)
            throws JsonParseException {
        JsonObject jsonObject = json.getAsJsonObject();

        // Deserialize desire
        Desire desire = desireMapping.get(jsonObject.get("desire").getAsString());

        // Deserialize conditions
        List<BeliefConstraint> conditions = new ArrayList<>();
        JsonArray conditionsArray = jsonObject.getAsJsonArray("conditions");
        for (JsonElement element : conditionsArray) {
            BeliefConstraintDeserializer beliefConstraintDeserializer =
                    new BeliefConstraintDeserializer(this.beliefMapping);
            BeliefConstraint condition =
                    beliefConstraintDeserializer.deserialize(element, BeliefConstraint.class, context);
            conditions.add(condition);
        }

        return new PhaseConditions(desire, conditions);
    }
}

