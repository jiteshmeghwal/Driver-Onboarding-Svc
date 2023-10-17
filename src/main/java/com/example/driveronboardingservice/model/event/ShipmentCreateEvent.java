package com.example.driveronboardingservice.model.event;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ShipmentCreateEvent extends AbstractEvent{
    private String orderId;
    private long stepInstanceId;
}
