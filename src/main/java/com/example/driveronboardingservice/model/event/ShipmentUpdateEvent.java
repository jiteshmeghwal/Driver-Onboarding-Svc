package com.example.driveronboardingservice.model.event;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ShipmentUpdateEvent extends AbstractEvent{
    private String orderId;
    private short statusCd;
}
