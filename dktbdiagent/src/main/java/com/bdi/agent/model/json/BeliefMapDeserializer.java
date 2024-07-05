package com.bdi.agent.model.json;

import com.bdi.agent.model.Action;
import com.bdi.agent.model.Belief;
import com.bdi.agent.model.BeliefMap;
import com.bdi.agent.model.enums.BeliefUpdateType;
import com.bdi.agent.model.enums.BoundaryCheck;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.reflect.TypeToken;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BeliefMapDeserializer implements JsonDeserializer<BeliefMap> {
    private Map<String, Belief> beliefMapping;

    public BeliefMapDeserializer(Map<String, Belief> beliefMapping) {
        this.beliefMapping = beliefMapping;
    }

    @Override
    public BeliefMap deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context)
            throws JsonParseException {
        JsonObject jsonObject = json.getAsJsonObject();

        BeliefMap beliefMap = new BeliefMap();

        // Deserialize actionConditions
        List<Action> actionConditions =
                context.deserialize(jsonObject.get("actionConditions"), new TypeToken<List<Action>>() {
                }.getType());
        beliefMap.setActionConditions(actionConditions);

        // Deserialize beliefConditions
        JsonObject beliefConditionsJson = jsonObject.getAsJsonObject("beliefConditions");
        Map<Belief, BoundaryCheck> beliefConditions = new HashMap<>();
        beliefConditionsJson.entrySet().forEach(entry -> {
            Belief belief = findBeliefByName(entry.getKey()); // Implement this method to find Belief by name
            BoundaryCheck boundaryCheck = context.deserialize(entry.getValue(), BoundaryCheck.class);
            beliefConditions.put(belief, boundaryCheck);
        });
        beliefMap.setBeliefConditions(beliefConditions);

        // Deserialize beliefConditionValues
        JsonObject beliefConditionValuesJson = jsonObject.getAsJsonObject("beliefConditionValues");
        Map<Belief, Float> beliefConditionValues = new HashMap<>();
        beliefConditionValuesJson.entrySet().forEach(entry -> {
            Belief belief = findBeliefByName(entry.getKey()); // Implement this method to find Belief by name
            Float value = entry.getValue().getAsFloat();
            beliefConditionValues.put(belief, value);
        });
        beliefMap.setBeliefConditionValues(beliefConditionValues);

        // Deserialize beliefMapping
        JsonObject beliefMappingJson = jsonObject.getAsJsonObject("beliefMapping");
        Map<Belief, Float> beliefMapping = new HashMap<>();
        beliefMappingJson.entrySet().forEach(entry -> {
            Belief belief = findBeliefByName(entry.getKey()); // Implement this method to find Belief by name
            Float value = entry.getValue().getAsFloat();
            beliefMapping.put(belief, value);
        });
        beliefMap.setBeliefMapping(beliefMapping);

        // Deserialize beliefMod
        JsonObject beliefModJson = jsonObject.getAsJsonObject("beliefMod");
        Map<Belief, BeliefUpdateType> beliefMod = new HashMap<>();
        beliefModJson.entrySet().forEach(entry -> {
            Belief belief = findBeliefByName(entry.getKey()); // Implement this method to find Belief by name
            BeliefUpdateType updateType = context.deserialize(entry.getValue(), BeliefUpdateType.class);
            beliefMod.put(belief, updateType);
        });
        beliefMap.setBeliefMod(beliefMod);

        return beliefMap;
    }

    // Method to find Belief by name (you need to implement this based on your application)
    private Belief findBeliefByName(String name) {
        return this.beliefMapping.get(name);
    }
}

