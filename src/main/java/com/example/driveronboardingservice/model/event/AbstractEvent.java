package com.example.driveronboardingservice.model.event;

import com.example.driveronboardingservice.constant.EventType;
import lombok.Getter;
import lombok.Setter;
import org.springframework.context.ApplicationEvent;

import java.time.Clock;

@Getter
@Setter
public class AbstractEvent extends ApplicationEvent {
    protected EventType eventType;
    protected String userId;

    public AbstractEvent(Object source, Clock clock) {
        super(source, clock);
    }
}