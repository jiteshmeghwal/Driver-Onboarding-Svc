package com.example.driveronboardingservice.constant;

public enum ShipmentStatus {
    ORDERED((short)1),
    SHIPPED((short)2),
    DELIVERED((short)3);
    private short code;

    private ShipmentStatus(short code) {
        this.code = code;
    }

    private short getCode(ShipmentStatus shipmentStatus) {
        return shipmentStatus.code;
    }

    private ShipmentStatus getByCode(short code) {
        for(ShipmentStatus status : ShipmentStatus.values()) {
            if(status.code == code) return status;
        }
        return null;
    }
}
