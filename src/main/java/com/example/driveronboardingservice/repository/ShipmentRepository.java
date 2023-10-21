package com.example.driveronboardingservice.repository;

import com.example.driveronboardingservice.entity.Shipment;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ShipmentRepository extends CrudRepository<Shipment, Long> {
    Optional<Shipment> findByOrderId(String orderId);

    @Query("Select s from Shipment s WHERE s.stepId = :stepId and s.driverId = :driverId")
    Optional<Shipment> findByStepIdAndDriverId(Short stepId, String driverId);
}
