package com.example.driveronboardingservice.controller;

import com.example.driveronboardingservice.exception.ValidationException;
import com.example.driveronboardingservice.model.ShipmentDTO;
import com.example.driveronboardingservice.service.ShipmentService;
import com.example.driveronboardingservice.util.RequestContextStore;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/shipment")
public class ShipmentController {
    @Autowired
    private ShipmentService shipmentService;

    @GetMapping
    public ShipmentDTO getShipment(@RequestHeader("stepId") Short stepId) throws ValidationException {
        return shipmentService.getShipmentDTO(stepId, RequestContextStore.getUser().getUsername());
    }

    @GetMapping("/{userId}")
    @Secured("ADMIN")
    public ShipmentDTO getShipment(@RequestHeader("stepId") Short stepId, @PathVariable("userId")String userId)
            throws ValidationException {
        return shipmentService.getShipmentDTO(stepId, userId);
    }
}
