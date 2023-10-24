package com.example.driveronboardingservice.model.event;

import lombok.Getter;
import lombok.Setter;

import java.time.Clock;

@Getter
@Setter
public class CreateShipmentEvent extends AbstractEvent{
    private Short stepId;
    public CreateShipmentEvent(Object source, Clock clock) {
        super(source, clock);
    }
}
