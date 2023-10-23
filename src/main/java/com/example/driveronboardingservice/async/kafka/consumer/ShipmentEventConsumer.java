package com.example.driveronboardingservice.async.kafka.consumer;

import com.example.driveronboardingservice.constant.ShipmentStatus;
import com.example.driveronboardingservice.model.OnboardingStepDTO;
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

    @Autowired
    private OnboardingStepService stepService;

    @Override
    @KafkaListener(topics = "${shipment.update.event.topic}")
    public void consume(ConsumerRecord<String, String> record, Acknowledgment acknowledgment) {
        logger.debug("Consumed shipment update message: {}", record.value());
        Gson gson = new Gson();
        ShipmentUpdateEvent event = gson.fromJson(record.value(), ShipmentUpdateEvent.class);
        try {
            ShipmentDTO shipmentDTO = shipmentService.updateShipment(ShipmentDTO.builder()
                    .carrier(event.getCarrier())
                    .orderId(event.getOrderId())
                    .status(event.getStatusCd()).build());
            if (ShipmentStatus.DELIVERED.getCode().equals(event.getStatusCd())) {
                //update step to complete status
                stepService.updateOnboardingStepStatus(shipmentDTO.getStepId(), shipmentDTO.getDriverId(),
                        true, null);
            }
            acknowledgment.acknowledge();
        } catch (Exception exception) {
            logger.error("Failed updating shipment status");
        }
    }
}