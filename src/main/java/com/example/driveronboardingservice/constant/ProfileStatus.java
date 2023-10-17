package com.example.driveronboardingservice.constant;

/**
 * Enum class for different profile status
 * @author Jitesh Meghwal
 */
public enum ProfileStatus {
    CREATED((short)1),
    ONBOARDING_STEPS_COMPLETE((short)2),
    VERIFIED((short)3),
    COMPLETE((short)3);

    private final short statusCode;

    private ProfileStatus(short statusCode) {
        this.statusCode = statusCode;
    }

    public short getStatusCode() {
        return this.statusCode;
    }

    public ProfileStatus getByStatusCode(short code) {
        for(ProfileStatus p : ProfileStatus.values()) {
            if(p.statusCode == code) return p;
        }
        return null;
    }
}
