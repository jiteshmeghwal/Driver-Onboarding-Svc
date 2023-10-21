package com.example.driveronboardingservice.repository;

import com.example.driveronboardingservice.entity.DriverProfile;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface DriverProfileRepository extends CrudRepository<DriverProfile, String> {
    Optional<DriverProfile> findByDriverId(String driverId);

    @Query("UPDATE DriverProfile dp SET dp.isAvailable = :available WHERE dp.driverId = :driverId")
    int updateAvailability(boolean available, String driverId);
}
