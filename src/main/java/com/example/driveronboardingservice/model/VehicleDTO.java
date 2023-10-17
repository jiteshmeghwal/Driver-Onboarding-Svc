package com.example.driveronboardingservice.model;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class VehicleDTO {
    private String regNo;
    private String modelName;
    private String vehicleType;
}
