package com.bdi.agent.domain;

import com.bdi.agent.model.Agent;
import com.bdi.agent.model.Belief;
import com.bdi.agent.model.ExceptionalBelief;
import com.bdi.agent.model.Scenario;
import com.bdi.agent.repository.BeliefRepository;
import com.bdi.agent.service.AgentService;
import com.bdi.agent.service.BeliefService;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.assertj.core.data.Offset;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import javax.persistence.EntityNotFoundException;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;


@ExtendWith(SpringExtension.class)
@SpringBootTest
@ActiveProfiles({"mockBeliefRepository"})
@TestPropertySource(locations="classpath:application-test.properties")
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
public class BeliefServiceTest {

    @Autowired
    private transient BeliefRepository mockBeliefRepository;

    @Autowired
    private transient BeliefService beliefService;

    // is not used but prevents many errors from appearing
    @MockBean
    private AgentService agentService;

    private Scenario testScenario;

    @BeforeEach
    public void setup() {
        Scenario simpleTestScenario = new Scenario("test");
        simpleTestScenario.setKnowledgeList(new ArrayList<>());
        simpleTestScenario.setConditions(new ArrayList<>());
        simpleTestScenario.setBeliefs(new ArrayList<>());
        simpleTestScenario.setDesires(new ArrayList<>());
        simpleTestScenario.setIntentionMapping(new HashMap<>());
        simpleTestScenario.setActions(new ArrayList<>());

        this.testScenario = simpleTestScenario;
    }

    @Test
    public void updateBeliefValid() {
        Agent agent = new Agent();
        agent.setUserId("testId");
        agent.setKnowledgeFile("test");
        agent.setIntentionId(0L);
        agent.setCurrentSubject("");
        agent.isActive(true);
        agent.setCurrentAction(0L);
        agent.setScore(0.0f);
        agent.isTrainerResponding(false);

        Belief belief = new Belief(0L, "B1", "", "", 0.3f);
        this.testScenario.setBeliefs(new ArrayList<>(List.of(belief)));
        agent.setScenario(this.testScenario);
        beliefService.setBeliefValue(agent, "B1", 0.5f);

        ArgumentCaptor<Belief> argumentCaptor = ArgumentCaptor.forClass(Belief.class);
        verify(mockBeliefRepository).save(argumentCaptor.capture());
        assertEquals(0.5f, argumentCaptor.getValue().getValue());
    }

    @Test
    public void updateBeliefInvalid() {
        Agent agent = new Agent();
        agent.setUserId("testId");
        agent.setKnowledgeFile("test");
        agent.setIntentionId(0L);
        agent.setCurrentSubject("");
        agent.isActive(true);
        agent.setCurrentAction(0L);
        agent.setScore(0.0f);
        agent.isTrainerResponding(false);
        agent.setScenario(this.testScenario);

        assertThrows(EntityNotFoundException.class, () -> {
            beliefService.setBeliefValue(agent, "B1", 0.5f);
        });
    }

    @Test
    public void testReadBeliefsFromCsv() {
        beliefService.setBeliefsFile("src/test/java/com/bdi/agent/files/testBeliefs.csv");
        HashSet<Belief> expected = new HashSet<>();

        for (String id : new String[] {"B1", "B2", "B3", "B4", "B5", "B6", "B10" }) {
            expected.add(new Belief(null, id, "Ik denk dat KT mij begrijpt", "alle fasen", 0.0f));
        }
        for (String[] pair : new String[][] {
                            { "B7", "reason unknown" },
                            { "B8", "" },
                            { "B9", "" },
                            { "B11", "Depends on B4" },
                            { "B12", "Depends on B4 and B5" },
                            { "B13", "Depends on B4, B5, B6, B7" },
                            { "B14", "Depends on B4, B5, B6, B7" },
                            { "B15", "Depends on B4, B5, B6, B7" }
                    }) {
            expected.add(new ExceptionalBelief(null, pair[0], "Ik denk dat KT mij begrijpt", "alle fasen", 0.0f, pair[1]));
        }
        HashSet<Belief> beliefs = beliefService.readBeliefsFromCsv();
        assertEquals(beliefs, expected);
    }

