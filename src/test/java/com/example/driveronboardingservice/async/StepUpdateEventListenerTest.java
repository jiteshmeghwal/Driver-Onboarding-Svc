package com.example.driveronboardingservice.async;

import com.example.driveronboardingservice.async.kafka.producer.ProfileVerificationEventProducer;
import com.example.driveronboardingservice.async.publisher.EventPublisher;
import com.example.driveronboardingservice.constant.EventType;
import com.example.driveronboardingservice.constant.OnboardingStepType;
import com.example.driveronboardingservice.exception.ValidationException;
import com.example.driveronboardingservice.model.OnboardingStepDTO;
import com.example.driveronboardingservice.model.event.AbstractEvent;
import com.example.driveronboardingservice.model.event.StepCompleteEvent;
import com.example.driveronboardingservice.service.OnboardingStepService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Clock;
import java.util.Optional;

@ExtendWith(MockitoExtension.class)
class StepUpdateEventListenerTest {

    @Mock
    private OnboardingStepService onboardingStepService;
    @Mock
    private ProfileVerificationEventProducer profileVerificationEventProducer;
    @Mock
    private EventPublisher eventPublisher;

    @InjectMocks
    private StepUpdateEventListener stepUpdateEventListener;
    private final String driverId = "user";

    @Test
    void onStepCompletion_NoNextStep() throws ValidationException {
        Mockito.when(onboardingStepService.getNextIncompleteOnboardingStep(driverId)).thenReturn(Optional.empty());
        stepUpdateEventListener.onStepCompletion(getStepCompleteEvent());
        Mockito.verify(profileVerificationEventProducer, Mockito.times(0)).produce(driverId);
        Mockito.verify(eventPublisher, Mockito.times(0)).publishEvent(Mockito.any(AbstractEvent.class));
    }

    @Test
    void onStepCompletion_NextStepBackgroundVerification() throws ValidationException {
        Mockito.when(onboardingStepService.getNextIncompleteOnboardingStep(driverId))
                .thenReturn(Optional.of(OnboardingStepDTO.builder()
                        .stepTypeCd(OnboardingStepType.BACKGROUND_VERIFICATION.getCode()).build()));
        stepUpdateEventListener.onStepCompletion(getStepCompleteEvent());
        Mockito.verify(profileVerificationEventProducer, Mockito.times(1)).produce(driverId);
        Mockito.verify(eventPublisher, Mockito.times(0)).publishEvent(Mockito.any(AbstractEvent.class));
    }

    @Test
    void onStepCompletion_NextStepShipment() throws ValidationException {
        Mockito.when(onboardingStepService.getNextIncompleteOnboardingStep(driverId))
                .thenReturn(Optional.of(OnboardingStepDTO.builder()
                        .stepTypeCd(OnboardingStepType.SHIPMENT.getCode()).build()));
        stepUpdateEventListener.onStepCompletion(getStepCompleteEvent());
        Mockito.verify(profileVerificationEventProducer, Mockito.times(0)).produce(driverId);
        Mockito.verify(eventPublisher, Mockito.times(1)).publishEvent(Mockito.any(AbstractEvent.class));
    }

    private StepCompleteEvent getStepCompleteEvent() {
        StepCompleteEvent stepCompleteEvent = new StepCompleteEvent(this, Clock.systemUTC());
        stepCompleteEvent.setEventType(EventType.STEP_COMPLETE);
        stepCompleteEvent.setUserId(driverId);
        stepCompleteEvent.setOnboardingStep(OnboardingStepDTO.builder()
                .stepId((short)1)
                .stepTypeCd(OnboardingStepType.DOC_UPLOAD.getCode()).build());
        return  stepCompleteEvent;
    }

}