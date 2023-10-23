package com.example.driveronboardingservice.service;

import com.example.driveronboardingservice.constant.VehicleType;
import com.example.driveronboardingservice.entity.Vehicle;
import com.example.driveronboardingservice.model.VehicleDTO;
import com.example.driveronboardingservice.operations.IVehicleOperations;
import com.example.driveronboardingservice.repository.VehicleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class VehicleService implements IVehicleOperations {
    @Autowired
    VehicleRepository vehicleRepository;

    @Override
    public List<VehicleDTO> getVehicles(String driverId) {
        List<Vehicle> vehicles = vehicleRepository.findByDriverId(driverId);
        return vehicles.stream().map(this::getVehicleDTO).collect(Collectors.toList());
    }

    private VehicleDTO getVehicleDTO(Vehicle vehicle) {
        return VehicleDTO.builder()
                .regNo(vehicle.getRegNo())
                .modelName(vehicle.getModel())
                .vehicleType(VehicleType.getByCode(vehicle.getVehicleType()).getType())
                .build();
    }
}
