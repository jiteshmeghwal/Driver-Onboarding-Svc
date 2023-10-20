package com.example.driveronboardingservice.model;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ShipmentDTO {
    private Long shipmentId;
    private String orderId;
    private String carrier;
    private Short status;
    private String driverId;
    private Short stepId;
}