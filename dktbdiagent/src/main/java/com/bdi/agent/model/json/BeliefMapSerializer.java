package com.bdi.agent.model.json;

import com.bdi.agent.model.BeliefMap;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import java.lang.reflect.Type;

public class BeliefMapSerializer implements JsonSerializer<BeliefMap> {

    @Override
    public JsonElement serialize(BeliefMap beliefMap, Type typeOfSrc, JsonSerializationContext context) {
        JsonObject jsonObject = new JsonObject();

        // Serialize actionConditions
        jsonObject.add("actionConditions", context.serialize(beliefMap.getActionConditions()));

        // Serialize beliefConditions
        JsonObject beliefConditionsJson = new JsonObject();
        beliefMap.getBeliefConditions().forEach((belief, boundaryCheck) -> {
            beliefConditionsJson.add(belief.getName(), context.serialize(boundaryCheck));
        });
        jsonObject.add("beliefConditions", beliefConditionsJson);

        // Serialize beliefConditionValues
        JsonObject beliefConditionValuesJson = new JsonObject();
        beliefMap.getBeliefConditionValues().forEach((belief, value) -> {
            beliefConditionValuesJson.addProperty(belief.getName(), value);
        });
        jsonObject.add("beliefConditionValues", beliefConditionValuesJson);

        // Serialize beliefMapping
        JsonObject beliefMappingJson = new JsonObject();
        beliefMap.getBeliefMapping().forEach((belief, value) -> {
            beliefMappingJson.addProperty(belief.getName(), value);
        });
        jsonObject.add("beliefMapping", beliefMappingJson);

        // Serialize beliefMod
        JsonObject beliefModJson = new JsonObject();
        beliefMap.getBeliefMod().forEach((belief, updateType) -> {
            beliefModJson.add(belief.getName(), context.serialize(updateType));
        });
        jsonObject.add("beliefMod", beliefModJson);

        return jsonObject;
    }
}

