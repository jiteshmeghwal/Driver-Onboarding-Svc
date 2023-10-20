package com.example.driveronboardingservice.repository;

import com.example.driveronboardingservice.dao.entity.Shipment;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ShipmentRepository extends CrudRepository<Shipment, Long> {
    Optional<Shipment> findByOrderId(String orderId);
}
