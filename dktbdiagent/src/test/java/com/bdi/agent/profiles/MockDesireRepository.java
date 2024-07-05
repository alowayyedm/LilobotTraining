package com.bdi.agent.profiles;

import com.bdi.agent.model.Desire;
import com.bdi.agent.repository.AgentRepository;
import com.bdi.agent.repository.DesireRepository;
import org.mockito.Mockito;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;

@Profile("mockDesireRepository")
@Configuration
public class MockDesireRepository {
    /**
     * Mocks the desireRepository.
     *
     * @return A mocked AgentRepository.
     */
    @Bean
    @Primary
    public DesireRepository getMockDesireRepository() {
        return Mockito.mock(DesireRepository.class);
    }
}
