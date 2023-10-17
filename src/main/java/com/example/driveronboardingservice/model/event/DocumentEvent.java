package com.example.driveronboardingservice.model.event;

import com.example.driveronboardingservice.constant.EventType;
import com.example.driveronboardingservice.model.DocumentMetadata;
import lombok.Getter;
import lombok.Setter;
import org.springframework.context.ApplicationEvent;

import java.time.Clock;

@Getter
@Setter
public class DocumentEvent extends ApplicationEvent {
    private EventType eventType;
    private String userId;
    private DocumentMetadata documentMetadata;

    public DocumentEvent(Object source, Clock clock) {
        super(source, clock);
    }
}