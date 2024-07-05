package com.bdi.agent.model.json;

import com.bdi.agent.model.util.BeliefConstraint;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import java.lang.reflect.Type;

public class BeliefConstraintSerializer implements JsonSerializer<BeliefConstraint> {

    @Override
    public JsonElement serialize(BeliefConstraint beliefConstraint, Type typeOfSrc, JsonSerializationContext context) {
        JsonObject jsonObject = new JsonObject();

        // Serialize boundaryCheck
        jsonObject.add("boundaryCheck", context.serialize(beliefConstraint.getBoundaryCheck()));

        // Serialize belief or beliefName
        if (beliefConstraint.getBelief() != null) {
            jsonObject.add("belief", context.serialize(beliefConstraint.getBelief().getName()));
        }

        // Serialize goalValue
        jsonObject.addProperty("goalValue", beliefConstraint.getGoalValue());

        return jsonObject;
    }
}

