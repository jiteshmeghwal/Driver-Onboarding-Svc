package com.example.driveronboardingservice.model;

import lombok.Data;

@Data
public class VehicleTypeDTO {
    private short code;
    private String type;

    public VehicleTypeDTO(short code, String type) {
        this.code = code;
        this.type = type;
    }
}

