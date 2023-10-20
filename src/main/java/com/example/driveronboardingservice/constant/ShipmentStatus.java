package com.example.driveronboardingservice.constant;

public enum ShipmentStatus {
    ORDERED((short)1),
    SHIPPED((short)2),
    DELIVERED((short)3);
    private final Short code;

    private ShipmentStatus(Short code) {
        this.code = code;
    }

    public Short getCode() {
        return code;
    }
}
