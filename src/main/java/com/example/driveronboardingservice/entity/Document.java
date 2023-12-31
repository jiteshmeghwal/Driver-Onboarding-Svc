package com.example.driveronboardingservice.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.sql.Timestamp;

@Entity
@Table(name = "document")
@Data
public class Document {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "doc_id")
    private Long docId;
    @Column(name = "doc_name", nullable = false)
    private String docName;
    @Column(name = "doc_upload_time", nullable = false)
    private Timestamp docUploadTime;
    @Column(name = "valid_till")
    private Timestamp validTill;
    @Column(name = "driver_id", nullable = false)
    private String driverId;
    @Column(name = "step_id", nullable = false)
    private Short stepId;

    @ManyToOne
    @JoinColumn(name = "driver_id", insertable = false, updatable = false)
    private DriverProfile driver;

}
