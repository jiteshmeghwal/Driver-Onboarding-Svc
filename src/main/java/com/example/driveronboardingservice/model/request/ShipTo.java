package com.example.driveronboardingservice.model.request;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ShipTo {
    private String name;
    private Contact contact;
    private Address address;
}
