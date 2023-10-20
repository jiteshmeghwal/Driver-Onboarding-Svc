package com.example.driveronboardingservice.dao.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.sql.Timestamp;

@Entity
@Table(name = "shipment")
@Data
public class Shipment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
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

    @Column(name = "driver_id", nullable = false)
    private String driverId;

    @Column(name = "step_id", nullable = false)
    private Short stepId;

    @ManyToOne
    @JoinColumn(name = "driver_id", insertable = false, updatable = false)
    private DriverProfile driver;
}
