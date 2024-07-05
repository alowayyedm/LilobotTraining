package com.bdi.agent.profiles;

import com.bdi.agent.repository.KnowledgeRepository;
import com.bdi.agent.repository.ScenarioRepository;
import org.mockito.Mockito;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;

@Profile("mockScenarioRepository")
@Configuration
public class MockScenarioRepository {
    /**
     * Mocks the scenarioRepository.
     *
     * @return A mocked AgentRepository.
     */
    @Bean
    @Primary
    public ScenarioRepository getMockAgentRepository() {
        return Mockito.mock(ScenarioRepository.class);
    }
}
