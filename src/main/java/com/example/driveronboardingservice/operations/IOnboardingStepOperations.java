package com.example.driveronboardingservice.operations;

import com.example.driveronboardingservice.exception.ValidationException;
import com.example.driveronboardingservice.model.OnboardingStepDTO;

import java.util.List;
import java.util.Optional;

public interface IOnboardingStepOperations {

    List<OnboardingStepDTO> getOnboardingSteps(String driverId);

    void updateOnboardingStepStatus(Short stepId, String driverId, boolean complete,
                                    String additionalComments) throws ValidationException;

    OnboardingStepDTO getOnboardingStep(Short stepId, String driverId) throws ValidationException;

    Optional<OnboardingStepDTO> getNextIncompleteOnboardingStep(String driverId);
}

