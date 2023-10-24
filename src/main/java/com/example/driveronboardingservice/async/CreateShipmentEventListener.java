package com.example.driveronboardingservice.async;

import com.example.driveronboardingservice.client.TrackingDeviceOrderClient;
import com.example.driveronboardingservice.entity.FailedEvents;
import com.example.driveronboardingservice.exception.ResourceNotFoundException;
import com.example.driveronboardingservice.exception.ValidationException;
import com.example.driveronboardingservice.model.ShipmentDTO;
import com.example.driveronboardingservice.model.event.CreateShipmentEvent;
import com.example.driveronboardingservice.model.response.CreateOrderResponse;
import com.example.driveronboardingservice.service.FailedEventsService;
import com.example.driveronboardingservice.service.ShipmentService;
import com.google.gson.Gson;
import jakarta.transaction.Transactional;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

@Component
public class CreateShipmentEventListener {
    private static final Logger logger = LogManager.getLogger(CreateShipmentEventListener.class);
    @Autowired
    private TrackingDeviceOrderClient trackingDeviceOrderClient;
    @Autowired
    private ShipmentService shipmentService;
    @Autowired
    private FailedEventsService failedEventsService;

    @Async
    @EventListener
    @Transactional
    public void onCreateShipmentEvent(CreateShipmentEvent createShipmentEvent)
            throws ResourceNotFoundException, ValidationException {
        try {
            CreateOrderResponse response = trackingDeviceOrderClient.createOrder(createShipmentEvent.getUserId());
            shipmentService.createShipment(ShipmentDTO.builder()
                            .orderId(response.getOrderId())
                            .orderDate(LocalDateTime.now())
                    .driverId(createShipmentEvent.getUserId())
                    .stepId(createShipmentEvent.getStepId()).build());

            logger.info("Created order {} for user {} and step {}", response.getOrderId(),
                    createShipmentEvent.getUserId(), createShipmentEvent.getStepId());
        } catch (HttpServerErrorException | HttpClientErrorException exception) {
            //persist logic failed event
            failedEventsService.persistFailedEvent(createFailedEvent(
                    createShipmentEvent.getEventType().name(),
                    new Gson().toJson(createShipmentEvent),
                    exception.getMessage()));
        }
    }

    private FailedEvents createFailedEvent(String eventType, String eventPayload, String errorMessage) {
        FailedEvents failedEvent = new FailedEvents();
        failedEvent.setEventType(eventType);
        failedEvent.setEventPayload(eventPayload);
        failedEvent.setErrorMessage(errorMessage);
        failedEvent.setRetryCount(0);
        failedEvent.setCreatedAt(Timestamp.valueOf(LocalDateTime.now()));
        failedEvent.setNextRetryTime(Timestamp.valueOf(LocalDateTime.now().plus(30,
                ChronoUnit.MINUTES)));
        return failedEvent;
    }
}
