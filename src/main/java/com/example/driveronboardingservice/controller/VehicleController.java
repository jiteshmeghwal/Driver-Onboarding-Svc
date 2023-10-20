package com.example.driveronboardingservice.controller;

import com.example.driveronboardingservice.model.VehicleDTO;
import com.example.driveronboardingservice.service.VehicleService;
import com.example.driveronboardingservice.util.RequestContextStore;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/vehicles")
public class VehicleController {
    @Autowired
    VehicleService vehicleService;

    @GetMapping
    public ResponseEntity<List<VehicleDTO>> getVehicle() {
        List<VehicleDTO> vehicleList = vehicleService.getVehicles(RequestContextStore.getUser().getUsername());
        if(vehicleList.isEmpty()) return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        return new ResponseEntity<>(vehicleList, HttpStatus.OK);
    }
}
