package com.example.driveronboardingservice.constant;

import lombok.Getter;

public enum OnboardingStepType {
    DOC_UPLOAD((short)2),
    BACKGROUND_VERIFICATION((short)3),
    SHIPMENT((short)4);

    @Getter
    private Short code;
    OnboardingStepType(Short code) {
        this.code = code;
    }

    public static OnboardingStepType getByCode(Short code) {
        for(OnboardingStepType stepType : OnboardingStepType.values()) {
            if(stepType.getCode() == code) return stepType;
        }
        return null;
    }
}
