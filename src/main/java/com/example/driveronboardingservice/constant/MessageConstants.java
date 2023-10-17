package com.example.driveronboardingservice.constant;

public enum MessageConstants {
    PROFILE_ALREADY_EXISTS("ERR-1", "Profile with given email/phone already exist!"),
    PROFILE_DOES_NOT_EXISTS("ERR-2", "Profile with given email/phone does not exist!"),
    DOCUMENT_NOT_FOUND("ERR-3", "Requested file not found"),
    GENERIC_ERROR("ERR-4", "Some error occurred!"),
    STEP_DOES_NOT_MATCH_WITH_USER("ERR-5", "Step does not match with user");

    private String code, desc;

    MessageConstants(String code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    public String getCode() {
        return code;
    }

    public String getDesc() {
        return desc;
    }
}
