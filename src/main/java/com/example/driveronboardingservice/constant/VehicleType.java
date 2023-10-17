package com.example.driveronboardingservice.constant;

/**
 * Enum class for different vehicle types
 * @author Jitesh Meghwal
 */
public enum VehicleType {
    TWO_WHEELER((short) 2, "2-Wheeler"),
    THREE_WHEELER((short)3, "3-Wheeler"),
    FOUR_WHEELER((short)4, "4-wheeler");

    private final short code;
    private final String type;

    VehicleType(short code, String type) {
        this.code = code;
        this.type = type;
    }

    public short getCode() {
        return code;
    }

    public String getType() {
        return type;
    }

    public static VehicleType getByCode(short code) {
        for(VehicleType v : VehicleType.values()) {
            if(v.code == code) return v;
        }
        return null;
    }
}
