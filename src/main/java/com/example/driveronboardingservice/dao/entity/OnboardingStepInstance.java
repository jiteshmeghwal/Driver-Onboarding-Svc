package com.example.driveronboardingservice.dao.entity;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "onboarding_step_instance")
@Data
public class OnboardingStepInstance {
    @Id
    @Column(name = "step_instance_id")
    private long stepInstanceId;
    @Column(name = "complete_ind", nullable = false)
    private boolean isComplete;
    @Column(name = "step_id", nullable = false)
    private short stepId;
    @Column(name = "driver_id", nullable = false)
    private String driverId;

    @OneToOne
    @JoinColumn(name="step_id", insertable = false, updatable = false)
    private OnboardingStep step;
    @OneToOne
    @JoinColumn(name="driver_id", insertable = false, updatable = false)
    private DriverProfile driver;
}
