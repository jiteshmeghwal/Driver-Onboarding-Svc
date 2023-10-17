package com.example.driveronboardingservice.service;

import com.example.driveronboardingservice.constant.VehicleType;
import com.example.driveronboardingservice.dao.entity.Vehicle;
import com.example.driveronboardingservice.model.VehicleDTO;
import com.example.driveronboardingservice.repository.VehicleRepository;
import com.example.driveronboardingservice.util.RequestContextStore;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class VehicleService {
    @Autowired
    VehicleRepository vehicleRepository;

    public List<VehicleDTO> getVehicles() {
        List<Vehicle> vehicles = vehicleRepository.findByDriverId(
                RequestContextStore.getUser().getUsername());
        return vehicles.stream().map(this::getVehicleDTO).collect(Collectors.toList());
    }

    private VehicleDTO getVehicleDTO(Vehicle vehicle) {
        return VehicleDTO.builder()
                .regNo(vehicle.getRegNo())
                .vehicleType(VehicleType.getByCode(vehicle.getVehicleType()).getType())
                .build();
    }
}
