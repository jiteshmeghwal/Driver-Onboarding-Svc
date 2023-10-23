package com.example.driveronboardingservice.operations;

import com.example.driveronboardingservice.model.VehicleDTO;

import java.util.List;

public interface IVehicleOperations {
    List<VehicleDTO> getVehicles(String driverId);
}

