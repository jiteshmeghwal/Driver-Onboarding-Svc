package com.example.driveronboardingservice.dao.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.sql.Timestamp;

@Entity
@Table(name = "shipment")
@Data
public class Shipment {
    @Id
    @Column(name = "shipment_id")
    private Long id;

    @Column(name = "order_id", nullable = false)
    private String orderId;

    @Column(name = "status_cd", nullable = false)
    private Short status;

    @Column(name = "carrier")
    private String carrier;

    @Column(name = "order_date", nullable = false)
    private Timestamp orderDate;

    @Column(name = "last_update_time")
    private Timestamp lastUpdateTime;

    @ManyToOne
    @JoinColumn(name = "driver_id")
    private DriverProfile driver;

    @OneToOne
    @JoinColumn(name = "step_instance_id")
    private OnboardingStepInstance stepInstance;
}
