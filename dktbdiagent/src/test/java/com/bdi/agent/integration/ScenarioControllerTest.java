package com.bdi.agent.integration;

import com.bdi.agent.model.*;
import com.bdi.agent.model.api.scenarios.Scenarios;
import com.bdi.agent.model.api.scenarios.beliefs.GenericBelief;
import com.bdi.agent.model.api.scenarios.desires.Constraint;
import com.bdi.agent.model.api.scenarios.intents.Intent;
import com.bdi.agent.model.api.scenarios.intents.ModifyIntent;
import com.bdi.agent.model.enums.BeliefUpdateType;
import com.bdi.agent.model.enums.BoundaryCheck;
import com.bdi.agent.model.enums.Phase;
import com.bdi.agent.service.AgentService;
import com.bdi.agent.service.ScenarioService;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.lang.reflect.Type;
import java.util.*;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


@ExtendWith(SpringExtension.class)
@TestPropertySource(locations="classpath:application-test.properties")
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
@AutoConfigureMockMvc(addFilters = false)
@SpringBootTest
public class ScenarioControllerTest {

    static {
        System.setProperty("AUTH_TOKEN", "test");
    }

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private AgentService agentService;

    @MockBean
    private ScenarioService scenarioService;


    @Test
    public void createScenarioThatExists() throws Exception {

        when(scenarioService.scenarioExists("default")).thenReturn(true);
        mockMvc.perform(post("/scenarios/default")).andExpect(status().isBadRequest());

    }

    @Test
    public void createScenarioThatDoesNotExists() throws Exception {

        when(scenarioService.scenarioExists("default")).thenReturn(false);
        mockMvc.perform(post("/scenarios/default")).andExpect(status().isCreated());

    }

    @Test
    public void deleteScenarioCouldNotFindScenario() throws Exception {
        when(scenarioService.deleteScenario("default")).thenReturn(false);
        mockMvc.perform(delete("/scenarios/default")).andExpect(status().isNotFound());
    }

    @Test
    public void deleteScenarioThatExists() throws Exception {
        when(scenarioService.deleteScenario("default")).thenReturn(true);
        mockMvc.perform(delete("/scenarios/default")).andExpect(status().isOk());
    }

    @Test
    public void editExistingIntentsTestFound() throws Exception {
        Intent i1 = new Intent("I1", List.of("v1", "v2"));
        Intent i2 = new Intent("I1", new ArrayList<>(List.of("v1")));
        ModifyIntent mi = new ModifyIntent(new ArrayList<>(List.of(i2)));
        Scenario sc = new Scenario("default");
        Knowledge k = new Knowledge("I1", "sub", "attr");
        k.setValues(new ArrayList<>(List.of("v1", "v2")));
        sc.setKnowledgeList(new ArrayList<>(List.of(k)));
        when(scenarioService.getScenarioByName("default")).thenReturn(sc);
        String requestBody = objectMapper.writeValueAsString(mi);
        MvcResult res = mockMvc.perform(put("/scenarios/default/knowledge").contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(requestBody)).andExpect(status().isOk()).andReturn();
        String response = res.getResponse().getContentAsString();
        ModifyIntent miResponse = objectMapper.readValue(response,
                new TypeReference<ModifyIntent>() {});
        Assertions.assertEquals(mi, miResponse);
    }