    @Test
    void testBeliefsSorted() {
        List<Belief> beliefs = new ArrayList<>();

        Belief b1 = new Belief("B1", "Ik voel me in controle in het gesprek", 0.5f);
        Belief b7 = new Belief("B7", "Ik denk dat KT mij kan helpen", 0.7f);
        Belief b13 = new Belief("B13", "Ik denk dat Juf Ellie mij kan helpen", 0.5f);

        beliefs.add(b7);
        beliefs.add(b1);
        beliefs.add(b13);

        List<Belief> sortedBeliefs = beliefService.sortBeliefsByName(beliefs);

        List<Belief> expectedSortedBeliefs = List.of(b1, b7, b13);
        assertEquals(expectedSortedBeliefs, sortedBeliefs);
    }

    @Test
    void testBeliefsSortedInvalid_1() {
        List<Belief> beliefs = new ArrayList<>();

        Belief b1 = new Belief("B1", "Ik voel me in controle in het gesprek", 0.5f);
        Belief ba = new Belief("BA", "Ik denk dat KT mij kan helpen", 0.7f);

        beliefs.add(ba);
        beliefs.add(b1);

        assertThrows(IllegalArgumentException.class, () -> {
            beliefService.sortBeliefsByName(beliefs);
        });
    }

    @Test
    void testBeliefsSortedInvalid_2() {
        List<Belief> beliefs = new ArrayList<>();
        Belief b = new Belief("B", "Ik voel me in controle in het gesprek", 0.5f);
        Belief b1 = new Belief("B1", "Ik voel me in controle in het gesprek", 0.5f);

        beliefs.add(b);
        beliefs.add(b1);

        assertThrows(IllegalArgumentException.class, () -> {
            beliefService.sortBeliefsByName(beliefs);
        });
    }

    @Test
    void testBeliefsSortedInvalid_3() {
        List<Belief> beliefs = new ArrayList<>();
        Belief b = new Belief("", "Ik voel me in controle in het gesprek", 0.5f);
        Belief b1 = new Belief("B1", "Ik voel me in controle in het gesprek", 0.5f);

        beliefs.add(b);
        beliefs.add(b1);

        assertThrows(IndexOutOfBoundsException.class, () -> {
            beliefService.sortBeliefsByName(beliefs);
        });
    }

    @Test
    void testGetAllInitialBeliefsSorted() {
        // This test only asserts one specific scenario since it should simply return the result of
        // sortBeliefsByName. Also verifies that specific methods are called.

        HashSet<Belief> mockBeliefs = new HashSet<>();

        Belief b1 = new Belief("B1", "Ik voel me in controle in het gesprek", 0.5f);
        Belief b7 = new Belief("B7", "Ik denk dat KT mij kan helpen", 0.7f);
        Belief b13 = new Belief("B13", "Ik denk dat Juf Ellie mij kan helpen", 0.5f);

        mockBeliefs.add(b7);
        mockBeliefs.add(b1);
        mockBeliefs.add(b13);

        BeliefService spyBeliefService = Mockito.spy(beliefService);
        Mockito.doReturn(mockBeliefs).when(spyBeliefService).readBeliefsFromCsv();

        List<Belief> sortedBeliefs = spyBeliefService.getAllInitialBeliefsSorted();

        List<Belief> expectedSortedBeliefs = List.of(b1, b7, b13);
        assertEquals(expectedSortedBeliefs, sortedBeliefs);

        verify(spyBeliefService).sortBeliefsByName(mockBeliefs.stream().toList());
        verify(spyBeliefService).readBeliefsFromCsv();
    }
}
