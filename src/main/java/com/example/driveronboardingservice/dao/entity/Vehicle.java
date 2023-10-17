package com.example.driveronboardingservice.dao.entity;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "vehicle")
@Data
public class Vehicle {
    @Id
    @Column(name = "vehicle_id")
    private Long vehicleId;
    @Column(name = "vehicle_model", nullable = false)
    private String model;
    @Column(name = "vehicle_reg_no", nullable = false)
    private String regNo;
    @Column(name = "vehicle_type_cd", nullable = false)
    private Short vehicleType;
    @Column(name = "driver_id", nullable = false)
    private String driverId;

    @OneToOne
    @JoinColumn(name = "driver_id", insertable = false, updatable = false)
    private DriverProfile driver;
}
