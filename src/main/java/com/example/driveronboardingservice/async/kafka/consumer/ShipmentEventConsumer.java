package com.example.driveronboardingservice.async.kafka.consumer;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
public class ShipmentEventConsumer implements AbstractKafkaConsumer {
    private static final Logger logger = LogManager.getLogger(ShipmentEventConsumer.class);

    @Override
    @KafkaListener(topics = "${shipment.update.event.topic}")
    public void consume(String message) {
        logger.info("Consumed shipment update message: {}", message);
    }
}