package com.example.driveronboardingservice.constant;

public enum ShipmentStatus {
    CREATED((short)0),
    FAILED((short)1),
    ORDERED((short)2),
    SHIPPED((short)3),
    DELIVERED((short)4);
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
