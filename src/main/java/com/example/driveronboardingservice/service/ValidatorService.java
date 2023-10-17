package com.example.driveronboardingservice.service;

import com.example.driveronboardingservice.constant.MessageConstants;
import com.example.driveronboardingservice.dao.entity.DriverProfile;
import com.example.driveronboardingservice.dao.entity.OnboardingStepInstance;
import com.example.driveronboardingservice.exception.ValidationException;
import com.example.driveronboardingservice.model.DocumentMetadata;
import com.example.driveronboardingservice.model.request.GenericDriverProfileRequest;
import com.example.driveronboardingservice.repository.DriverProfileRepository;
import com.example.driveronboardingservice.repository.OnboardingStepInstanceRepository;
import com.example.driveronboardingservice.util.RequestContextStore;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class ValidatorService {

    @Autowired
    DriverProfileRepository driverProfileRepo;
    @Autowired
    OnboardingStepInstanceRepository stepInstanceRepository;

    public void validateCreateProfileRequest(GenericDriverProfileRequest createRequest) throws ValidationException {
        String driverId = RequestContextStore.getUser().getUsername();
        Optional<DriverProfile> profile = driverProfileRepo.findByDriverId(driverId);
        if(profile.isPresent()) {
            throw new ValidationException(MessageConstants.PROFILE_ALREADY_EXISTS.getCode(),
                    MessageConstants.PROFILE_ALREADY_EXISTS.getDesc());
        }
    }

    public void validateDocumentUploadRequest(DocumentMetadata documentMetadata) throws ValidationException {
        String driverId = RequestContextStore.getUser().getUsername();
        //validate if the step id sent in request really belongs to the user
        Optional<OnboardingStepInstance> stepInstance = stepInstanceRepository.findByStepInstanceIdAndDriverId(
                documentMetadata.getStepInstanceId(),
                driverId);
        if(stepInstance.isEmpty()) {
            throw new ValidationException(MessageConstants.STEP_DOES_NOT_MATCH_WITH_USER.getCode(),
                    MessageConstants.STEP_DOES_NOT_MATCH_WITH_USER.getDesc());
        }
    }
}
