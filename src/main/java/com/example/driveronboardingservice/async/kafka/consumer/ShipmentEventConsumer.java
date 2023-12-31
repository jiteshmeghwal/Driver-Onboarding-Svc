package com.example.driveronboardingservice.async.kafka.consumer;

import com.example.driveronboardingservice.exception.ValidationException;
import com.example.driveronboardingservice.model.ShipmentDTO;
import com.example.driveronboardingservice.model.event.kafka.ShipmentUpdateEvent;
import com.example.driveronboardingservice.service.OnboardingStepService;
import com.example.driveronboardingservice.service.ShipmentService;
import com.google.gson.Gson;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Service;

@Service
public class ShipmentEventConsumer implements AbstractKafkaConsumer {
    private static final Logger logger = LogManager.getLogger(ShipmentEventConsumer.class);

    @Autowired
    private ShipmentService shipmentService;

    @Override
    @KafkaListener(topics = "${shipment.update.event.topic}")
    public void consume(ConsumerRecord<String, String> record, Acknowledgment acknowledgment) {
        logger.debug("Consumed shipment update message: {}", record.value());
        Gson gson = new Gson();
        ShipmentUpdateEvent event = gson.fromJson(record.value(), ShipmentUpdateEvent.class);
        try {
            shipmentService.updateShipment(ShipmentDTO.builder()
                            .orderId(event.getOrderId())
                            .carrier(event.getCarrier())
                            .status(event.getStatusCd())
                    .build());
        } catch (ValidationException e) {
            logger.error("Shipment update failed due to, error: {}, cause: {}", e.getDesc(),
                    e.getMessage());
        }
        acknowledgment.acknowledge();
    }
}