package com.example.driveronboardingservice.dao.entity;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "onboarding_step_instance")
@Data
public class OnboardingStepInstance {
    @EmbeddedId
    @AttributeOverrides({
            @AttributeOverride(name = "stepId", column = @Column(name = "step_id")),
            @AttributeOverride(name = "driverId", column = @Column(name = "driver_id"))
    })
    private OnboardingStepInstancePK onboardingStepInstancePK;
    @Column(name = "complete_ind", nullable = false)
    private boolean isComplete;
}
