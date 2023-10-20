package com.example.driveronboardingservice.async;

import com.example.driveronboardingservice.client.TrackingDeviceOrderClient;
import com.example.driveronboardingservice.constant.OnboardingStepType;
import com.example.driveronboardingservice.exception.ResourceNotFoundException;
import com.example.driveronboardingservice.exception.ValidationException;
import com.example.driveronboardingservice.model.DriverDTO;
import com.example.driveronboardingservice.model.OnboardingStepDTO;
import com.example.driveronboardingservice.model.ShipmentDTO;
import com.example.driveronboardingservice.model.auth.CustomUser;
import com.example.driveronboardingservice.model.event.StepCompleteEvent;
import com.example.driveronboardingservice.model.request.*;
import com.example.driveronboardingservice.model.response.CreateOrderResponse;
import com.example.driveronboardingservice.service.DriverProfileService;
import com.example.driveronboardingservice.service.OnboardingStepService;
import com.example.driveronboardingservice.service.ShipmentService;
import com.example.driveronboardingservice.service.auth.CustomUserDetailsService;
import jakarta.transaction.Transactional;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.EventListener;
import org.springframework.retry.annotation.Retryable;
import org.springframework.scheduling.annotation.Async;

import java.util.Optional;

public class StepCompleteEventListener implements ApplicationListener<StepCompleteEvent> {
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


    @Override
    @Async
    @Transactional
    @EventListener
    @Retryable
    public void onApplicationEvent(StepCompleteEvent event) {
        logger.info("Received stepId {} completion event for user {}",
                event.getOnboardingStep().getStepId(), event.getUserId());

        Optional<OnboardingStepDTO> nextIncompleteStep = onboardingStepService.getNextIncompleteStep(
                event.getUserId()
        );
        if(nextIncompleteStep.isPresent()) {
            if (OnboardingStepType.SHIPMENT.getCode().equals(nextIncompleteStep.get().getStepTypeCd())) {
                try {
                    shipmentService.createShipment(new CreateShipmentRequest(event.getOnboardingStep().getStepId(), event.getUserId()));
                } catch (Exception e) {
                    logger.error("Failed creating shipment");
                }
            }
        }
    }
}
