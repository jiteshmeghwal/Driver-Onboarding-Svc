package com.example.driveronboardingservice.service;

import com.example.driveronboardingservice.constant.MessageConstants;
import com.example.driveronboardingservice.constant.ShipmentStatus;
import com.example.driveronboardingservice.dao.entity.Shipment;
import com.example.driveronboardingservice.exception.ValidationException;
import com.example.driveronboardingservice.model.ShipmentDTO;
import com.example.driveronboardingservice.repository.ShipmentRepository;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.Optional;

@Service
public class ShipmentService {
    private static final Logger logger = LogManager.getLogger(ShipmentService.class);

    @Autowired
    private ShipmentRepository shipmentRepository;

    public void createShipment(ShipmentDTO shipmentDTO) {
        Shipment shipment = new Shipment();
        shipment.setOrderId(shipmentDTO.getOrderId());
        shipment.setStatus(ShipmentStatus.ORDERED.getCode());
        shipment.setOrderDate(Timestamp.from(Instant.now()));
        shipment.setDriverId(shipmentDTO.getDriverId());
        shipment.setStepId(shipmentDTO.getStepId());
        shipmentRepository.save(shipment);
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

    public ShipmentDTO getShipmentDTO(Shipment shipment) {
        return ShipmentDTO.builder()
                .shipmentId(shipment.getId())
                .orderId(shipment.getOrderId())
                .carrier(shipment.getCarrier())
                .status(shipment.getStatus())
                .driverId(shipment.getDriverId())
                .stepId(shipment.getStepId())
                .build();
    }
}
