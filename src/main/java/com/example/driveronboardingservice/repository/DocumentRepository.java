package com.example.driveronboardingservice.repository;

import org.springframework.data.repository.CrudRepository;
import com.example.driveronboardingservice.entity.Document;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface DocumentRepository extends CrudRepository<Document, Long> {
    Optional<Document> findByDriverIdAndStepId(String driverId, Short stepId);
}
