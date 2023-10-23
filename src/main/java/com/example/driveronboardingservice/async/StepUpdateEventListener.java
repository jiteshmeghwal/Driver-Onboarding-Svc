package com.example.driveronboardingservice.async;

import com.example.driveronboardingservice.async.kafka.producer.ProfileVerificationEventProducer;
import com.example.driveronboardingservice.constant.OnboardingStepType;
import com.example.driveronboardingservice.exception.ValidationException;
import com.example.driveronboardingservice.model.OnboardingStepDTO;
import com.example.driveronboardingservice.model.event.StepCompleteEvent;
import com.example.driveronboardingservice.service.OnboardingStepService;
import com.example.driveronboardingservice.service.TrackingDeviceOrderService;
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
    private TrackingDeviceOrderService trackingDeviceOrderService;
    @Autowired
    private ProfileVerificationEventProducer profileVerificationEventProducer;

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
                onboardingStepService.updateStepStatusByStepType(event.getUserId(),
                        OnboardingStepType.BACKGROUND_VERIFICATION, false);
        }

        Optional<OnboardingStepDTO> nextIncompleteStep = onboardingStepService.getNextIncompleteOnboardingStep(
                event.getUserId()
        );
        if(nextIncompleteStep.isPresent()) {
            OnboardingStepType nextIncompleteStepType = OnboardingStepType
                    .getByCode(nextIncompleteStep.get().getStepTypeCd());
            switch (nextIncompleteStepType) {
                case BACKGROUND_VERIFICATION:
                    //send driver's profile to background verification system.
                    //background verification can be automatic/ manual
                    //automatic system can be AI based systems, validating user
                    //uploaded documents via DL algorithms
                    profileVerificationEventProducer.produce(event.getUserId());
                    break;
                case SHIPMENT :
                    trackingDeviceOrderService.initiateOrder(event.getUserId(),
                            nextIncompleteStep.get().getStepId());
            }
        }
    }

}
