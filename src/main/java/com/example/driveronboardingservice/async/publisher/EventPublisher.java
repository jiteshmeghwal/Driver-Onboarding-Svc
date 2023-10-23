package com.example.driveronboardingservice.async.publisher;

import com.example.driveronboardingservice.model.event.AbstractEvent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Async
@Service
public class EventPublisher {
    @Autowired
    private ApplicationEventPublisher applicationEventPublisher;

    public void publishEvent(AbstractEvent stepCompleteEvent) {
        applicationEventPublisher.publishEvent(stepCompleteEvent);
    }
}
