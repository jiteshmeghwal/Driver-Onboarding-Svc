package com.example.driveronboardingservice.async;

import com.example.driveronboardingservice.client.TrackingDeviceOrderClient;
import com.example.driveronboardingservice.constant.OnboardingStepType;
import com.example.driveronboardingservice.constant.ShipmentStatus;
import com.example.driveronboardingservice.exception.ResourceNotFoundException;
import com.example.driveronboardingservice.exception.ValidationException;
import com.example.driveronboardingservice.model.OnboardingStepDTO;
import com.example.driveronboardingservice.model.ShipmentDTO;
import com.example.driveronboardingservice.model.event.StepCompleteEvent;
import com.example.driveronboardingservice.model.response.CreateOrderResponse;
import com.example.driveronboardingservice.service.OnboardingStepService;
import com.example.driveronboardingservice.service.ShipmentService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;

import java.time.LocalDateTime;
import java.util.Optional;

@Component
public class StepUpdateEventListener {
    private static final Logger logger = LogManager.getLogger(StepUpdateEventListener.class);

    @Autowired
    private OnboardingStepService onboardingStepService;
    @Autowired
    private TrackingDeviceOrderClient trackingDeviceOrderClient;
    @Autowired
    private ShipmentService shipmentService;


    @Async
    @EventListener
    public void onStepCompletion(StepCompleteEvent event) throws ValidationException {
        logger.info("Received stepId {} completion event for user {}",
                event.getOnboardingStep().getStepId(), event.getUserId());
        OnboardingStepType completedStepType = OnboardingStepType
                .getByCode(event.getOnboardingStep().getStepTypeCd());
        switch (completedStepType) {
            case DOC_UPLOAD:
                //mark background verification step as incomplete, if a new doc is uploaded by user
                Optional<OnboardingStepDTO> backgroundVerificationStep = onboardingStepService
                        .getOnboardingSteps(event.getUserId())
                        .stream()
                        .filter(onboardingStep -> OnboardingStepType.BACKGROUND_VERIFICATION.getCode()
                                .equals(onboardingStep.getStepTypeCd()))
                        .findFirst();
                if (backgroundVerificationStep.isPresent() &&
                        backgroundVerificationStep.get().isComplete()) {
                    OnboardingStepDTO backgroundVerificationOnboardingStep = backgroundVerificationStep.get();
                    backgroundVerificationOnboardingStep.setComplete(false);
                    onboardingStepService.updateOnboardingStep(backgroundVerificationOnboardingStep);
                }
        }


        Optional<OnboardingStepDTO> nextIncompleteStep = onboardingStepService.getNextIncompleteOnboardingStep(
                event.getUserId()
        );
        if(nextIncompleteStep.isPresent()) {
            OnboardingStepType nextIncompleteStepType = OnboardingStepType
                    .getByCode(nextIncompleteStep.get().getStepTypeCd());
            switch (nextIncompleteStepType) {
                case SHIPMENT :
                    try {
                        //initiate tracking device order
                        CreateOrderResponse response = trackingDeviceOrderClient.createOrder(event.getUserId());
                        shipmentService.createShipment(
                                ShipmentDTO.builder()
                                        .orderId(response.getOrderId())
                                        .orderDate(LocalDateTime.now())
                                        .status(ShipmentStatus.ORDERED.getCode())
                                        .driverId(event.getUserId())
                                        .stepId(nextIncompleteStep.get().getStepId()).build()
                        );
                    } catch (HttpServerErrorException | HttpClientErrorException exception) {
                        shipmentService.createShipment(ShipmentDTO.builder()
                                .status(ShipmentStatus.FAILED.getCode())
                                .driverId(event.getUserId())
                                .stepId(nextIncompleteStep.get().getStepId())
                                .build());
                    } catch (ResourceNotFoundException e) {
                        //It will never reach here
                        logger.error("User {} not found", event.getUserId());
                    }
            }
        }
    }

}
