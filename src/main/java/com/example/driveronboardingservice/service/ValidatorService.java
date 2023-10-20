package com.example.driveronboardingservice.service;

import com.example.driveronboardingservice.constant.MessageConstants;
import com.example.driveronboardingservice.exception.ValidationException;
import com.example.driveronboardingservice.model.request.GenericDriverProfileRequest;

import java.util.Objects;

public class ValidatorService {

    public void validateCreateProfileRequest(GenericDriverProfileRequest createRequest) throws ValidationException {
        if(Objects.isNull(createRequest.getAddrLine1()) ||
        Objects.isNull(createRequest.getCity()) ||
        Objects.isNull(createRequest.getZipCode()) ||
        Objects.isNull(createRequest.getVehicleModel()) ||
        Objects.isNull(createRequest.getVehicleRegNo()) ||
        Objects.isNull(createRequest.getVehicleTypeCode())) {
            throw new ValidationException(MessageConstants.INVALID_REQUEST.getCode(),
                    MessageConstants.INVALID_REQUEST.getDesc());
        }
    }
}
