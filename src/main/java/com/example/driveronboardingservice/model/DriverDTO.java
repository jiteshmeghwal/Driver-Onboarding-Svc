package com.example.driveronboardingservice.model;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class DriverDTO {
    private String driverId;
    private String name;
    private String phone;
    private String email;
    private String addrLine1;
    private String addrLine2;
    private String city;
    private String zipCode;
    private boolean availableToDrive;
}
