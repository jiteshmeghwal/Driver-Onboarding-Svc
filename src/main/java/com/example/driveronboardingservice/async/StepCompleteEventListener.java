package com.example.driveronboardingservice.async;

import com.example.driveronboardingservice.client.TrackingDeviceOrderClient;
import com.example.driveronboardingservice.constant.OnboardingStepType;
import com.example.driveronboardingservice.entity.OnboardingStep;
import com.example.driveronboardingservice.exception.ValidationException;
import com.example.driveronboardingservice.model.OnboardingStepDTO;
import com.example.driveronboardingservice.model.event.StepCompleteEvent;
import com.example.driveronboardingservice.model.request.CreateShipmentRequest;
import com.example.driveronboardingservice.service.DriverProfileService;
import com.example.driveronboardingservice.service.OnboardingStepService;
import com.example.driveronboardingservice.service.ShipmentService;
import com.example.driveronboardingservice.service.auth.CustomUserDetailsService;
import jakarta.transaction.Transactional;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.retry.annotation.Retryable;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
public class StepCompleteEventListener {
    private static final Logger logger = LogManager.getLogger(StepCompleteEventListener.class);

    @Autowired
    private OnboardingStepService onboardingStepService;

    @Autowired
    private TrackingDeviceOrderClient trackingDeviceOrderClient;

    @Autowired
    private DriverProfileService driverProfileService;

    @Autowired
    private CustomUserDetailsService customUserDetailsService;

    @Autowired
    private ShipmentService shipmentService;


    @Async
    @Transactional
    @EventListener
    @Retryable
    public void onStepCompletion(StepCompleteEvent event) throws ValidationException {
        logger.info("Received stepId {} completion event for user {}",
                event.getOnboardingStep().getStepId(), event.getUserId());
        OnboardingStepType completedStepType = OnboardingStepType
                .getByCode(event.getOnboardingStep().getStepTypeCd());
        switch(completedStepType) {
            case DOC_UPLOAD :
                    Optional<OnboardingStepDTO> backgroundVerificationStep = onboardingStepService
                            .getOnboardingStepsByDriver(event.getUserId())
                            .stream()
                            .filter(onboardingStep -> OnboardingStepType.BACKGROUND_VERIFICATION.getCode()
                                    .equals(onboardingStep.getStepTypeCd()))
                            .findFirst();
                    if(backgroundVerificationStep.isPresent()) {
                        onboardingStepService.updateCompleteStatus(backgroundVerificationStep.get().getStepId(),
                                event.getUserId(), false);
                    }
        }

        Optional<OnboardingStepDTO> nextIncompleteStep = onboardingStepService.getNextIncompleteStep(
                event.getUserId()
        );
        if(nextIncompleteStep.isPresent()) {
            if (OnboardingStepType.SHIPMENT.getCode().equals(nextIncompleteStep.get().getStepTypeCd())) {
                try {
                    shipmentService.createShipment(new CreateShipmentRequest(nextIncompleteStep.get().getStepId(), event.getUserId()));
                } catch (Exception e) {
                    logger.error("Failed creating shipment");
                }
            }
        }
    }
}