    @Test
    public void editExistingIntentsTestNotFoundWithWrongFormat() throws Exception {
        Intent i1 = new Intent("I1", List.of("v1", "v2"));
        ModifyIntent mi = new ModifyIntent(List.of(i1));
        Scenario sc = new Scenario("default");
        sc.setKnowledgeList(new ArrayList<>());
        when(scenarioService.getScenarioByName("default")).thenReturn(sc);
        String requestBody = objectMapper.writeValueAsString(mi);
        mockMvc.perform(put("/scenarios/default/knowledge")
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .accept(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isInternalServerError());
    }

    @Test
    public void editExistingIntentsTestNotFoundCreatesNewIntent() throws Exception {
        Intent i1 = new Intent("I_1_t", List.of("v1", "v2"));
        ModifyIntent mi = new ModifyIntent(List.of(i1));
        Scenario sc = new Scenario("default");
        sc.setKnowledgeList(new ArrayList<>());
        when(scenarioService.getScenarioByName("default")).thenReturn(sc);
        String requestBody = objectMapper.writeValueAsString(mi);
        mockMvc.perform(put("/scenarios/default/knowledge")
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .accept(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isOk());
    }

    @Test
    public void getAllIntents() throws Exception {
        Intent i1 = new Intent("null_sub_attr", List.of("v1", "v2"));
        Knowledge k = new Knowledge("I1", "sub", "attr");
        k.setValues(List.of("v1", "v2"));
        Scenario sc = new Scenario("default");
        sc.setKnowledgeList(List.of(k));
        when(scenarioService.getScenarioByName("default")).thenReturn(sc);
        MvcResult res = mockMvc.perform(get("/scenarios/default/knowledge")).andExpect(status().isOk())
                .andReturn();
        String intents = res.getResponse().getContentAsString();
        List<Intent> intentList = objectMapper.readValue(intents, new TypeReference<List<Intent>>() {
            public Type getType() {
                return super.getType();
            }
        });
        Assertions.assertEquals(List.of(i1), intentList);
    }

    @Test
    public void createIntentIsPresentTest() throws Exception {
        Scenario sc = new Scenario("default");
        Knowledge k = new Knowledge("I1", "a", "b", "c");
        sc.setKnowledgeList(List.of(k));
        Intent i = new Intent("a_b_c", List.of());
        String requestBody = objectMapper.writeValueAsString(i);
        when(scenarioService.getScenarioByName("default")).thenReturn(sc);
        mockMvc.perform(post("/scenarios/default/knowledge").contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(requestBody)).andExpect(status().isConflict());
    }

    @Test
    public void createIntentIsNotPresentTest() throws Exception {
        Scenario sc = new Scenario("default");
        Intent i = new Intent("a_b_c", List.of());
        String requestBody = objectMapper.writeValueAsString(i);
        sc.setKnowledgeList(new ArrayList<>(List.of()));
        when(scenarioService.getScenarioByName("default")).thenReturn(sc);
        doNothing().when(scenarioService).editScenario(any());
        MvcResult res = mockMvc.perform(post("/scenarios/default/knowledge")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON).content(requestBody))
                .andExpect(status().isCreated()).andReturn();
        String intentResponse = res.getResponse().getContentAsString();
        Intent intentObj = objectMapper.readValue(intentResponse, new TypeReference<Intent>() {
            public Type getType() {
                return super.getType();
            }
        });
        Assertions.assertEquals(i, intentObj);
    }

    @Test
    public void deleteIntentNotFoundTest() throws Exception {
        Scenario sc = new Scenario("default");
        sc.setKnowledgeList(new ArrayList<>(List.of()));
        when(scenarioService.getScenarioByName("default")).thenReturn(sc);
        mockMvc.perform(delete("/scenarios/default/knowledge/I1"))
                .andExpect(status().isNotFound());
    }

    @Test
    public void deleteIntentFoundTest() throws Exception {
        Scenario sc = new Scenario("default");
        Knowledge k = new Knowledge("I1", "a", "b", "c");
        sc.setKnowledgeList(new ArrayList<>(List.of(k)));
        when(scenarioService.getScenarioByName("default")).thenReturn(sc);
        mockMvc.perform(delete("/scenarios/default/knowledge/a_b_c"))
                .andExpect(status().isOk());
    }

    @Test
    public void getAllScenariosTest() throws Exception {
//        Scenario sc1 = new Scenario("s1");
//        Scenario sc2 = new Scenario("s2");
        when(scenarioService.getAllScenarios()).thenReturn(List.of("s1", "s2"));
        MvcResult res = mockMvc.perform(get("/scenarios")).andExpect(status().isOk()).andReturn();
        String response = res.getResponse().getContentAsString();
        Scenarios sc = objectMapper.readValue(response, new TypeReference<Scenarios>() {
            public Type getType() {
                return super.getType();
            }
        });
        Assertions.assertEquals(new Scenarios(List.of("s1", "s2")), sc);
    }

    @Test
    public void getAllBeliefsTest() throws Exception {
        Scenario sc = new Scenario("default");
        Belief b = new Belief("B1", "B1_ph1", 0.3f);
        b.setId(1L);
        sc.setBeliefs(List.of(b));
        when(scenarioService.getScenarioByName("default")).thenReturn(sc);
        MvcResult res = mockMvc.perform(get("/scenarios/default/beliefs"))
                .andExpect(status().isOk()).andReturn();
        String response = res.getResponse().getContentAsString();
        List<com.bdi.agent.model.api.scenarios.beliefs.Belief> belief =
                objectMapper.readValue(response
                        , new TypeReference<List<com.bdi.agent.model.api.scenarios.beliefs.Belief>>() {
                        });

        Assertions.assertEquals(List.of(new com.bdi.agent.model.api.scenarios.beliefs.Belief(1L, "B1_ph1", 0.3f)), belief);
    }

    @Test
    public void editBeliefNotFoundTest() throws Exception {
        Scenario sc = new Scenario("default");
        sc.setBeliefs(List.of());
        when(scenarioService.getScenarioByName("default")).thenReturn(sc);
        com.bdi.agent.model.api.scenarios.beliefs.Belief b =
                new com.bdi.agent.model.api.scenarios.beliefs.Belief(1L, "B1", 0.0f);
        List<com.bdi.agent.model.api.scenarios.beliefs.Belief> beliefs = List.of(b);
        String requestBody = objectMapper.writeValueAsString(beliefs);
        mockMvc.perform(put("/scenarios/default/beliefs")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .accept(MediaType.APPLICATION_JSON)
                .content(requestBody)).andExpect(status().isNotFound());
    }

    @Test
    public void editBeliefFoundTest() throws Exception {
        Scenario sc = new Scenario("default");
        Belief b1 = new Belief("B1", "B1", 0.3f);
        sc.setBeliefs(List.of(b1));
        when(scenarioService.getScenarioByName("default")).thenReturn(sc);
        com.bdi.agent.model.api.scenarios.beliefs.Belief b =
                new com.bdi.agent.model.api.scenarios.beliefs.Belief(1L, "B1", 0.0f);
        List<com.bdi.agent.model.api.scenarios.beliefs.Belief> beliefs = List.of(b);
        String requestBody = objectMapper.writeValueAsString(beliefs);
        MvcResult res = mockMvc.perform(put("/scenarios/default/beliefs")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .accept(MediaType.APPLICATION_JSON)
                .content(requestBody)).andExpect(status().isOk()).andReturn();
        String response = res.getResponse().getContentAsString();
        List<com.bdi.agent.model.api.scenarios.beliefs.Belief> beliefsResponse =
                objectMapper.readValue(response
                        , new TypeReference<List<com.bdi.agent.model.api.scenarios.beliefs.Belief>>() {});
        Assertions.assertEquals(beliefs, beliefsResponse);
    }

    @Test
    public void createBeliefTest() throws Exception {
        Scenario sc = new Scenario("default");
        sc.setBeliefs(new ArrayList<>(List.of()));
        when(scenarioService.getScenarioByName("default")).thenReturn(sc);
        com.bdi.agent.model.api.scenarios.beliefs.Belief b =
                new com.bdi.agent.model.api.scenarios.beliefs.Belief(null, "B1", 0.0f);
        String requestBody = objectMapper.writeValueAsString(b);
        MvcResult res = mockMvc.perform(post("/scenarios/default/beliefs")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(requestBody)).andExpect(status().isCreated()).andReturn();
        String response = res.getResponse().getContentAsString();
        com.bdi.agent.model.api.scenarios.beliefs.Belief bResponse =
                objectMapper.readValue(response
                        , new TypeReference<com.bdi.agent.model.api.scenarios.beliefs.Belief>() {});
        b.setId(null);
        Assertions.assertEquals(b, bResponse);

    }

    @Test
    public void deleteBeliefNotFoundTest() throws Exception {
        Scenario sc = new Scenario("default");
        sc.setBeliefs(new ArrayList<>());
        when(scenarioService.getScenarioByName("default")).thenReturn(sc);
        mockMvc.perform(delete("/scenarios/default/beliefs/1")).andExpect(status().isNotFound());
    }

    @Test
    public void deleteBeliefFoundTest() throws Exception {
        Scenario sc = new Scenario("default");
        Belief b = new Belief(1L, "B1", "I believe something", "", 0.3f);
        sc.setBeliefs(new ArrayList<>(List.of(b)));
        when(scenarioService.getScenarioByName("default")).thenReturn(sc);
        mockMvc.perform(delete("/scenarios/default/beliefs/I believe something"))
                .andExpect(status().isOk());
    }

    @Test
    public void getDesiresTest() throws Exception {
        Scenario sc = new Scenario("default");
        Desire d = new Desire(1L, "D1", "D1", true, Phase.PHASE1, new HashSet<>());
        sc.setDesires(new ArrayList<>(List.of(d)));
        sc.setConditions(new ArrayList<>());
        when(scenarioService.getScenarioByName("default")).thenReturn(sc);
        MvcResult res = mockMvc.perform(get("/scenarios/default/desires"))
                .andExpect(status().isOk()).andReturn();
        com.bdi.agent.model.api.scenarios.desires.Desire dExpectedResponse =
                new com.bdi.agent.model.api.scenarios.desires.Desire(
                        d.getId(),
                        d.getName(),
                        sc.getConditions().stream().filter(cond -> cond.getDesire().equals(d))
                                .map(cond -> cond.getConditions().stream()
                                        .map(c -> new Constraint(c.getBelief().getFullName(), c.getBoundaryCheck(),
                                                c.getGoalValue()))
                                        .toList()).toList(),
                        Phase.PHASE1,
                        new ArrayList<>());
        String response = res.getResponse().getContentAsString();
        List<com.bdi.agent.model.api.scenarios.desires.Desire> dActualResponse =
                objectMapper.readValue(response
                        , new TypeReference<List<com.bdi.agent.model.api.scenarios.desires.Desire>>() { });
        Assertions.assertEquals(List.of(dExpectedResponse), dActualResponse);
    }

    @Test
    public void editDesiresNotFoundTest() throws Exception {
        Scenario sc = new Scenario("default");
        when(scenarioService.getScenarioByName("default")).thenReturn(sc);
        com.bdi.agent.model.api.scenarios.desires.Desire d =
                new com.bdi.agent.model.api.scenarios.desires.Desire(1L, "D1", List.of());
        String requestBody = objectMapper.writeValueAsString(List.of(d));
        mockMvc.perform(put("/scenarios/default/desires")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(requestBody)).andExpect(status().isOk());
    }

    @Test
    public void editDesiresFoundTest() throws Exception {
        Scenario sc = new Scenario("default");
        Desire currD = new Desire(1L, "D1", "I desire something", true, Phase.PHASE1, new HashSet<>());
        sc.setDesires(new ArrayList<>(List.of(currD)));
        sc.setConditions(new ArrayList<>());
        when(scenarioService.getScenarioByName("default")).thenReturn(sc);
        com.bdi.agent.model.api.scenarios.desires.Desire d =
                new com.bdi.agent.model.api.scenarios.desires.Desire(1L, "D1", List.of());
        String requestBody = objectMapper.writeValueAsString(List.of(d));
        MvcResult res = mockMvc.perform(put("/scenarios/default/desires")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(requestBody)).andExpect(status().isOk()).andReturn();
        String response = res.getResponse().getContentAsString();
        List<com.bdi.agent.model.api.scenarios.desires.Desire> dResponse =
                objectMapper.readValue(response
                        , new TypeReference<List<com.bdi.agent.model.api.scenarios.desires.Desire>>() {});
        Assertions.assertEquals(List.of(d), dResponse);

    }

    @Test
    public void createDesireTest() throws Exception {
        Scenario sc = new Scenario("default");
        sc.setDesires(new ArrayList<>());
        sc.setConditions(new ArrayList<>());
        when(scenarioService.getScenarioByName("default")).thenReturn(sc);
        com.bdi.agent.model.api.scenarios.desires.Desire d =
                new com.bdi.agent.model.api.scenarios.desires.Desire(1L, "D1", List.of());
        String requestBody = objectMapper.writeValueAsString(d);
        MvcResult res = mockMvc.perform(post("/scenarios/default/desires")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(requestBody)).andExpect(status().isCreated()).andReturn();
        String response = res.getResponse().getContentAsString();
        com.bdi.agent.model.api.scenarios.desires.Desire dResponse =
                objectMapper.readValue(response
                        , new TypeReference<com.bdi.agent.model.api.scenarios.desires.Desire>() {});
        d.setId(null);
        Assertions.assertEquals(d, dResponse);
    }

    @Test
    public void deleteDesireFoundTest() throws Exception {
        Scenario sc = new Scenario("default");
        Desire currD = new Desire(1L, "D1", "D1", true, Phase.PHASE1, new HashSet<>());
        sc.setDesires(new ArrayList<>(List.of(currD)));
        sc.setConditions(new ArrayList<>());
        when(scenarioService.getScenarioByName("default")).thenReturn(sc);
        mockMvc.perform(delete("/scenarios/default/desires/D1")).andExpect(status().isOk());
    }

    @Test
    public void deleteDesireNotFoundTest() throws Exception {
        Scenario sc = new Scenario("default");
        sc.setDesires(new ArrayList<>(List.of()));
        when(scenarioService.getScenarioByName("default")).thenReturn(sc);
        mockMvc.perform(delete("/scenarios/default/desires/1")).andExpect(status().isNotFound());
    }

    @Test
    public void getActionConditionsTest() throws Exception {
        Scenario sc = new Scenario("default");
        Desire currD = new Desire(1L, "D1", "", true, Phase.PHASE1, new HashSet<>());
        Map<String, BeliefMap> im = new HashMap<>();
        BeliefMap bm = new BeliefMap();
        bm.setActionConditions(List.of(new Action(currD, "type", "A1", "sub", "attr", false)));
        im.put("intent", bm);
        sc.setIntentionMapping(im);
        when(scenarioService.getScenarioByName("default")).thenReturn(sc);
        MvcResult res = mockMvc.perform(get("/scenarios/default/intents/intent/mapping/actions"))
                .andExpect(status().isOk()).andReturn();
        String response = res.getResponse().getContentAsString();
        List<String> actualResponse = objectMapper.readValue(response, new TypeReference<List<String>>() {});
        Assertions.assertEquals(List.of("A1"), actualResponse);
    }

    @Test
    public void setActionConditionsTest() throws Exception {
        Scenario sc = new Scenario("default");
        sc.setIntentionMapping(new HashMap<>());
        Action a1 = new Action();
        a1.setName("A1");
        sc.setActions(new ArrayList<>(List.of(a1)));
        when(scenarioService.getScenarioByName("default")).thenReturn(sc);
        List<String> actions = new ArrayList<>(List.of("A1"));
        String requestBody = objectMapper.writeValueAsString(actions);
        MvcResult res = mockMvc.perform(put("/scenarios/default/intents/intent/mapping/actions")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isOk()).andReturn();
        String response = res.getResponse().getContentAsString();
        List<String> actualResponse = objectMapper.readValue(response, new TypeReference<List<String>>() {});
        Assertions.assertEquals(List.of("A1"), actualResponse);
    }

    @Test
    public void getConditionsTest() throws Exception {
        Scenario sc = new Scenario("default");
        Belief b = new Belief("B1", "B1", 0.3f);
        Map<String, BeliefMap> im = new HashMap<>();
        BeliefMap bm = new BeliefMap();
        Map<Belief, BoundaryCheck> sbc = new HashMap<>();
        sbc.put(b, BoundaryCheck.EQ);
        bm.setBeliefConditions(sbc);
        Map<Belief, Float> sbcv = new HashMap<>();
        sbcv.put(b, 0.7f);
        bm.setBeliefConditionValues(sbcv);
        im.put("I1", bm);
        sc.setIntentionMapping(im);
        sc.setKnowledgeList(new ArrayList<>(List.of(new Knowledge("I1", "", ""))));
        when(scenarioService.getScenarioByName("default")).thenReturn(sc);
        MvcResult res = mockMvc.perform(get("/scenarios/default/intents/I1/mapping/conditions"))
                .andExpect(status().isOk()).andReturn();
        String response = res.getResponse().getContentAsString();
        List<GenericBelief<BoundaryCheck>> actualResponse = objectMapper.readValue(response
                , new TypeReference<List<GenericBelief<BoundaryCheck>>>() {});
        Assertions.assertEquals(List.of(new GenericBelief<>("B1", BoundaryCheck.EQ, 0.7f)), actualResponse);
    }

    @Test
    public void setConditionsTest() throws Exception {
        Scenario sc = new Scenario("default");
        Belief b = new Belief("B1", "B1", 0.3f);
        b.setId(1L);
        sc.setBeliefs(new ArrayList<>(List.of(b)));
        Map<String, BeliefMap> im = new HashMap<>();
        BeliefMap bm = new BeliefMap();
        Map<Belief, BoundaryCheck> sbc = new HashMap<>();
        bm.setBeliefConditions(sbc);
        Map<Belief, Float> sbcv = new HashMap<>();
        bm.setBeliefConditionValues(sbcv);
        im.put("I1", bm);
        sc.setIntentionMapping(im);
        sc.setKnowledgeList(new ArrayList<>(List.of(new Knowledge("I1", "", ""))));
        when(scenarioService.getScenarioByName("default")).thenReturn(sc);
        List<GenericBelief<BoundaryCheck>> gbList = List.of(
                new GenericBelief<>("B1", BoundaryCheck.EQ, 0.7f));
        String requestBody = objectMapper.writeValueAsString(gbList);
        MvcResult res = mockMvc.perform(put("/scenarios/default/intents/I1/mapping/conditions")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isOk()).andReturn();
        String response = res.getResponse().getContentAsString();
        List<GenericBelief<BoundaryCheck>> actualResponse = objectMapper.readValue(response
                , new TypeReference<List<GenericBelief<BoundaryCheck>>>() {});
        Assertions.assertEquals(gbList, actualResponse);
    }

    @Test
    public void getMappingTest() throws Exception {
        Scenario sc = new Scenario("default");
        sc.setKnowledgeList(new ArrayList<>(List.of(new Knowledge("I1", "", ""))));
        Belief b = new Belief("B1", "B1", 0.3f);
        sc.setBeliefs(new ArrayList<>(List.of(b)));
        Map<String, BeliefMap> im = new HashMap<>();
        BeliefMap bm = new BeliefMap();
        Map<Belief, BeliefUpdateType> sbm = new HashMap<>();
        sbm.put(b, BeliefUpdateType.SET_TO);
        bm.setBeliefMod(sbm);
        Map<Belief, Float> sbma = new HashMap<>();
        sbma.put(b, 0.7f);
        bm.setBeliefMapping(sbma);
        im.put("I1", bm);
        sc.setIntentionMapping(im);
        when(scenarioService.getScenarioByName("default")).thenReturn(sc);
        MvcResult res = mockMvc.perform(get("/scenarios/default/intents/I1/mapping"))
                .andExpect(status().isOk()).andReturn();
        String response = res.getResponse().getContentAsString();
        List<GenericBelief<BeliefUpdateType>> actualResponse = objectMapper.readValue(response
                , new TypeReference<List<GenericBelief<BeliefUpdateType>>>() {});
        Assertions.assertEquals(List.of(new GenericBelief<>("B1", BeliefUpdateType.SET_TO, 0.7f)), actualResponse);
    }

    @Test
    public void setMappingTest() throws Exception {
        Scenario sc = new Scenario("default");
        sc.setKnowledgeList(new ArrayList<>(List.of(new Knowledge("I1", "", ""))));
        Belief b = new Belief("B1", "B1", 0.3f);
        b.setId(1L);
        sc.setBeliefs(new ArrayList<>(List.of(b)));
        Map<String, BeliefMap> im = new HashMap<>();
        BeliefMap bm = new BeliefMap();
        Map<Belief, BeliefUpdateType> sbm = new HashMap<>();
        bm.setBeliefMod(sbm);
        Map<Belief, Float> sbma = new HashMap<>();
        bm.setBeliefMapping(sbma);
        im.put("I1", bm);
        sc.setIntentionMapping(im);
        when(scenarioService.getScenarioByName("default")).thenReturn(sc);
        List<GenericBelief<BeliefUpdateType>> gbList = new ArrayList<>(List.of(
                new GenericBelief<>("B1", BeliefUpdateType.SET_TO, 0.7f)));
        String requestBody = objectMapper.writeValueAsString(gbList);
        MvcResult res = mockMvc.perform(put("/scenarios/default/intents/I1/mapping")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isOk()).andReturn();
        String response = res.getResponse().getContentAsString();
        List<GenericBelief<BeliefUpdateType>> actualResponse = objectMapper.readValue(response
                , new TypeReference<List<GenericBelief<BeliefUpdateType>>>() {});
        Assertions.assertEquals(List.of(new GenericBelief<>("B1", BeliefUpdateType.SET_TO, 0.7f)), actualResponse);
    }

}
