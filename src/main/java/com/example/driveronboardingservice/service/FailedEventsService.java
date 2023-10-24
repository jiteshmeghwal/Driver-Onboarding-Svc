package com.example.driveronboardingservice.service;

import com.example.driveronboardingservice.entity.FailedEvents;
import com.example.driveronboardingservice.repository.FailedEventsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class FailedEventsService {
    @Autowired
    FailedEventsRepository failedEventsRepository;

    public void persistFailedEvent(FailedEvents failedEvents) {
        failedEventsRepository.save(failedEvents);
    }
}
