package com.example.driveronboardingservice.dao.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

@Entity
@Table(name = "onboarding_step")
@Data
public class OnboardingStep {
    @Id
    @Column(name = "step_id")
    private short stepId;
    @Column(name = "step_type_cd", nullable = false)
    private short stepTypeCd;
    @Column(name = "step_title", nullable = false)
    private String stepTitle;
    @Column(name = "step_desc", nullable = false)
    private String stepDesc;
}
