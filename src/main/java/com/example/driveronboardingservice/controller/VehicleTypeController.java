package com.example.driveronboardingservice.controller;

import com.example.driveronboardingservice.constant.VehicleType;
import com.example.driveronboardingservice.model.VehicleTypeDTO;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/vehicle-types")
public class VehicleTypeController {
    private static final Logger logger = LogManager.getLogger(VehicleTypeController.class);
    @GetMapping
    @Cacheable("vehicleTypesCache")
    public List<VehicleTypeDTO> getVehicleTypes() {
        return Arrays.stream(VehicleType.values())
                .map(vehicleType -> new VehicleTypeDTO(vehicleType.getCode(), vehicleType.getType()))
                .collect(Collectors.toList());
    }
}
