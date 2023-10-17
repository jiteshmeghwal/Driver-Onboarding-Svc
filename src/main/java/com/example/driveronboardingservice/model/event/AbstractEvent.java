package com.example.driveronboardingservice.model.event;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class AbstractEvent {
    protected String eventType;
    protected String userId;
    protected LocalDateTime eventTimestamp;
}