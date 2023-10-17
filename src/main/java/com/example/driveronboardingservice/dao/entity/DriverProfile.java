package com.example.driveronboardingservice.dao.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "driver_profile")
@Data
public class DriverProfile {
    @Id
    @Column(name = "driver_id")
    private String driverId;
    @Column(name = "addr_line_1", nullable = false)
    private String addrLine1;
    @Column(name = "addr_line_2", nullable = false)
    private String addrLine2;
    @Column(name = "city", nullable = false)
    private String city;
    @Column(name = "zip_code", nullable = false)
    private String zipCode;
    @Column(name = "available_ind", nullable = false)
    private boolean isAvailable;

    @OneToMany(mappedBy = "driver", fetch = FetchType.LAZY)
    private List<Vehicle> vehicles = new ArrayList<>();
    @OneToMany(mappedBy = "driver", fetch = FetchType.LAZY)
    private List<Shipment> shipments = new ArrayList<>();
    @OneToMany(mappedBy = "driver", fetch = FetchType.LAZY)
    private List<Document> documents = new ArrayList<>();
}
