package com.example.driveronboardingservice.service;

import com.example.driveronboardingservice.constant.MessageConstants;
import com.example.driveronboardingservice.constant.ShipmentStatus;
import com.example.driveronboardingservice.entity.Shipment;
import com.example.driveronboardingservice.exception.ValidationException;
import com.example.driveronboardingservice.model.ShipmentDTO;
import com.example.driveronboardingservice.operations.IShipmentOperations;
import com.example.driveronboardingservice.repository.ShipmentRepository;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.Objects;
import java.util.Optional;

/**
 * Service to create a new shipment of tracking device
 * @author Jitesh Meghwal
 */
@Service
public class ShipmentService implements IShipmentOperations {
    private static final Logger logger = LogManager.getLogger(ShipmentService.class);

    @Autowired
    private ShipmentRepository shipmentRepository;
    @Autowired
    private OnboardingStepService onboardingStepService;

    @Override
    public void createShipment(ShipmentDTO shipmentDTO) throws ValidationException {
        validateShipmentAlreadyExist(shipmentDTO.getStepId(), shipmentDTO.getDriverId());
        Shipment shipment = new Shipment();
        shipment.setOrderId(shipmentDTO.getOrderId());
        shipment.setOrderDate(Timestamp.valueOf(shipmentDTO.getOrderDate()));
        shipment.setStatus(ShipmentStatus.ORDERED.getCode());
        shipment.setDriverId(shipmentDTO.getDriverId());
        shipment.setStepId(shipmentDTO.getStepId());
        shipmentRepository.save(shipment);
        logger.info("Created shipment for user {} with shipmentID {}",shipmentDTO.getDriverId(),
                shipment.getId());
    }

    private void validateShipmentAlreadyExist(Short stepId, String driverId) throws ValidationException {
        Optional<Shipment> shipment = shipmentRepository.findByStepIdAndDriverId(stepId, driverId);
        if(shipment.isPresent()) {
            throw new ValidationException(MessageConstants.SHIPMENT_ALREADY_EXIST.getCode(),
                    MessageConstants.SHIPMENT_ALREADY_EXIST.getDesc());
        }
    }

    @Override
    public void updateShipment(ShipmentDTO shipmentDTO) throws ValidationException {
        Optional<Shipment> shipmentOptional = shipmentRepository.findByOrderId(shipmentDTO.getOrderId());
        if(shipmentOptional.isEmpty()) {
            logger.error("Shipment not found for orderId {}",
                    shipmentDTO.getOrderId());
            throw new ValidationException(MessageConstants.SHIPMENT_NOT_FOUND.getCode(),
                    MessageConstants.SHIPMENT_NOT_FOUND.getDesc());
        }
        Shipment shipment = shipmentOptional.get();
        if(shipmentDTO.getCarrier() != null) {
            shipment.setCarrier(shipmentDTO.getCarrier());
        }
        shipment.setStatus(shipmentDTO.getStatus());
        shipment.setLastUpdateTime(Timestamp.from(Instant.now()));
        shipmentRepository.save(shipment);
        logger.info("Updated shipment {} for user {} and step {}", shipment.getId(),
                shipment.getDriverId(), shipment.getStepId());
        if(Objects.equals(shipment.getStatus(), ShipmentStatus.DELIVERED.getCode())) {
            onboardingStepService.updateOnboardingStepStatus(shipment.getStepId(),
                    shipment.getDriverId(), true, null);
        }
    }

    @Override
    public ShipmentDTO getShipment(Short stepId, String driverId) throws ValidationException {
        Optional<Shipment> shipment = shipmentRepository.findByStepIdAndDriverId(stepId, driverId);
        if(shipment.isEmpty()) {
            throw new ValidationException(MessageConstants.SHIPMENT_NOT_FOUND.getCode(),
                    MessageConstants.SHIPMENT_NOT_FOUND.getDesc());
        }
        return getShipmentDTO(shipment.get());
    }

    private ShipmentDTO getShipmentDTO(Shipment shipment) {
        return ShipmentDTO.builder()
                .shipmentId(shipment.getId())
                .orderId(shipment.getOrderId())
                .orderDate(shipment.getOrderDate().toLocalDateTime())
                .carrier(shipment.getCarrier())
                .status(shipment.getStatus())
                .statusDesc(ShipmentStatus.getByCode(shipment.getStatus()).name())
                .driverId(shipment.getDriverId())
                .stepId(shipment.getStepId())
                .build();
    }
}
