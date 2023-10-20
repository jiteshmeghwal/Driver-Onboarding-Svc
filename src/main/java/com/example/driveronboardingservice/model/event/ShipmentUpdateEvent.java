package com.example.driveronboardingservice.model.event;

import lombok.Data;

@Data
public class ShipmentUpdateEvent{
    private String orderId;
    private Short statusCd;
    private String carrier;
}
