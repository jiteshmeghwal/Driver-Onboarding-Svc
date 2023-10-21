package com.example.driveronboardingservice.repository;

import com.example.driveronboardingservice.entity.Vehicle;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface VehicleRepository extends CrudRepository<Vehicle, Long> {
    List<Vehicle> findByDriverId(String driverId);
}
