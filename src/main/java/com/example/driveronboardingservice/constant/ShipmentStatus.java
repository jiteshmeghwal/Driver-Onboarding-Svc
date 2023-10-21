package com.example.driveronboardingservice.constant;

public enum ShipmentStatus {
    FAILED((short)0),
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

    public static ShipmentStatus getByCode(Short code) {
        for(ShipmentStatus shipmentStatus : ShipmentStatus.values()) {
            if(shipmentStatus.getCode().equals(code)) return shipmentStatus;
        }
        return null;
    }
}
