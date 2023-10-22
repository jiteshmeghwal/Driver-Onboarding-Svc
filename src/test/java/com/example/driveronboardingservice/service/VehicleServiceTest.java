package com.example.driveronboardingservice.service;

import com.example.driveronboardingservice.constant.VehicleType;
import com.example.driveronboardingservice.entity.Vehicle;
import com.example.driveronboardingservice.model.VehicleDTO;
import com.example.driveronboardingservice.repository.VehicleRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;

@ExtendWith(MockitoExtension.class)
class VehicleServiceTest {

    @Mock
    VehicleRepository vehicleRepository;

    @InjectMocks
    VehicleService vehicleService;

    private final String driverId = "User";

    @Test
    void getVehiclesNoVehicleFound() {
        Mockito.when(vehicleRepository.findByDriverId(driverId)).thenReturn(new ArrayList<>());
        List<VehicleDTO> vehicleDTOS = vehicleService.getVehicles(driverId);
        Assertions.assertEquals(0, vehicleDTOS.size());
        Mockito.verify(vehicleRepository, Mockito.times(1)).findByDriverId(driverId);
    }

    @Test
    void getVehicles() {
        List<Vehicle> vehicles = new ArrayList<>();
        vehicles.add(getVehicleDE());
        Mockito.when(vehicleRepository.findByDriverId(driverId)).thenReturn(vehicles);
        List<VehicleDTO> vehicleDTOS = vehicleService.getVehicles(driverId);
        Assertions.assertEquals(1, vehicleDTOS.size());
        Mockito.verify(vehicleRepository, Mockito.times(1)).findByDriverId(driverId);
    }

    private Vehicle getVehicleDE() {
        Vehicle vehicle = new Vehicle();
        vehicle.setVehicleType(VehicleType.THREE_WHEELER.getCode());
        vehicle.setModel("Piaggio");
        vehicle.setRegNo("RJ14CE5321");
        return vehicle;
    }
}