package com.example.driveronboardingservice.model.request;

import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class GenericDriverProfileRequest {
    private String addrLine1;
    private String addrLine2;
    private String city;
    private String zipCode;
    private String vehicleRegNo;
    private String vehicleModel;
    private Short vehicleTypeCode;
    private Boolean online;
}
