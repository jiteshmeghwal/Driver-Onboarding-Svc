package com.example.driveronboardingservice.model.request;

import lombok.Data;

@Data
public class CreateShipmentRequest {
    private Short stepId;
    private String driverId;

    public CreateShipmentRequest(){

    }

    public CreateShipmentRequest(Short stepId, String driverId) {
        this.stepId = stepId;
        this.driverId = driverId;
    }
}
