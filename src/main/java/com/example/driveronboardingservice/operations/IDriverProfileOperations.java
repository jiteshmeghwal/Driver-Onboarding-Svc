package com.example.driveronboardingservice.operations;

import com.example.driveronboardingservice.exception.ForbiddenException;
import com.example.driveronboardingservice.exception.ResourceNotFoundException;
import com.example.driveronboardingservice.exception.ValidationException;
import com.example.driveronboardingservice.model.DriverDTO;
import com.example.driveronboardingservice.model.request.GenericDriverProfileRequest;

public interface IDriverProfileOperations {
    void createProfile(GenericDriverProfileRequest createRequest, String driverId) throws ValidationException;

    void updateAvailability(boolean available, String driverId) throws ForbiddenException;

    DriverDTO getDriverDetails(String driverId) throws ResourceNotFoundException;
}
