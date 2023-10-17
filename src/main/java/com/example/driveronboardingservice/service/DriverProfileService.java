package com.example.driveronboardingservice.service;

import com.example.driveronboardingservice.constant.MessageConstants;
import com.example.driveronboardingservice.dao.entity.DriverProfile;
import com.example.driveronboardingservice.exception.ResourceNotFoundException;
import com.example.driveronboardingservice.exception.ValidationException;
import com.example.driveronboardingservice.model.DriverDTO;
import com.example.driveronboardingservice.model.request.GenericDriverProfileRequest;
import com.example.driveronboardingservice.repository.DriverProfileRepository;
import com.example.driveronboardingservice.util.EntityMapper;
import com.example.driveronboardingservice.util.RequestContextStore;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class DriverProfileService {
    private static final Logger logger = LogManager.getLogger(DriverProfileService.class);
    private static final EntityMapper entityMapper = new EntityMapper();

    @Autowired
    DriverProfileRepository driverProfileRepo;
    @Autowired
    ValidatorService validatorService;

    public void createProfile(GenericDriverProfileRequest createRequest) throws ValidationException {
        logger.info("Received request to create driver profile");
        validatorService.validateCreateProfileRequest(createRequest);
        DriverProfile profile = entityMapper.mapCreateDriverRequestToDriverProfile(createRequest);
        driverProfileRepo.save(profile);
        logger.info("Driver Profile created");
    }

    public void deleteProfile() throws ResourceNotFoundException {
        logger.info("Received request to delete driver Profile");
        String driverId = SecurityContextHolder.getContext().getAuthentication().getName();
        Optional<DriverProfile> driverProfile = driverProfileRepo.findByDriverId(driverId);

        if(driverProfile.isEmpty()) {
            throw new ResourceNotFoundException(MessageConstants.PROFILE_DOES_NOT_EXISTS.getCode(),
                    MessageConstants.PROFILE_DOES_NOT_EXISTS.getDesc());
        }
        driverProfileRepo.delete(driverProfile.get());
        logger.info("Deleted driver profile successfully");
    }

    public void updateProfile() {
    }

    public DriverDTO getDriverDetails() throws ResourceNotFoundException {

        String driverId = RequestContextStore.getUser().getUsername();
        Optional<DriverProfile> driverProfile = driverProfileRepo.findByDriverId(driverId);

        if(driverProfile.isEmpty()) {
            throw new ResourceNotFoundException(MessageConstants.PROFILE_DOES_NOT_EXISTS.getCode(),
                    MessageConstants.PROFILE_DOES_NOT_EXISTS.getDesc());
        }
        return getDriverDetails(driverProfile.get());
    }

    private DriverDTO getDriverDetails(DriverProfile driver) {
        return DriverDTO.builder()
                .driverId(driver.getDriverId())
                .addrLine1(driver.getAddrLine1())
                .addrLine2(driver.getAddrLine2())
                .city(driver.getCity())
                .zipCode(driver.getZipCode())
                .availableToDrive(driver.isAvailable())
                .build();
    }
}
