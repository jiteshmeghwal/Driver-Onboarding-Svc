package com.example.driveronboardingservice.service;

import com.example.driveronboardingservice.constant.MessageConstants;
import com.example.driveronboardingservice.entity.DriverProfile;
import com.example.driveronboardingservice.exception.ForbiddenException;
import com.example.driveronboardingservice.exception.ResourceNotFoundException;
import com.example.driveronboardingservice.exception.ValidationException;
import com.example.driveronboardingservice.model.DriverDTO;
import com.example.driveronboardingservice.model.OnboardingStepDTO;
import com.example.driveronboardingservice.model.auth.CustomUser;
import com.example.driveronboardingservice.model.request.GenericDriverProfileRequest;
import com.example.driveronboardingservice.operations.IDriverProfileOperations;
import com.example.driveronboardingservice.repository.DriverProfileRepository;
import com.example.driveronboardingservice.service.auth.CustomUserDetailsService;
import com.example.driveronboardingservice.util.EntityMapper;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class DriverProfileService implements IDriverProfileOperations {
    private static final Logger logger = LogManager.getLogger(DriverProfileService.class);
    private static final EntityMapper entityMapper = new EntityMapper();
    private static final ValidatorService validatorService = new ValidatorService();

    @Autowired
    DriverProfileRepository driverProfileRepo;
    @Autowired
    CustomUserDetailsService userDetailsService;
    @Autowired
    OnboardingStepService onboardingStepService;

    @Override
    public void createProfile(GenericDriverProfileRequest createRequest, String driverId)
            throws ValidationException {
        validateProfileNotExist(driverId);
        validatorService.validateCreateProfileRequest(createRequest);
        DriverProfile profile = entityMapper.mapCreateDriverRequestToDriverProfile(createRequest, driverId);
        driverProfileRepo.save(profile);
        logger.info("Driver Profile created for driver {}", driverId);
    }

    private void validateProfileNotExist(String driverId) throws ValidationException {
        Optional<DriverProfile> profile = driverProfileRepo.findByDriverId(driverId);
        if(profile.isPresent()) {
            throw new ValidationException(MessageConstants.PROFILE_ALREADY_EXIST.getCode(),
                    MessageConstants.PROFILE_ALREADY_EXIST.getDesc());
        }
    }

    @Override
    public void updateAvailability(boolean available, String driverId) throws ForbiddenException {
        if(available) {
            Optional<OnboardingStepDTO> nextRequiredStep = onboardingStepService.getNextIncompleteOnboardingStep(
                    driverId
            );
            if(nextRequiredStep.isEmpty()) {
                driverProfileRepo.updateAvailability(true, driverId);
            } else {
                throw new ForbiddenException(
                        MessageConstants.ONBOARDING_STEPS_PENDING.getCode(),
                        MessageConstants.ONBOARDING_STEPS_PENDING.getDesc()
                );
            }
        } else {
            driverProfileRepo.updateAvailability(false, driverId);
        }
    }

    @Override
    public DriverDTO getDriverDetails(String driverId) throws ResourceNotFoundException {

        Optional<DriverProfile> driverProfile = driverProfileRepo.findByDriverId(driverId);

        if(driverProfile.isEmpty()) {
            throw new ResourceNotFoundException(MessageConstants.PROFILE_DOES_NOT_EXIST.getCode(),
                    MessageConstants.PROFILE_DOES_NOT_EXIST.getDesc());
        }
        return getDriverDetails(driverProfile.get());
    }

    private DriverDTO getDriverDetails(DriverProfile driver) {
        CustomUser user = (CustomUser) userDetailsService.loadUserByUsername(driver.getDriverId());
        return DriverDTO.builder()
                .driverId(driver.getDriverId())
                .name(user.getFullName())
                .phone(user.getPhone())
                .email(user.getEmail())
                .addrLine1(driver.getAddrLine1())
                .addrLine2(driver.getAddrLine2())
                .city(driver.getCity())
                .zipCode(driver.getZipCode())
                .availableToDrive(driver.isAvailable())
                .build();
    }
}
