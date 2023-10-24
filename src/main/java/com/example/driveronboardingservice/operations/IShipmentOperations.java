package com.example.driveronboardingservice.operations;

import com.example.driveronboardingservice.exception.ResourceNotFoundException;
import com.example.driveronboardingservice.exception.ValidationException;
import com.example.driveronboardingservice.model.ShipmentDTO;

public interface IShipmentOperations {
    void createShipment(ShipmentDTO shipmentDTO) throws ResourceNotFoundException, ValidationException;

    void updateShipment(ShipmentDTO shipmentDTO) throws ValidationException;

    ShipmentDTO getShipment(Short stepId, String driverId) throws ValidationException;
    ShipmentDTO getShipmentByOrderId(String orderId) throws ValidationException;
}

