package com.example.driveronboardingservice.operations;

import com.example.driveronboardingservice.exception.ValidationException;

public interface IOrderServiceOperations {
    void initiateOrder(String driverId, Short stepId) throws ValidationException;
}
