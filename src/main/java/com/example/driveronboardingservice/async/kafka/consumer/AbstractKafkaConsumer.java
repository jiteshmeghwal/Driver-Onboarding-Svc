package com.example.driveronboardingservice.async.kafka.consumer;

import org.springframework.stereotype.Service;

@Service
public interface AbstractKafkaConsumer {
    void consume(String message);
}
