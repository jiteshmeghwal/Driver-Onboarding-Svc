package com.example.driveronboardingservice.model;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class ShipmentDTO {
    private Long shipmentId;
    private String orderId;
    private LocalDateTime orderDate;
    private String carrier;
    private Short status;
    private String statusDesc;
    private String driverId;
    private Short stepId;
}