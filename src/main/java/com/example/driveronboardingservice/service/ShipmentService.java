package com.example.driveronboardingservice.service;

import com.example.driveronboardingservice.client.TrackingDeviceOrderClient;
import com.example.driveronboardingservice.constant.MessageConstants;
import com.example.driveronboardingservice.constant.OnboardingStepType;
import com.example.driveronboardingservice.constant.ShipmentStatus;
import com.example.driveronboardingservice.dao.entity.Shipment;
import com.example.driveronboardingservice.exception.ResourceNotFoundException;
import com.example.driveronboardingservice.exception.ValidationException;
import com.example.driveronboardingservice.model.DriverDTO;
import com.example.driveronboardingservice.model.OnboardingStepDTO;
import com.example.driveronboardingservice.model.ShipmentDTO;
import com.example.driveronboardingservice.model.auth.CustomUser;
import com.example.driveronboardingservice.model.request.*;
import com.example.driveronboardingservice.model.response.CreateOrderResponse;
import com.example.driveronboardingservice.repository.ShipmentRepository;
import com.example.driveronboardingservice.service.auth.CustomUserDetailsService;
import jakarta.transaction.Transactional;
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
    @Autowired
    private CustomUserDetailsService customUserDetailsService;
    @Autowired
    private DriverProfileService driverProfileService;
    @Autowired
    private OnboardingStepService onboardingStepService;
    @Autowired
    private TrackingDeviceOrderClient trackingDeviceOrderClient;

    public void createShipment(CreateShipmentRequest createRequest) throws ResourceNotFoundException,
            ValidationException {
        validateOnboardingStep(createRequest.getStepId(), createRequest.getDriverId());
        validateShipmentNotExist(createRequest.getStepId(), createRequest.getDriverId());
        DriverDTO driver = driverProfileService.getDriverDetails(createRequest.getDriverId());
        CustomUser userDetails = (CustomUser) customUserDetailsService.loadUserByUsername(
                createRequest.getDriverId());

        Contact contact = Contact.builder()
                .email(userDetails.getEmail()).phone(userDetails.getPhone()).build();
        Address address = Address.builder()
                .addrLine1(driver.getAddrLine1()).addrLine2(driver.getAddrLine2())
                .city(driver.getCity()).zipCode(driver.getZipCode()).build();
        ShipTo shipTo = ShipTo.builder()
                .name(userDetails.getFullName())
                .address(address).contact(contact).build();
        CreateOrderResponse response = trackingDeviceOrderClient.createOrder(
                CreateOrderRequest.builder()
                        .shipTo(shipTo).build()
        );
        createShipment(
                ShipmentDTO.builder()
                        .orderId(response.getOrderId())
                        .driverId(driver.getDriverId())
                        .stepId(createRequest.getStepId()).build()
        );
    }

    @Transactional
    public void createShipment(ShipmentDTO shipmentDTO) {
        Shipment shipment = new Shipment();
        shipment.setOrderId(shipmentDTO.getOrderId());
        shipment.setStatus(ShipmentStatus.ORDERED.getCode());
        shipment.setOrderDate(Timestamp.from(Instant.now()));
        shipment.setDriverId(shipmentDTO.getDriverId());
        shipment.setStepId(shipmentDTO.getStepId());
        shipmentRepository.save(shipment);
    }

    private void validateShipmentNotExist(Short stepId, String driverId) throws ValidationException {
        Optional<Shipment> shipment = shipmentRepository.findByStepIdAndDriverId(stepId, driverId);
        if(shipment.isPresent()) {
            throw new ValidationException(MessageConstants.SHIPMENT_ALREADY_EXIST.getCode(),
                    MessageConstants.SHIPMENT_ALREADY_EXIST.getDesc());
        }
    }

    private void validateOnboardingStep(Short stepId, String driverId) throws ValidationException {
        Optional<OnboardingStepDTO> onboardingStepDTO = onboardingStepService.getNextIncompleteStep(driverId);

        if (onboardingStepDTO.isEmpty() ||
                ( !OnboardingStepType.SHIPMENT.getCode().equals(onboardingStepDTO.get().getStepTypeCd()) ||
                        !onboardingStepDTO.get().getStepId().equals(stepId))) {
            throw new ValidationException(MessageConstants.INVALID_STEP.getCode(), MessageConstants.INVALID_STEP.getDesc());
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
                .driverId(shipment.getDriverId())
                .stepId(shipment.getStepId())
                .build();
    }
}
