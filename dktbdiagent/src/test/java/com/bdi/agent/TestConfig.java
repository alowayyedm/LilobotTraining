package com.bdi.agent;

import com.bdi.agent.utils.FloatComparer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

@TestConfiguration
@PropertySource("classpath:config.properties")
public class TestConfig {
    @Value("${comparerEpsilon}")
    private float comparerEpsilon;

    /**
     * Creates a new FloatComparer.
     *
     * @return the FloatComparer.
     */
    @Bean
    public FloatComparer getFloatComparer() {
        return new FloatComparer(comparerEpsilon);
    }
}
