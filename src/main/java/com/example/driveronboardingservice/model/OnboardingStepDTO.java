package com.example.driveronboardingservice.model;

import lombok.*;

@Data
@Builder
public class OnboardingStepDTO {
    private long stepId;
    private short stepTypeCd;
    private String stepTypeDesc;
    private String stepTitle;
    private String stepDesc;
    private boolean isComplete;
}
