package com.example.driveronboardingservice.service;

import com.example.driveronboardingservice.async.publisher.EventPublisher;
import com.example.driveronboardingservice.config.AppConfig;
import com.example.driveronboardingservice.constant.EventType;
import com.example.driveronboardingservice.constant.MessageConstants;
import com.example.driveronboardingservice.constant.OnboardingStepType;
import com.example.driveronboardingservice.entity.OnboardingStep;
import com.example.driveronboardingservice.entity.OnboardingStepInstance;
import com.example.driveronboardingservice.entity.OnboardingStepInstancePK;
import com.example.driveronboardingservice.exception.ValidationException;
import com.example.driveronboardingservice.model.OnboardingStepDTO;
import com.example.driveronboardingservice.model.event.StepCompleteEvent;
import com.example.driveronboardingservice.repository.OnboardingStepInstanceRepository;
import com.example.driveronboardingservice.repository.OnboardingStepRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.time.Clock;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class OnboardingStepService {
    @Autowired
    private AppConfig appConfig;
    @Autowired
    private OnboardingStepRepository onboardingStepRepository;
    @Autowired
    private OnboardingStepInstanceRepository onboardingStepInstanceRepository;
    @Autowired
    private EventPublisher eventPublisher;


    @Cacheable("onboardingSteps")
    public List<OnboardingStep> getOnboardingStepsList() {
        Iterable<OnboardingStep> onboardingSteps = onboardingStepRepository.findAll();

        List<OnboardingStep> onboardingStepList = new ArrayList<>();
        for(OnboardingStep step : onboardingSteps) {
            onboardingStepList.add(step);
        }
        return onboardingStepList;
    }

    public List<OnboardingStepDTO> getOnboardingStepsByDriver(String driverId) {
        List<OnboardingStepInstance> onboardingStepInstances = onboardingStepInstanceRepository.findAllByDriverId(driverId);
        List<OnboardingStep> onboardingStepList = getOnboardingStepsList();

        List<OnboardingStepDTO> onboardingSteps = new ArrayList<>();

        for(Short stepTypeCd : appConfig.getOnboardingStepTypeSequence()) {
            onboardingSteps.addAll(onboardingStepList.stream()
                    .filter(onboardingStep -> onboardingStep.getStepTypeCd().equals(stepTypeCd))
                    .map(onboardingStep -> {
                        OnboardingStepInstance onboardingStepInstance = onboardingStepInstances.stream()
                                .filter(stepInstance -> stepInstance.getOnboardingStepInstancePK().getStepId()
                                        .equals(onboardingStep.getStepId()))
                                .findFirst()
                                .orElse(getNewOnboardingStepInstance(onboardingStep.getStepId(), driverId));

                        return getOnboardingStepDTO(onboardingStep, onboardingStepInstance);
                    }).toList());
        }

        return onboardingSteps;
    }

    private OnboardingStepInstance getNewOnboardingStepInstance(Short stepId, String driverId) {
        OnboardingStepInstancePK onboardingStepInstancePK = new OnboardingStepInstancePK();
        onboardingStepInstancePK.setStepId(stepId);
        onboardingStepInstancePK.setDriverId(driverId);
        OnboardingStepInstance onboardingStepInstance = new OnboardingStepInstance();
        onboardingStepInstance.setOnboardingStepInstancePK(onboardingStepInstancePK);
        onboardingStepInstance.setComplete(false);
        return onboardingStepInstance;
    }

    public void updateStep(OnboardingStepDTO onboardingStepDTO) {
        OnboardingStepInstancePK onboardingStepInstancePK = new OnboardingStepInstancePK();
        onboardingStepInstancePK.setStepId(onboardingStepDTO.getStepId());
        onboardingStepInstancePK.setDriverId(onboardingStepDTO.getDriverId());
        OnboardingStepInstance onboardingStepInstance;
        Optional<OnboardingStepInstance> onboardingStepInstanceOptional =
                onboardingStepInstanceRepository.findById(onboardingStepInstancePK);
        if(onboardingStepInstanceOptional.isPresent()) {
            onboardingStepInstance = onboardingStepInstanceOptional.get();
        } else {
            onboardingStepInstance = new OnboardingStepInstance();
            onboardingStepInstance.setOnboardingStepInstancePK(onboardingStepInstancePK);
        }
        onboardingStepInstance.setComplete(onboardingStepDTO.isComplete());
        onboardingStepInstance.setAdditionalComments(onboardingStepDTO.getAdditionalComments());
        onboardingStepInstanceRepository.save(onboardingStepInstance);
        if(onboardingStepDTO.isComplete()) {
            StepCompleteEvent stepCompleteEvent = getStepCompleteEvent(onboardingStepDTO);
            eventPublisher.publishEvent(stepCompleteEvent);
        }
    }
    
    public OnboardingStepDTO getOnboardingStep(Short stepId, String driverId) throws ValidationException {
        List<OnboardingStepDTO> onboardingStepList = getOnboardingStepsByDriver(driverId);
        Optional<OnboardingStepDTO> matchingStep = onboardingStepList.stream()
                .filter(step -> step.getStepId().equals(stepId))
                .findFirst();

        if(matchingStep.isEmpty()) {
            throw new ValidationException(MessageConstants.INVALID_STEP.getCode(),
                    MessageConstants.INVALID_STEP.getDesc());
        }
        return matchingStep.get();
    }

    public Optional<OnboardingStepDTO> getNextIncompleteStep(String driverId) {
        List<OnboardingStepDTO> onboardingStepList = getOnboardingStepsByDriver(driverId);
        return onboardingStepList.stream()
                .filter(step -> !step.isComplete())
                .findFirst();
    }

    public StepCompleteEvent getStepCompleteEvent(OnboardingStepDTO onboardingStep) {
        StepCompleteEvent stepCompleteEvent = new StepCompleteEvent(this, Clock.systemUTC());
        stepCompleteEvent.setEventType(EventType.STEP_COMPLETE);
        stepCompleteEvent.setUserId(onboardingStep.getDriverId());
        stepCompleteEvent.setOnboardingStep(onboardingStep);
        return  stepCompleteEvent;
    }

    private OnboardingStepDTO getOnboardingStepDTO(OnboardingStep onboardingStep,
                                                   OnboardingStepInstance onboardingStepInstance) {
        return OnboardingStepDTO.builder()
                .stepId(onboardingStep.getStepId())
                .stepTypeCd(onboardingStep.getStepTypeCd())
                .stepTypeDesc(OnboardingStepType.getByCode(onboardingStep.getStepTypeCd()).name())
                .stepTitle(onboardingStep.getStepTitle())
                .stepDesc(onboardingStep.getStepDesc())
                .isComplete(onboardingStepInstance.isComplete())
                .additionalComments(onboardingStepInstance.getAdditionalComments())
                .driverId(onboardingStepInstance.getOnboardingStepInstancePK().getDriverId())
                .build();
    }
}
