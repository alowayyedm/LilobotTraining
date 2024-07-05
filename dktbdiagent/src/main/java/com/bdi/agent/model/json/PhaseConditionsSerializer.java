package com.bdi.agent.model.json;

import com.bdi.agent.model.PhaseConditions;
import com.bdi.agent.model.util.BeliefConstraint;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import java.lang.reflect.Type;

public class PhaseConditionsSerializer implements JsonSerializer<PhaseConditions> {

    @Override
    public JsonElement serialize(PhaseConditions phaseConditions, Type typeOfSrc, JsonSerializationContext context) {
        JsonObject jsonObject = new JsonObject();

        // Serialize desire
        jsonObject.add("desire", context.serialize(phaseConditions.getDesire().getName()));

        // Serialize conditions;
        JsonArray conditionsArray = new JsonArray();
        for (BeliefConstraint condition : phaseConditions.getConditions()) {
            conditionsArray.add(context.serialize(condition));
        }
        jsonObject.add("conditions", conditionsArray);

        return jsonObject;
    }
}

