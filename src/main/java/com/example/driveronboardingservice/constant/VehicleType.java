package com.example.driveronboardingservice.constant;

/**
 * Enum class for different vehicle types
 * @author Jitesh Meghwal
 */
public enum VehicleType {
    TWO_WHEELER((short) 2, "2-Wheeler"),
    THREE_WHEELER((short)3, "3-Wheeler"),
    FOUR_WHEELER((short)4, "4-wheeler");

    private final Short code;
    private final String type;

    VehicleType(Short code, String type) {
        this.code = code;
        this.type = type;
    }

    public Short getCode() {
        return code;
    }

    public String getType() {
        return type;
    }

    public static VehicleType getByCode(Short code) {
        for(VehicleType v : VehicleType.values()) {
            if(v.code == code) return v;
        }
        return null;
    }
}
