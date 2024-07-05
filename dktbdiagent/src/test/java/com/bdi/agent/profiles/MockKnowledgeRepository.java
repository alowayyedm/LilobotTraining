package com.bdi.agent.profiles;

import com.bdi.agent.repository.KnowledgeRepository;
import org.mockito.Mockito;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;

@Profile("mockKnowledgeRepository")
@Configuration
public class MockKnowledgeRepository {
    /**
     * Mocks the knowledgeRepository.
     *
     * @return A mocked AgentRepository.
     */
    @Bean
    @Primary
    public KnowledgeRepository getMockAgentRepository() {
        return Mockito.mock(KnowledgeRepository.class);
    }
}
