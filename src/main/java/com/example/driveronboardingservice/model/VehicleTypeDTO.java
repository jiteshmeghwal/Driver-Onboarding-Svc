package com.example.driveronboardingservice.model;

import lombok.Data;

@Data
public class VehicleTypeDTO {
    private Short code;
    private String type;

    public VehicleTypeDTO(Short code, String type) {
        this.code = code;
        this.type = type;
    }
}

