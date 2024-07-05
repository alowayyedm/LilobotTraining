package com.bdi.agent.utils;

import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
@NoArgsConstructor
@Getter
public class ValueConfiguration {
    @Value("${oneStep}")
    public float oneStep;
    @Value("${twoSteps}")
    public float twoStep;
    @Value("${minThreshold}")
    public float minThreshold;
    @Value("${midThreshold}")
    public float midThreshold;
    @Value("${maxThreshold}")
    public float maxThreshold;
    @Value("${minValue}")
    public float minValue;
    @Value("${maxValue}")
    public float maxValue;
}
