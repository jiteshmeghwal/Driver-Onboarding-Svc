package com.example.driveronboardingservice.service;

import com.example.driveronboardingservice.client.TrackingDeviceOrderClient;
import com.example.driveronboardingservice.constant.MessageConstants;
import com.example.driveronboardingservice.constant.OnboardingStepType;
import com.example.driveronboardingservice.constant.ShipmentStatus;
import com.example.driveronboardingservice.entity.Shipment;
import com.example.driveronboardingservice.exception.ResourceNotFoundException;
import com.example.driveronboardingservice.exception.ValidationException;
import com.example.driveronboardingservice.model.OnboardingStepDTO;
import com.example.driveronboardingservice.model.ShipmentDTO;
import com.example.driveronboardingservice.model.response.CreateOrderResponse;
import com.example.driveronboardingservice.repository.ShipmentRepository;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.Optional;

/**
 * Service to create a new shipment of tracking device
 * @author Jitesh Meghwal
 */
@Service
public class ShipmentService {
    private static final Logger logger = LogManager.getLogger(ShipmentService.class);

    @Autowired
    private ShipmentRepository shipmentRepository;
    @Autowired
    private OnboardingStepService onboardingStepService;
    @Autowired
    private TrackingDeviceOrderClient trackingDeviceOrderClient;

    public void createShipment(Short stepId, String driverId) throws ResourceNotFoundException,
            ValidationException {
        validateOnboardingStep(stepId, driverId);

        Shipment shipment;

        try {
            CreateOrderResponse response = trackingDeviceOrderClient.createOrder(driverId);
            shipment = createShipment(
                    ShipmentDTO.builder()
                            .orderId(response.getOrderId())
                            .driverId(driverId)
                            .stepId(stepId).build()
            );
        } catch (HttpServerErrorException | HttpClientErrorException exception) {
            shipment = createShipment(ShipmentDTO.builder()
                    .status(ShipmentStatus.FAILED.getCode())
                    .driverId(driverId)
                    .stepId(stepId)
                    .build());
        }

        shipmentRepository.save(shipment);
    }

    public Shipment createShipment(ShipmentDTO shipmentDTO) {
        Shipment shipment = new Shipment();
        shipment.setOrderId(shipmentDTO.getOrderId());
        shipment.setStatus(shipmentDTO.getStatus());
        shipment.setOrderDate(Timestamp.from(Instant.now()));
        shipment.setDriverId(shipmentDTO.getDriverId());
        shipment.setStepId(shipmentDTO.getStepId());
        return shipment;
    }

    private void validateOnboardingStep(Short stepId, String driverId) throws ValidationException {
        Optional<OnboardingStepDTO> onboardingStepDTO = onboardingStepService.getNextIncompleteStep(driverId);

        if (onboardingStepDTO.isEmpty() ||
                ( !OnboardingStepType.SHIPMENT.getCode().equals(onboardingStepDTO.get().getStepTypeCd()) ||
                        !onboardingStepDTO.get().getStepId().equals(stepId))) {
            throw new ValidationException(MessageConstants.INVALID_STEP.getCode(),
                    MessageConstants.INVALID_STEP.getDesc());
        }
    }

    public ShipmentDTO updateShipment(ShipmentDTO shipmentDTO) throws ValidationException {
        Optional<Shipment> shipmentOptional = shipmentRepository.findByOrderId(shipmentDTO.getOrderId());
        if(shipmentOptional.isEmpty()) {
            logger.error("Shipment not found for order ID {}", shipmentDTO.getOrderId());
            throw new ValidationException(MessageConstants.SHIPMENT_NOT_FOUND.getCode(),
                    MessageConstants.SHIPMENT_NOT_FOUND.getDesc());
        }
        Shipment shipment = shipmentOptional.get();
        shipment.setStatus(shipmentDTO.getStatus());
        if(shipmentDTO.getCarrier() != null) {
            shipment.setCarrier(shipmentDTO.getCarrier());
        }
        shipment.setLastUpdateTime(Timestamp.from(Instant.now()));
        shipmentRepository.save(shipment);
        return getShipmentDTO(shipment);
    }

    public ShipmentDTO getShipmentDTO(Short stepId, String driverId) throws ValidationException {
        Optional<Shipment> shipment = shipmentRepository.findByStepIdAndDriverId(stepId, driverId);
        if(shipment.isEmpty()) {
            throw new ValidationException(MessageConstants.SHIPMENT_NOT_FOUND.getCode(),
                    MessageConstants.SHIPMENT_NOT_FOUND.getDesc());
        }
        return getShipmentDTO(shipment.get());
    }

    public ShipmentDTO getShipmentDTO(Shipment shipment) {
        return ShipmentDTO.builder()
                .shipmentId(shipment.getId())
                .orderId(shipment.getOrderId())
                .carrier(shipment.getCarrier())
                .status(shipment.getStatus())
                .statusDesc(ShipmentStatus.getByCode(shipment.getStatus()).name())
                .driverId(shipment.getDriverId())
                .stepId(shipment.getStepId())
                .build();
    }
}
