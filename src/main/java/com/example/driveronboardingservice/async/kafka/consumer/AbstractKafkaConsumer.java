package com.example.driveronboardingservice.async.kafka.consumer;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.support.Acknowledgment;

public interface AbstractKafkaConsumer {
    void consume(ConsumerRecord<String, String> record, Acknowledgment acknowledgment);
}
