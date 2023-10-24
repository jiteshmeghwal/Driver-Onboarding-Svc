package com.example.driveronboardingservice.service;

import com.example.driveronboardingservice.constant.ShipmentStatus;
import com.example.driveronboardingservice.entity.Shipment;
import com.example.driveronboardingservice.exception.ValidationException;
import com.example.driveronboardingservice.model.ShipmentDTO;
import com.example.driveronboardingservice.repository.ShipmentRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.sql.Timestamp;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@ExtendWith(MockitoExtension.class)
class ShipmentServiceTest {
    @Mock
    private ShipmentRepository shipmentRepository;
    @Mock
    private OnboardingStepService onboardingStepService;
    @InjectMocks
    private ShipmentService shipmentService;
    private String driverId = "user";
    private Short stepId = (short) 1;

    @Test
    void createShipment() throws ValidationException {
        shipmentService.createShipment(ShipmentDTO.builder()
                .orderId(String.valueOf(UUID.randomUUID()))
                .orderDate(LocalDateTime.now())
                .driverId(driverId)
                .stepId(stepId).build());
        Mockito.verify(shipmentRepository, Mockito.times(1)).save(Mockito.any(Shipment.class));
    }

    @Test
    void updateShipment_ValidationException() {
        Mockito.when(shipmentRepository.findByOrderId(Mockito.anyString())).thenReturn(Optional.empty());
        Assertions.assertThrows(ValidationException.class, ()->
                shipmentService.updateShipment(ShipmentDTO.builder()
                        .orderId(UUID.randomUUID().toString()).build()));
    }

    @Test
    void updateShipment() throws ValidationException {
        Mockito.when(shipmentRepository.findByOrderId(Mockito.anyString())).thenReturn(Optional.of(new Shipment()));
        shipmentService.updateShipment(ShipmentDTO.builder()
                .orderId(UUID.randomUUID().toString())
                .status(ShipmentStatus.DELIVERED.getCode()).build());
    }

    @Test
    void getShipment_ValidationException() {
        Mockito.when(shipmentRepository.findByStepIdAndDriverId(stepId, driverId)).thenReturn(Optional.empty());
        Assertions.assertThrows(ValidationException.class, ()->
                shipmentService.getShipment(stepId, driverId));
    }

    @Test
    void getShipment() throws ValidationException {
        Mockito.when(shipmentRepository.findByStepIdAndDriverId(stepId, driverId)).thenReturn(Optional.of(getMockShipment()));
        shipmentService.getShipment(stepId, driverId);
    }

    @Test
    void validateShipmentAlreadyExist() {
        Mockito.when(shipmentRepository.findByStepIdAndDriverId(stepId, driverId)).thenReturn(Optional.of(getMockShipment()));
        Assertions.assertThrows(ValidationException.class, ()->
                shipmentService.validateShipmentAlreadyExist(stepId, driverId));
    }

    private Shipment getMockShipment() {
        Shipment shipment = new Shipment();
        shipment.setStatus(ShipmentStatus.SHIPPED.getCode());
        shipment.setOrderDate(Timestamp.from(Instant.now()));
        return shipment;
    }
}