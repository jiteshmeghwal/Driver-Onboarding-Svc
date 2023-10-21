package com.example.driveronboardingservice.repository;

import com.example.driveronboardingservice.entity.OnboardingStepInstance;
import com.example.driveronboardingservice.entity.OnboardingStepInstancePK;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OnboardingStepInstanceRepository extends CrudRepository<OnboardingStepInstance, OnboardingStepInstancePK> {

    @Query("SELECT osi FROM OnboardingStepInstance osi WHERE osi.onboardingStepInstancePK.driverId = :driverId")
    List<OnboardingStepInstance> findAllByDriverId(@Param("driverId") String driverId);
}