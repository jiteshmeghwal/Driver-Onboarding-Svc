package com.example.driveronboardingservice.async.kafka.producer;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Service;

@Service
public class ProfileVerificationEventProducer implements AbstractKafkaProducer {
    @Autowired
    private KafkaTemplate<String, String> kafkaTemplate;
    @Value("${driver.verification.event.topic}")
    private String driverVerificationEventTopic;

    @Override
    public void produce(String payload) {
        Message<String> message = MessageBuilder
                .withPayload(payload)
                .setHeader(KafkaHeaders.TOPIC, driverVerificationEventTopic)
                .setHeader("EVENT_TYPE", "PROFILE_VERIFY")
                .build();
        kafkaTemplate.send(message);
    }
}
