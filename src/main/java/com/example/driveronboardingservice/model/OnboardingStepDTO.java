package com.example.driveronboardingservice.model;

import lombok.*;

@Data
@Builder
public class OnboardingStepDTO {
    private Short stepId;
    private Short stepTypeCd;
    private String stepTypeDesc;
    private String stepTitle;
    private String stepDesc;
    private boolean isComplete;
}
