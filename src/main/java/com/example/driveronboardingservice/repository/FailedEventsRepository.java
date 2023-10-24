package com.example.driveronboardingservice.repository;

import com.example.driveronboardingservice.entity.FailedEvents;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FailedEventsRepository extends CrudRepository<FailedEvents, Long> {
}
