package com.example.driveronboardingservice.entity;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "onboarding_step")
@Data
public class OnboardingStep {
    @Id
    @Column(name = "step_id")
    private Short stepId;
    @Column(name = "step_type_cd", nullable = false)
    private Short stepTypeCd;
    @Column(name = "step_title", nullable = false)
    private String stepTitle;
    @Column(name = "step_desc", nullable = false)
    private String stepDesc;
}
