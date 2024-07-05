//package com.bdi.agent.fullFlowTests;
//
//import static org.junit.jupiter.api.Assertions.assertEquals;
//import static org.junit.jupiter.api.Assertions.fail;
//
//import com.bdi.agent.model.Agent;
//import com.bdi.agent.model.Belief;
//import com.bdi.agent.model.BeliefMap;
//import com.bdi.agent.model.Desire;
//import com.bdi.agent.model.Knowledge;
//import com.bdi.agent.model.Perception;
//import com.bdi.agent.model.PhaseConditions;
//import com.bdi.agent.model.Scenario;
//import com.bdi.agent.model.enums.BeliefName;
//import com.bdi.agent.model.enums.BeliefUpdateType;
//import com.bdi.agent.model.enums.BoundaryCheck;
//import com.bdi.agent.model.enums.Phase;
//import com.bdi.agent.model.util.BeliefConstraint;
//import com.bdi.agent.repository.AgentRepository;
//import com.bdi.agent.repository.BeliefRepository;
//import com.bdi.agent.repository.DesireRepository;
//import com.bdi.agent.repository.KnowledgeRepository;
//import com.bdi.agent.repository.LogEntryRepository;
//import com.bdi.agent.repository.ScenarioRepository;
//import com.bdi.agent.service.AgentService;
//import com.bdi.agent.service.LogEntryService;
//import java.util.ArrayList;
//import java.util.HashMap;
//import java.util.HashSet;
//import java.util.List;
//import java.util.Map;
//import java.util.Optional;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.context.SpringBootTest;
//import org.springframework.boot.test.mock.mockito.MockBean;
//import org.springframework.test.context.ActiveProfiles;
//import org.springframework.test.context.TestPropertySource;
//import org.springframework.transaction.annotation.Transactional;
//
//@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
//@TestPropertySource(locations="classpath:application-test.properties")
//public class AgentServiceFullFlowTests {
//
//    static {
//        System.setProperty("AUTH_TOKEN", "test");
//    }
//
//    @Autowired
//    private AgentService agentService;
//
//    @Autowired
//    private AgentRepository agentRepository;
//
//    private Agent agent;
//    @BeforeEach
//    public void setup() {
//        Scenario scenario = createCustomTestScenario();
//        Agent agent = new Agent();
//        agent.setUserId("testId");
//        agent.setKnowledgeFile("test");
//        agent.setIntentionId(0L);
//        agent.setCurrentSubject("");
//        agent.isActive(true);
//        agent.setCurrentAction(0L);
//        agent.setScore(0.0f);
//        agent.isTrainerResponding(false);
//        agent.setScenario(scenario);
//        try {
//            agentRepository.save(agent);
//        } catch (Exception e) {
//            throw e;
//        }
//        this.agent = agent;
//    }
//
//    @Test
//    @Transactional
//    public void testBeliefGetsUpdatedOnReason() {
//        agentService.reason(agent, new Perception("request", "update", "b1", "some message"));
//        Agent updatedAgent = agentRepository.getById(agent.getId());
//        Optional<Belief> beliefOptional = updatedAgent.getScenario().getBeliefs().stream().filter(x -> x.getName() == "B1").findFirst();
//        if (beliefOptional.isEmpty()) fail();
//        assertEquals(beliefOptional.get().getValue(), 0.1f);
//    }
//
//    @Test
//    @Transactional
//    public void testDesireFirstDesireGetsActivated() {
//        for (int i = 0; i < 7; i++) {
//            agentService.reason(agent, new Perception("request", "update", "b1", "some message"));
//        }
//        Agent updatedAgent = agentRepository.getById(agent.getId());
//        Optional<Belief> beliefOptional =
//                updatedAgent.getScenario().getBeliefs().stream().filter(x -> x.getName() == "B1").findFirst();
//        if (beliefOptional.isEmpty()) fail();
//        assertEquals(beliefOptional.get().getValue(), 0.7f, 0.000001f);
//        Optional<Desire> desireOptional = agent.getScenario().getDesires().stream().filter(x -> x.getName().equals("D1")).findFirst();
//        if (desireOptional.isEmpty()) fail();
//        assertEquals(updatedAgent.getIntentionId(), desireOptional.get().getId());
//    }
//
//    /**
//     * instantiates a very basic scenario to test with
//     * scenario has 2 phases, 2 beliefs and 2 desires
//     * @return created scenario
//     */
//    private Scenario createCustomTestScenario() {
//        Belief B1 = new Belief();
//        B1.setValue(0.0F);
//        B1.setName("B1");
//        B1.setFullName("belief in something");
//
//        Belief B2 = new Belief();
//        B2.setValue(0.0F);
//        B2.setName("B2");
//        B2.setFullName("belief something else");
//
//        Desire D0 = new Desire();
//        D0.setName("D0");
//        D0.setFullName("always desire this");
//        D0.setActions(new HashSet<>());
//
//        Desire D1 = new Desire();
//        D1.setPhase(Phase.PHASE1);
//        D1.setName("D1");
//        D1.setActions(new HashSet<>());
//        D1.setFullName("desire something");
//
//        Desire D2 = new Desire();
//        D2.setPhase(Phase.PHASE2);
//        D2.setName("D2");
//        D2.setActions(new HashSet<>());
//        D2.setFullName("desire something else");
//
//        BeliefConstraint bc0 = new BeliefConstraint();
//        bc0.setBelief(B1);
//        bc0.setBoundaryCheck(BoundaryCheck.LT);
//        bc0.setGoalValue(0.6F);
//        bc0.setBeliefName(BeliefName.B1);
//
//        BeliefConstraint bc1 = new BeliefConstraint();
//        bc1.setBelief(B1);
//        bc1.setBoundaryCheck(BoundaryCheck.GEQ);
//        bc1.setGoalValue(0.6F);
//        bc1.setBeliefName(BeliefName.B1);
//
//        BeliefConstraint bc2 = new BeliefConstraint();
//        bc2.setBelief(B1);
//        bc2.setBoundaryCheck(BoundaryCheck.GEQ);
//        bc2.setGoalValue(0.8F);
//        bc2.setBeliefName(BeliefName.B2);
//
//        BeliefConstraint bc3 = new BeliefConstraint();
//        bc3.setBelief(B2);
//        bc3.setBoundaryCheck(BoundaryCheck.GEQ);
//        bc3.setGoalValue(0.5F);
//        bc3.setBeliefName(BeliefName.B3);
//
//        PhaseConditions pc0 = new PhaseConditions();
//        pc0.setConditions(new ArrayList<>(List.of(bc0)));
//        pc0.setDesire(D0);
//
//        PhaseConditions pc1 = new PhaseConditions();
//        pc1.setConditions(new ArrayList<>(List.of(bc1)));
//        pc1.setDesire(D1);
//
//        PhaseConditions pc2 = new PhaseConditions();
//        pc2.setConditions(new ArrayList<>(List.of(bc2, bc3)));
//        pc2.setDesire(D2);
//
//        Scenario fullTestScenario = new Scenario();
//
//        BeliefMap bm1 = new BeliefMap();
//        bm1.setBeliefMapping(new HashMap<>(Map.of(B1, 0.1F)));
//        bm1.setBeliefMod(new HashMap<>(Map.of(B1, BeliefUpdateType.INCREASE)));
//
//        BeliefMap bm2 = new BeliefMap();
//        bm2.setBeliefMapping(new HashMap<>(Map.of(B2, 0.1F)));
//        bm2.setBeliefMod(new HashMap<>(Map.of(B2, BeliefUpdateType.INCREASE)));
//
//        Knowledge k1 = new Knowledge();
//        k1.setValues(new ArrayList<>(List.of("response")));
//        k1.setKnowledge("test");
//        k1.setType("request");
//        k1.setSubject("update");
//        k1.setAttribute("b1");
//
//        Knowledge k2 = new Knowledge();
//        k2.setKnowledge("test");
//        k2.setValues(new ArrayList<>(List.of("response")));
//        k2.setType("request");
//        k2.setSubject("update");
//        k2.setAttribute("b2");
//
//        List<Desire> desires = new ArrayList<>(List.of(D0, D1, D2));
//        List<Belief> beliefs = new ArrayList<>(List.of(B1, B2));
//        List<Knowledge> knowledges = new ArrayList<>(List.of(k1, k2));
//
//        fullTestScenario.setDesires(desires);
//        fullTestScenario.setBeliefs(beliefs);
//        fullTestScenario.setConditions(new ArrayList<>(List.of(pc0, pc1, pc2)));
//        fullTestScenario.setIntentionMapping(new HashMap<>(Map.of("request_update_b1", bm1, "request_update_b2", bm2)));
//        fullTestScenario.setKnowledgeList(knowledges);
////        desires.forEach(desire -> mockDesireRepository.save(desire));
////        beliefs.forEach(belief -> mockBeliefRepository.save(belief));
////        knowledges.forEach(knowledge -> mockKnowledgeRepository.save(knowledge));
//
//        return fullTestScenario;
//    }
//}
