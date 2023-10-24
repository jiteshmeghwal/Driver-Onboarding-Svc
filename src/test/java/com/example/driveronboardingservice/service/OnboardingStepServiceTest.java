package com.example.driveronboardingservice.service;

import com.example.driveronboardingservice.async.publisher.EventPublisher;
import com.example.driveronboardingservice.config.AppConfig;
import com.example.driveronboardingservice.constant.OnboardingStepType;
import com.example.driveronboardingservice.entity.OnboardingStep;
import com.example.driveronboardingservice.entity.OnboardingStepInstance;
import com.example.driveronboardingservice.entity.OnboardingStepInstancePK;
import com.example.driveronboardingservice.exception.ValidationException;
import com.example.driveronboardingservice.model.OnboardingStepDTO;
import com.example.driveronboardingservice.repository.OnboardingStepInstanceRepository;
import com.example.driveronboardingservice.repository.OnboardingStepRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;

import javax.swing.text.html.Option;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class OnboardingStepServiceTest {
    @Mock
    private AppConfig appConfig;
    @Mock
    private OnboardingStepRepository onboardingStepRepository;
    @Mock
    private OnboardingStepInstanceRepository onboardingStepInstanceRepository;
    @Mock
    private EventPublisher eventPublisher;
    @InjectMocks
    OnboardingStepService onboardingStepService;

    private String driverId = "user";
    private Short stepId = (short)1;

    @Test
    void getOnboardingSteps() {
        Mockito.when(onboardingStepInstanceRepository.findAllByDriverId(driverId)).thenReturn(List.of(getMockOnboardingStepInstance()));
        Mockito.when(onboardingStepRepository.findAll()).thenReturn(List.of(getMockOnboardingStep()));
        Mockito.when(appConfig.getOnboardingStepTypeSequence()).thenReturn(List.of(OnboardingStepType.DOC_UPLOAD.getCode()));
        List<OnboardingStepDTO> onboardingSteps = onboardingStepService.getOnboardingSteps(driverId);
        Assertions.assertEquals(1, onboardingSteps.size());
    }

    @Test
    void updateOnboardingStepStatus_ValidationException() {
        Mockito.when(onboardingStepRepository.findAll()).thenReturn(List.of(getMockOnboardingStep()));
        Mockito.when(onboardingStepInstanceRepository
                .findById(Mockito.any())).thenReturn(Optional.empty());
        Assertions.assertThrows(ValidationException.class, ()->onboardingStepService.updateOnboardingStepStatus(stepId, driverId, false, null));
    }

    @Test
    void updateOnboardingStepStatus() throws ValidationException {
        Mockito.when(onboardingStepRepository.findAll()).thenReturn(List.of(getMockOnboardingStep()));
        Mockito.when(onboardingStepInstanceRepository
                .findById(Mockito.any())).thenReturn(Optional.empty());
        onboardingStepService.updateOnboardingStepStatus(stepId, driverId, true, null);
    }

    @Test
    void getOnboardingStep_ValidationException() {

        Assertions.assertThrows(ValidationException.class, ()->onboardingStepService.getOnboardingStep(stepId, driverId));
    }

    @Test
    void getOnboardingStep() throws ValidationException {
        Mockito.when(onboardingStepInstanceRepository.findAllByDriverId(driverId)).thenReturn(List.of(getMockOnboardingStepInstance()));
        Mockito.when(onboardingStepRepository.findAll()).thenReturn(List.of(getMockOnboardingStep()));
        Mockito.when(appConfig.getOnboardingStepTypeSequence()).thenReturn(List.of(OnboardingStepType.DOC_UPLOAD.getCode()));

        onboardingStepService.getOnboardingStep(stepId, driverId);

    }

    @Test
    void getNextIncompleteOnboardingStep() {
        Mockito.when(onboardingStepInstanceRepository.findAllByDriverId(driverId)).thenReturn(List.of(getMockOnboardingStepInstance()));
        Mockito.when(onboardingStepRepository.findAll()).thenReturn(List.of(getMockOnboardingStep()));
        Mockito.when(appConfig.getOnboardingStepTypeSequence()).thenReturn(List.of(OnboardingStepType.DOC_UPLOAD.getCode()));

        Optional<OnboardingStepDTO> nextStep = onboardingStepService.getNextIncompleteOnboardingStep(driverId);
        Assertions.assertTrue(nextStep.isPresent());
        Assertions.assertEquals(OnboardingStepType.DOC_UPLOAD.getCode(), nextStep.get().getStepTypeCd());
    }

    @Test
    void updateStepStatusByStepType() throws ValidationException {
        Mockito.when(onboardingStepInstanceRepository.findAllByDriverId(driverId)).thenReturn(List.of(getMockOnboardingStepInstance()));
        Mockito.when(onboardingStepRepository.findAll()).thenReturn(List.of(getMockOnboardingStep()));
        Mockito.when(appConfig.getOnboardingStepTypeSequence()).thenReturn(List.of(OnboardingStepType.DOC_UPLOAD.getCode()));

        onboardingStepService.updateStepStatusByStepType(driverId, OnboardingStepType.DOC_UPLOAD, true);
    }

    private OnboardingStep getMockOnboardingStep() {
        OnboardingStep onboardingStep = new OnboardingStep();
        onboardingStep.setStepId(stepId);
        onboardingStep.setStepTypeCd(OnboardingStepType.DOC_UPLOAD.getCode());
        return onboardingStep;
    }

    private OnboardingStepInstance getMockOnboardingStepInstance() {
        OnboardingStepInstance onboardingStepInstance = new OnboardingStepInstance();
        OnboardingStepInstancePK onboardingStepInstancePK = new OnboardingStepInstancePK();
        onboardingStepInstancePK.setStepId(stepId);
        onboardingStepInstancePK.setDriverId(driverId);
        onboardingStepInstance.setOnboardingStepInstancePK(onboardingStepInstancePK);
        return onboardingStepInstance;
    }
}