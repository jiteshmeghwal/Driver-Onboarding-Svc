package com.example.driveronboardingservice.repository;

import com.example.driveronboardingservice.dao.entity.OnboardingStepInstance;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface OnboardingStepInstanceRepository extends CrudRepository<OnboardingStepInstance, Long> {
    Optional<OnboardingStepInstance> findByStepInstanceIdAndDriverId(long stepInstanceId, String driverId);
}
