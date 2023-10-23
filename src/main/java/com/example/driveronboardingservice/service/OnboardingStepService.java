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
import com.example.driveronboardingservice.operations.IOnboardingStepOperations;
import com.example.driveronboardingservice.repository.OnboardingStepInstanceRepository;
import com.example.driveronboardingservice.repository.OnboardingStepRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.time.Clock;
import java.util.*;

@Service
public class OnboardingStepService implements IOnboardingStepOperations {
    @Autowired
    private AppConfig appConfig;
    @Autowired
    private OnboardingStepRepository onboardingStepRepository;
    @Autowired
    private OnboardingStepInstanceRepository onboardingStepInstanceRepository;
    @Autowired
    private EventPublisher eventPublisher;


    @Cacheable("onboardingSteps")
    private Map<Short, OnboardingStep> getOnboardingStepsMap() {
        Iterable<OnboardingStep> onboardingSteps = onboardingStepRepository.findAll();

        Map<Short, OnboardingStep> onboardingStepsMap = new HashMap();
        for(OnboardingStep step : onboardingSteps) {
            onboardingStepsMap.put(step.getStepId(), step);
        }
        return onboardingStepsMap;
    }

    @Override
    public List<OnboardingStepDTO> getOnboardingSteps(String driverId) {
        List<OnboardingStepInstance> onboardingStepInstances = onboardingStepInstanceRepository.findAllByDriverId(driverId);
        Map<Short, OnboardingStep> onboardingStepsMap = getOnboardingStepsMap();

        List<OnboardingStepDTO> onboardingSteps = new ArrayList<>();

        for(Short stepTypeCd : appConfig.getOnboardingStepTypeSequence()) {
            onboardingSteps.addAll(onboardingStepsMap.values().stream()
                    .filter(onboardingStep -> onboardingStep.getStepTypeCd().equals(stepTypeCd))
                    .map(onboardingStep -> {
                        OnboardingStepInstance onboardingStepInstance = onboardingStepInstances.stream()
                                .filter(stepInstance -> stepInstance.getOnboardingStepInstancePK().getStepId()
                                        .equals(onboardingStep.getStepId()))
                                .findFirst()
                                .orElse(getOnboardingStepInstance(onboardingStep.getStepId(), driverId));

                        return getOnboardingStepDTO(onboardingStep, onboardingStepInstance);
                    }).toList());
        }

        return onboardingSteps;
    }

    private OnboardingStepInstance getOnboardingStepInstance(Short stepId, String driverId) {
        OnboardingStepInstancePK onboardingStepInstancePK = new OnboardingStepInstancePK();
        onboardingStepInstancePK.setStepId(stepId);
        onboardingStepInstancePK.setDriverId(driverId);
        OnboardingStepInstance onboardingStepInstance = new OnboardingStepInstance();
        onboardingStepInstance.setOnboardingStepInstancePK(onboardingStepInstancePK);
        onboardingStepInstance.setComplete(false);
        return onboardingStepInstance;
    }

    public void updateOnboardingStepStatus(Short stepId, String driverId, boolean complete,
                                           String additionalComments) throws ValidationException {
        OnboardingStep onboardingStep = getOnboardingStepsMap().get(stepId);
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
        if(onboardingStepInstance.isComplete() == complete) {
            throw new ValidationException(MessageConstants.STEP_ALREADY_REQUESTED_STATUS.getCode(),
                    MessageConstants.STEP_ALREADY_REQUESTED_STATUS.getDesc());
        }
        onboardingStepInstance.setComplete(complete);
        onboardingStepInstance.setAdditionalComments(additionalComments);
        onboardingStepInstanceRepository.save(onboardingStepInstance);
        if(complete) {
            //publish step complete event
            StepCompleteEvent stepCompleteEvent =
                    getStepCompleteEvent(getOnboardingStepDTO(onboardingStep, onboardingStepInstance));
            eventPublisher.publishEvent(stepCompleteEvent);
        }
    }

    @Override
    public void updateOnboardingStep(OnboardingStepDTO onboardingStepDTO) throws ValidationException {
        OnboardingStep onboardingStep = getOnboardingStepsMap().get(onboardingStepDTO.getStepId());
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
        if(onboardingStepInstance.isComplete() == onboardingStepDTO.isComplete()) {
            throw new ValidationException(MessageConstants.STEP_ALREADY_REQUESTED_STATUS.getCode(),
                    MessageConstants.STEP_ALREADY_REQUESTED_STATUS.getDesc());
        }
        onboardingStepInstance.setComplete(onboardingStepDTO.isComplete());
        onboardingStepInstance.setAdditionalComments(onboardingStepDTO.getAdditionalComments());
        onboardingStepInstance = onboardingStepInstanceRepository.save(onboardingStepInstance);
        if(onboardingStepDTO.isComplete()) {
            StepCompleteEvent stepCompleteEvent =
                    getStepCompleteEvent(getOnboardingStepDTO(onboardingStep, onboardingStepInstance));
            eventPublisher.publishEvent(stepCompleteEvent);
        }
    }
    
    @Override
    public OnboardingStepDTO getOnboardingStep(Short stepId, String driverId) throws ValidationException {
        List<OnboardingStepDTO> onboardingStepList = getOnboardingSteps(driverId);
        Optional<OnboardingStepDTO> matchingStep = onboardingStepList.stream()
                .filter(step -> step.getStepId().equals(stepId))
                .findFirst();

        if(matchingStep.isEmpty()) {
            throw new ValidationException(MessageConstants.INVALID_STEP.getCode(),
                    MessageConstants.INVALID_STEP.getDesc());
        }
        return matchingStep.get();
    }

    @Override
    public Optional<OnboardingStepDTO> getNextIncompleteOnboardingStep(String driverId) {
        List<OnboardingStepDTO> onboardingStepList = getOnboardingSteps(driverId);
        return onboardingStepList.stream()
                .filter(step -> !step.isComplete())
                .findFirst();
    }

    public void updateStepStatusByStepType(String driverId, OnboardingStepType stepType,
                                           boolean complete) throws ValidationException {
        OnboardingStepDTO onboardingStepDTO = getOnboardingSteps(driverId)
                .stream()
                .filter(onboardingStep -> stepType.getCode().equals(onboardingStep.getStepTypeCd())
                        && onboardingStep.isComplete() != complete)
                .findFirst()
                .orElse(null);
        if(onboardingStepDTO != null) {
            onboardingStepDTO.setComplete(complete);
            updateOnboardingStep(onboardingStepDTO);
        }
    }

    private StepCompleteEvent getStepCompleteEvent(OnboardingStepDTO onboardingStep) {
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
