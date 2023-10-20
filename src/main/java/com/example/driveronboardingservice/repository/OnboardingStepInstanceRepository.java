package com.example.driveronboardingservice.repository;

import com.example.driveronboardingservice.dao.entity.OnboardingStepInstance;
import com.example.driveronboardingservice.dao.entity.OnboardingStepInstancePK;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OnboardingStepInstanceRepository extends CrudRepository<OnboardingStepInstance, OnboardingStepInstancePK> {
}