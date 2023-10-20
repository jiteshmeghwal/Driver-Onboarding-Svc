package com.example.driveronboardingservice.model.request;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class Address {
    private String addrLine1;
    private String addrLine2;
    private String city;
    private String zipCode;
}
