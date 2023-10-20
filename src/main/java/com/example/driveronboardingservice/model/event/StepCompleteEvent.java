package com.example.driveronboardingservice.model.event;

import com.example.driveronboardingservice.model.OnboardingStepDTO;
import lombok.Getter;
import lombok.Setter;

import java.time.Clock;

@Getter
@Setter
public class StepCompleteEvent extends AbstractEvent{
    OnboardingStepDTO onboardingStep;

    public StepCompleteEvent(Object source, Clock clock) {
        super(source, clock);
    }
}
