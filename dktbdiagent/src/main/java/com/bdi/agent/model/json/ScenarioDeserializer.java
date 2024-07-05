package com.bdi.agent.model.json;

import com.bdi.agent.model.Action;
import com.bdi.agent.model.Belief;
import com.bdi.agent.model.BeliefMap;
import com.bdi.agent.model.Desire;
import com.bdi.agent.model.Knowledge;
import com.bdi.agent.model.PhaseConditions;
import com.bdi.agent.model.Scenario;
import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.reflect.TypeToken;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class ScenarioDeserializer implements JsonDeserializer<Scenario> {

    @Override
    public Scenario deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context)
            throws JsonParseException {
        JsonObject jsonObject = json.getAsJsonObject();
        String name = jsonObject.get("name").getAsString();

        // Deserialize simple lists
        List<Action> actions = context.deserialize(jsonObject.get("actions"), new TypeToken<List<Action>>() {
        }.getType());
        List<Knowledge> knowledgeList =
                context.deserialize(jsonObject.get("knowledgeList"), new TypeToken<List<Knowledge>>() {
                }.getType());
        List<Belief> beliefs = context.deserialize(jsonObject.get("beliefs"), new TypeToken<List<Belief>>() {
        }.getType());
        List<Desire> desires = context.deserialize(jsonObject.get("desires"), new TypeToken<List<Desire>>() {
        }.getType());

        Map<String, Belief> nameToBeliefMap =
                beliefs.stream().collect(Collectors.toMap(belief -> belief.getName(), belief -> belief));
        Map<String, Desire> nameToDesireMap =
                desires.stream().collect(Collectors.toMap(desire -> desire.getName(), desire -> desire));

        List<PhaseConditions> conditions = new ArrayList<>();
        JsonArray jsonArray = jsonObject.getAsJsonArray("conditions");
        jsonArray.forEach(jsonElement -> {
            PhaseConditionsDeserializer phaseConditionsDeserializer =
                    new PhaseConditionsDeserializer(nameToDesireMap, nameToBeliefMap);
            conditions.add(phaseConditionsDeserializer.deserialize(jsonElement, PhaseConditions.class, context));
        });

        // Deserialize intentionMapping
        Map<String, BeliefMap> intentionMapping = new HashMap<>();

        JsonObject intentionMappingArray = jsonObject.getAsJsonObject("intentionMapping");
        intentionMappingArray.entrySet().forEach(entry -> {
            String key = entry.getKey();
            JsonObject mappingObject = entry.getValue().getAsJsonObject();

            BeliefMapDeserializer beliefMapDeserializer = new BeliefMapDeserializer(nameToBeliefMap);
            BeliefMap beliefMap = beliefMapDeserializer.deserialize(mappingObject, BeliefMap.class, context);

            intentionMapping.put(key, beliefMap);
        });

        Scenario scenario = new Scenario(name);
        scenario.setActions(actions);
        scenario.setKnowledgeList(knowledgeList);
        scenario.setBeliefs(beliefs);
        scenario.setDesires(desires);
        scenario.setConditions(conditions);
        scenario.setIntentionMapping(intentionMapping);

        return scenario;
    }
}

