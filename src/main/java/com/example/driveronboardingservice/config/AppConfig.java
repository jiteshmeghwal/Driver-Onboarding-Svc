package com.example.driveronboardingservice.config;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

import java.util.ArrayList;
import java.util.List;

@Configuration
@Data
public class AppConfig {
    @Value("${onboarding.step.type.sequence}")
    private List<Short> onboardingStepTypeSequence = new ArrayList<>();
}
