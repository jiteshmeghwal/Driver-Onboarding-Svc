package com.example.driveronboardingservice.async;

import com.example.driveronboardingservice.client.TrackingDeviceOrderClient;
import com.example.driveronboardingservice.constant.EventType;
import com.example.driveronboardingservice.entity.FailedEvents;
import com.example.driveronboardingservice.exception.ResourceNotFoundException;
import com.example.driveronboardingservice.exception.ValidationException;
import com.example.driveronboardingservice.model.ShipmentDTO;
import com.example.driveronboardingservice.model.event.CreateShipmentEvent;
import com.example.driveronboardingservice.model.response.CreateOrderResponse;
import com.example.driveronboardingservice.service.FailedEventsService;
import com.example.driveronboardingservice.service.ShipmentService;
import org.checkerframework.checker.units.qual.C;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.client.HttpServerErrorException;

import java.time.Clock;

@ExtendWith(MockitoExtension.class)
class CreateShipmentEventListenerTest {
    @Mock
    private TrackingDeviceOrderClient trackingDeviceOrderClient;
    @Mock
    private ShipmentService shipmentService;
    @Mock
    private FailedEventsService failedEventsService;

    @InjectMocks
    private CreateShipmentEventListener createShipmentEventListener;

    private final Short stepId = (short)1;
    private final String driverId = "user";

    @Test
    void onCreateShipmentEvent_ValidationException() throws ValidationException, ResourceNotFoundException {
        Mockito.doThrow(ValidationException.class)
                .when(shipmentService).validateShipmentAlreadyExist(stepId, driverId);
        createShipmentEventListener.onCreateShipmentEvent(getCreateShipmentEvent());
        Mockito.verify(trackingDeviceOrderClient, Mockito.times(0)).createOrder(driverId);
        Mockito.verify(shipmentService, Mockito.times(0)).createShipment(Mockito.any(ShipmentDTO.class));
        Mockito.verify(failedEventsService, Mockito.times(0)).persistFailedEvent(Mockito.any(FailedEvents.class));
    }

    @Test
    void onCreateShipmentEvent_HttpServerException() throws ValidationException, ResourceNotFoundException {
        Mockito.doThrow(HttpServerErrorException.class)
                .when(trackingDeviceOrderClient).createOrder(driverId);
        createShipmentEventListener.onCreateShipmentEvent(getCreateShipmentEvent());
        Mockito.verify(trackingDeviceOrderClient, Mockito.times(1)).createOrder(driverId);
        Mockito.verify(shipmentService, Mockito.times(0)).createShipment(Mockito.any(ShipmentDTO.class));
        Mockito.verify(failedEventsService, Mockito.times(1)).persistFailedEvent(Mockito.any(FailedEvents.class));
    }

    @Test
    void onCreateShipmentEvent_UserNotFoundException() throws ValidationException, ResourceNotFoundException {
        Mockito.doThrow(ResourceNotFoundException.class)
                .when(trackingDeviceOrderClient).createOrder(driverId);
        createShipmentEventListener.onCreateShipmentEvent(getCreateShipmentEvent());
        Mockito.verify(trackingDeviceOrderClient, Mockito.times(1)).createOrder(driverId);
        Mockito.verify(shipmentService, Mockito.times(0)).createShipment(Mockito.any(ShipmentDTO.class));
        Mockito.verify(failedEventsService, Mockito.times(0)).persistFailedEvent(Mockito.any(FailedEvents.class));
    }

    @Test
    void onCreateShipmentEvent() throws ValidationException, ResourceNotFoundException {
        Mockito.when(trackingDeviceOrderClient.createOrder(driverId)).thenReturn(new CreateOrderResponse());
        createShipmentEventListener.onCreateShipmentEvent(getCreateShipmentEvent());
        Mockito.verify(trackingDeviceOrderClient, Mockito.times(1)).createOrder(driverId);
        Mockito.verify(shipmentService, Mockito.times(1)).createShipment(Mockito.any(ShipmentDTO.class));
        Mockito.verify(failedEventsService, Mockito.times(0)).persistFailedEvent(Mockito.any(FailedEvents.class));
    }

    private CreateShipmentEvent getCreateShipmentEvent() {
        CreateShipmentEvent createShipmentEvent = new CreateShipmentEvent(this, Clock.systemUTC());
        createShipmentEvent.setEventType(EventType.CREATE_SHIPMENT);
        createShipmentEvent.setStepId(stepId);
        createShipmentEvent.setUserId(driverId);
        return createShipmentEvent;
    }
}