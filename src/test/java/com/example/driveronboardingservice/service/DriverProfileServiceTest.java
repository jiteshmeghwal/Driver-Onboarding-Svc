package com.example.driveronboardingservice.service;

import com.example.driveronboardingservice.constant.MessageConstants;
import com.example.driveronboardingservice.constant.VehicleType;
import com.example.driveronboardingservice.entity.DriverProfile;
import com.example.driveronboardingservice.exception.ForbiddenException;
import com.example.driveronboardingservice.exception.ResourceNotFoundException;
import com.example.driveronboardingservice.exception.ValidationException;
import com.example.driveronboardingservice.model.DriverDTO;
import com.example.driveronboardingservice.model.OnboardingStepDTO;
import com.example.driveronboardingservice.model.auth.CustomUser;
import com.example.driveronboardingservice.model.request.GenericDriverProfileRequest;
import com.example.driveronboardingservice.repository.DriverProfileRepository;
import com.example.driveronboardingservice.service.auth.CustomUserDetailsService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

@ExtendWith(MockitoExtension.class)
class DriverProfileServiceTest {
    @Mock
    DriverProfileRepository driverProfileRepo;
    @Mock
    CustomUserDetailsService userDetailsService;
    @Mock
    OnboardingStepService onboardingStepService;
    @InjectMocks
    DriverProfileService driverProfileService;

    private final String driverId = "user";

    @Test
    void createProfile_ProfileAlreadyExist() {
        try {
            Mockito.when(driverProfileRepo.findByDriverId(driverId)).thenReturn(Optional.of(new DriverProfile()));
            driverProfileService.createProfile(new GenericDriverProfileRequest(), driverId);
        }catch (ValidationException exception) {
            Assertions.assertEquals(MessageConstants.PROFILE_ALREADY_EXIST.getCode(),
                    exception.getCode());
        }
    }

    @Test
    void createProfile_InvalidRequest() {
        try {
            Mockito.when(driverProfileRepo.findByDriverId(driverId)).thenReturn(Optional.empty());
            driverProfileService.createProfile(new GenericDriverProfileRequest(), driverId);
        } catch (ValidationException exception) {
            Assertions.assertEquals(MessageConstants.INVALID_REQUEST.getCode(),
                    exception.getCode());
        }
    }

    @Test
    void createProfile() throws ValidationException {
        Mockito.when(driverProfileRepo.findByDriverId(driverId)).thenReturn(Optional.empty());
        driverProfileService.createProfile(getCreateProfileRequest(), driverId);
        Mockito.verify(driverProfileRepo, Mockito.times(1)).save(Mockito.any(DriverProfile.class));
    }

    @Test
    void updateAvailabilityToFalse() throws ForbiddenException {
        driverProfileService.updateAvailability(false, driverId);
        Mockito.verify(driverProfileRepo, Mockito.times(1)).updateAvailability(false, driverId);
    }

    @Test
    void updateAvailabilityToTrue_PendingStepError() {
        Mockito.when(onboardingStepService.getNextIncompleteStep(driverId))
                .thenReturn(Optional.of(OnboardingStepDTO.builder().build()));
        try {
            driverProfileService.updateAvailability(true, driverId);
        } catch (ForbiddenException exception) {
            Assertions.assertEquals(MessageConstants.ONBOARDING_STEPS_PENDING.getCode(),
                    exception.getCode());
        }
    }

    @Test
    void updateAvailabilityToTrue() throws ForbiddenException {
        Mockito.when(onboardingStepService.getNextIncompleteStep(driverId))
                .thenReturn(Optional.empty());
        driverProfileService.updateAvailability(true, driverId);
        Mockito.verify(driverProfileRepo, Mockito.times(1)).updateAvailability(true, driverId);
    }

    @Test
    void getDriverDetails_DriverNotFound() {
        Mockito.when(driverProfileRepo.findByDriverId(driverId)).thenReturn(Optional.empty());
        try {
            driverProfileService.getDriverDetails(driverId);
        } catch (ResourceNotFoundException exception) {
            Assertions.assertEquals(MessageConstants.PROFILE_DOES_NOT_EXIST.getCode(),
                    exception.getCode());
        }
    }

    @Test
    void getDriverDetails() throws ResourceNotFoundException {
        CustomUser customUser = new CustomUser("user", "password"
                , List.of("USER"), "user", "9876443210", "user@xyz.com");
        DriverProfile profile = new DriverProfile();
        profile.setDriverId(driverId);
        Mockito.when(driverProfileRepo.findByDriverId(driverId)).thenReturn(Optional.of(profile));
        Mockito.when(userDetailsService.loadUserByUsername(driverId)).thenReturn(customUser);
        DriverDTO driver = driverProfileService.getDriverDetails(driverId);
        Assertions.assertEquals(driverId, driver.getDriverId());
    }

    private GenericDriverProfileRequest getCreateProfileRequest() {
        GenericDriverProfileRequest request = new GenericDriverProfileRequest();
        request.setAddrLine1("Address line 1");
        request.setCity("City");
        request.setZipCode("zipCode");
        request.setVehicleModel("vehicleModel");
        request.setVehicleTypeCode(VehicleType.THREE_WHEELER.getCode());
        request.setVehicleRegNo("RJ14CE5432");
        return request;
    }
}