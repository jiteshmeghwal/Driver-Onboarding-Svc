package com.example.driveronboardingservice.service;

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
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.time.Clock;
import java.util.*;

@Service
public class OnboardingStepService {
    @Autowired
    private ApplicationEventPublisher applicationEventPublisher;

    @Autowired
    private OnboardingStepRepository onboardingStepRepository;
    
    @Autowired
    private OnboardingStepInstanceRepository onboardingStepInstanceRepository;

    @Value("${onboarding.step.type.sequence}")
    private  List<Short> onboardingStepTypeSequence = new ArrayList<>();

    @Cacheable("onboardingSteps")
    public Map<Short, OnboardingStep> getOnboardingStepsMap() {
        Iterable<OnboardingStep> onboardingSteps = onboardingStepRepository.findAll();

        Map<Short, OnboardingStep> cacheMap = new HashMap<>();
        for(OnboardingStep step : onboardingSteps) {
            cacheMap.put(step.getStepId(), step);
        }
        return cacheMap;
    }

    public List<OnboardingStepDTO> getOnboardingStepsByDriver(String driverId) {
        List<OnboardingStepInstance> onboardingStepInstances = onboardingStepInstanceRepository.findAllByDriverId(driverId);
        Map<Short, OnboardingStep> onboardingStepMap = getOnboardingStepsMap();

        List<OnboardingStepDTO> onboardingSteps = new ArrayList<>();

        for(Short stepTypeCd : onboardingStepTypeSequence) {
            onboardingSteps.addAll(onboardingStepMap.values().stream()
                    .filter(onboardingStep -> onboardingStep.getStepTypeCd().equals(stepTypeCd))
                    .map(onboardingStep -> {
                        OnboardingStepDTO onboardingStepDTO = OnboardingStepDTO.builder()
                                .stepId(onboardingStep.getStepId())
                                .stepTypeCd(onboardingStep.getStepTypeCd())
                                .stepTypeDesc(OnboardingStepType.getByCode(onboardingStep.getStepTypeCd()).name())
                                .stepTitle(onboardingStep.getStepTitle())
                                .stepDesc(onboardingStep.getStepDesc())
                                .driverId(driverId)
                                .build();

                        OnboardingStepInstance matchingStepInstance = onboardingStepInstances.stream()
                                .filter(stepInstance -> stepInstance.getOnboardingStepInstancePK().getStepId()
                                        .equals(onboardingStep.getStepId()))
                                .findFirst()
                                .orElse(null);

                        if (matchingStepInstance != null) {
                            onboardingStepDTO.setComplete(matchingStepInstance.isComplete());
                            onboardingStepDTO.setAdditionalComments(matchingStepInstance.getAdditionalComments());
                        } else {
                            onboardingStepDTO.setComplete(false);
                        }
                        return onboardingStepDTO;
                    }).toList());
        }

        return onboardingSteps;
    }
    
    public OnboardingStepDTO updateCompleteStatus(Short stepId, String driverId, boolean complete)
            throws ValidationException {
        OnboardingStepInstancePK onboardingStepInstancePK = new OnboardingStepInstancePK();
        onboardingStepInstancePK.setStepId(stepId);
        onboardingStepInstancePK.setDriverId(driverId);
        OnboardingStepInstance onboardingStepInstance;
        Optional<OnboardingStepInstance> onboardingStepInstanceOptional =
                onboardingStepInstanceRepository.findById(onboardingStepInstancePK);
        if(onboardingStepInstanceOptional.isPresent()) {
            onboardingStepInstance = onboardingStepInstanceOptional.get();
        } else {
            onboardingStepInstance = new OnboardingStepInstance();
            onboardingStepInstance.setOnboardingStepInstancePK(onboardingStepInstancePK);
        }
        onboardingStepInstance.setComplete(complete);
        onboardingStepInstanceRepository.save(onboardingStepInstance);
        return getOnboardingStep(stepId, driverId);
    }

    public void updateStep(OnboardingStepDTO onboardingStepDTO) throws ValidationException {
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
            publishEvent(getStepCompleteEvent(getOnboardingStep(
                    onboardingStepDTO.getStepId(), onboardingStepDTO.getDriverId()
            )));
        }
    }

    @Async
    public void publishEvent(StepCompleteEvent stepCompleteEvent) {
        applicationEventPublisher.publishEvent(stepCompleteEvent);
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
}
