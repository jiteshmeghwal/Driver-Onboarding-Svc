package com.example.driveronboardingservice.async;

import com.example.driveronboardingservice.client.TrackingDeviceOrderClient;
import com.example.driveronboardingservice.constant.OnboardingStepType;
import com.example.driveronboardingservice.exception.ResourceNotFoundException;
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
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class StepUpdateEventListener {
    private static final Logger logger = LogManager.getLogger(StepUpdateEventListener.class);

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
    public void onStepCompletion(StepCompleteEvent event) throws ValidationException {
        logger.info("Received stepId {} completion event for user {}",
                event.getOnboardingStep().getStepId(), event.getUserId());
        OnboardingStepType completedStepType = OnboardingStepType
                .getByCode(event.getOnboardingStep().getStepTypeCd());
        switch (completedStepType) {
            case DOC_UPLOAD:
                //mark background verification step as incomplete, if a new doc is uploaded by user
                Optional<OnboardingStepDTO> backgroundVerificationStep = onboardingStepService
                        .getOnboardingStepsByDriver(event.getUserId())
                        .stream()
                        .filter(onboardingStep -> OnboardingStepType.BACKGROUND_VERIFICATION.getCode()
                                .equals(onboardingStep.getStepTypeCd()))
                        .findFirst();
                if (backgroundVerificationStep.isPresent() &&
                        backgroundVerificationStep.get().isComplete()) {
                    onboardingStepService.updateCompleteStatus(backgroundVerificationStep.get().getStepId(),
                            event.getUserId(), false);
                }
        }


        Optional<OnboardingStepDTO> nextIncompleteStep = onboardingStepService.getNextIncompleteStep(
                event.getUserId()
        );
        if(nextIncompleteStep.isPresent()) {
            OnboardingStepType nextIncompleteStepType = OnboardingStepType
                    .getByCode(nextIncompleteStep.get().getStepTypeCd());
            switch (nextIncompleteStepType) {
                case SHIPMENT :
                    try {
                        shipmentService.createShipment(
                                new CreateShipmentRequest(nextIncompleteStep.get().getStepId(),
                                        event.getUserId()));
                    } catch (ResourceNotFoundException e) {
                        logger.error("Failed validation while creating shipment: {}", e.getDesc());
                    }
            }
        }
    }

//    @Async
//    @Transactional
//    @EventListener
//    public void onStepUpdate(StepUpdateEvent event) throws ValidationException {
//        logger.info("Received stepId {} update event for user {}",
//                event.getOnboardingStep().getStepId(), event.getUserId());
//        if(event.getOnboardingStep().isComplete()) {
//            OnboardingStepType completedStepType = OnboardingStepType
//                    .getByCode(event.getOnboardingStep().getStepTypeCd());
//            switch (completedStepType) {
//                case DOC_UPLOAD:
//                    //mark background verification step as incomplete, if a new doc is uploaded by user
//                    Optional<OnboardingStepDTO> backgroundVerificationStep = onboardingStepService
//                            .getOnboardingStepsByDriver(event.getUserId())
//                            .stream()
//                            .filter(onboardingStep -> OnboardingStepType.BACKGROUND_VERIFICATION.getCode()
//                                    .equals(onboardingStep.getStepTypeCd()))
//                            .findFirst();
//                    if (backgroundVerificationStep.isPresent() &&
//                            backgroundVerificationStep.get().isComplete()) {
//                        onboardingStepService.updateCompleteStatus(backgroundVerificationStep.get().getStepId(),
//                                event.getUserId(), false);
//                    }
//                    break;
////                case BACKGROUND_VERIFICATION:
////                    //mark profile as verified
//            }
//        }
//
//        Optional<OnboardingStepDTO> nextIncompleteStep = onboardingStepService.getNextIncompleteStep(
//                event.getUserId()
//        );
//
//        if(nextIncompleteStep.isPresent()) {
//            OnboardingStepType nextIncompleteStepType = OnboardingStepType
//                    .getByCode(event.getOnboardingStep().getStepTypeCd());
//            switch (nextIncompleteStepType) {
////                case DOC_UPLOAD:
////                    //if next incomplete step is doc_upload, mark user profile as backlog
////                    break;
////                case BACKGROUND_VERIFICATION:
////                    //if next incomplete step is background verification, mark user as pending_verification
////                    break;
//                case SHIPMENT :
//                    try {
//                        shipmentService.createShipment(
//                                new CreateShipmentRequest(nextIncompleteStep.get().getStepId()
//                                        , event.getUserId()));
//                    } catch (Exception e) {
//                        logger.error("Failed validation while creating shipment: {}", e.getMessage());
//                    }
//            }
//        }
//    }
}
