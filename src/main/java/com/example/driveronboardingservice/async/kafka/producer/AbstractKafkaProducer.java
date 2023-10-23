package com.example.driveronboardingservice.async.kafka.producer;

public interface AbstractKafkaProducer {
    void produce(String payload);
}
