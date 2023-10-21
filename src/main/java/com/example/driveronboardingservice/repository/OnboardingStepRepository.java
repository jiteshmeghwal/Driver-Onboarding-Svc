package com.example.driveronboardingservice.repository;

import com.example.driveronboardingservice.entity.OnboardingStep;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OnboardingStepRepository extends CrudRepository<OnboardingStep, Short> {
}
