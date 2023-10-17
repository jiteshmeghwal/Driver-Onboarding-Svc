package com.example.driveronboardingservice.controller;

import com.example.driveronboardingservice.exception.ResourceNotFoundException;
import com.example.driveronboardingservice.exception.ValidationException;
import com.example.driveronboardingservice.model.DriverDTO;
import com.example.driveronboardingservice.model.request.GenericDriverProfileRequest;
import com.example.driveronboardingservice.service.DriverProfileService;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/driver-profile")
@Transactional
public class DriverProfileController {

    @Autowired
    DriverProfileService driverProfileService;

    @PostMapping
    public ResponseEntity<Object> createDriverProfile(
            @RequestBody GenericDriverProfileRequest createProfileRequest) throws ValidationException {
        driverProfileService.createProfile(createProfileRequest);
        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    @GetMapping
    public DriverDTO getDriverProfile() throws ResourceNotFoundException {
        return driverProfileService.getDriverDetails();
    }
}
