package com.example.driveronboardingservice.model.request;

import lombok.Data;

@Data
public class UpdateOnboardingStepRequest {
    boolean complete;
    String additionalComments;
}
