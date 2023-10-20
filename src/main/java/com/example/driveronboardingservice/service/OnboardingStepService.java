package com.example.driveronboardingservice.service;

import com.example.driveronboardingservice.constant.MessageConstants;
import com.example.driveronboardingservice.dao.CustomJDBCQueryExecutor;
import com.example.driveronboardingservice.dao.entity.OnboardingStep;
import com.example.driveronboardingservice.dao.entity.OnboardingStepInstance;
import com.example.driveronboardingservice.dao.entity.OnboardingStepInstancePK;
import com.example.driveronboardingservice.exception.ValidationException;
import com.example.driveronboardingservice.model.OnboardingStepDTO;
import com.example.driveronboardingservice.repository.OnboardingStepInstanceRepository;
import com.example.driveronboardingservice.repository.OnboardingStepRepository;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class OnboardingStepService {
    @Autowired
    private CustomJDBCQueryExecutor jdbcQueryExecutor;

    @Autowired
    private OnboardingStepRepository onboardingStepRepository;
    
    @Autowired
    private OnboardingStepInstanceRepository onboardingStepInstanceRepository;

    @PostConstruct
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
        return jdbcQueryExecutor.getOnboardingStepsByDriver(driverId,
                getOnboardingStepsMap());
    }
    
    public OnboardingStepDTO updateCompleteStatus(Short stepId, String driverId, boolean complete)
            throws ValidationException {
        OnboardingStepInstancePK onboardingStepInstancePK = new OnboardingStepInstancePK();
        onboardingStepInstancePK.setStepId(stepId);
        onboardingStepInstancePK.setDriverId(driverId);
        OnboardingStepInstance onboardingStepInstance = new OnboardingStepInstance();
        onboardingStepInstance.setOnboardingStepInstancePK(onboardingStepInstancePK);
        onboardingStepInstance.setComplete(complete);
        onboardingStepInstanceRepository.save(onboardingStepInstance);
        return getOnboardingStep(stepId, driverId);
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
}
