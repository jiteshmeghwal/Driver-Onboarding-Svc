package com.example.driveronboardingservice.dao.entity;

import jakarta.persistence.Embeddable;
import lombok.Data;

import java.io.Serializable;

@Embeddable
@Data
public class OnboardingStepInstancePK implements Serializable {
    private Short stepId;
    private String driverId;
}
