package com.example.driveronboardingservice.service;

import com.example.driveronboardingservice.client.TrackingDeviceOrderClient;
import com.example.driveronboardingservice.constant.ShipmentStatus;
import com.example.driveronboardingservice.exception.ResourceNotFoundException;
import com.example.driveronboardingservice.exception.ValidationException;
import com.example.driveronboardingservice.model.ShipmentDTO;
import com.example.driveronboardingservice.model.response.CreateOrderResponse;
import com.example.driveronboardingservice.operations.IOrderServiceOperations;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;

import java.time.LocalDateTime;

@Service
public class TrackingDeviceOrderService implements IOrderServiceOperations {
    private static final Logger logger = LogManager.getLogger(TrackingDeviceOrderService.class);
    @Autowired
    private TrackingDeviceOrderClient trackingDeviceOrderClient;
    @Autowired
    private ShipmentService shipmentService;
    @Override
    public void initiateOrder(String driverId, Short stepId) throws ValidationException {
        try {
            CreateOrderResponse response = trackingDeviceOrderClient.createOrder(driverId);
            shipmentService.createShipment(
                    ShipmentDTO.builder()
                            .orderId(response.getOrderId())
                            .orderDate(LocalDateTime.now())
                            .status(ShipmentStatus.ORDERED.getCode())
                            .driverId(driverId)
                            .stepId(stepId)
                            .build()
            );
        } catch (HttpServerErrorException | HttpClientErrorException exception) {
            shipmentService.createShipment(
                    ShipmentDTO.builder()
                            .status(ShipmentStatus.FAILED.getCode())
                            .driverId(driverId)
                            .stepId(stepId)
                            .build()
            );
        } catch (ResourceNotFoundException e) {
            //should never reach here
            logger.error("User {} not found", driverId);
        }
    }
}
