package com.example.driveronboardingservice.constant;

public enum MessageConstants {
    PROFILE_ALREADY_EXIST("ERR-1", "Profile with given email or phone already exist!"),
    PROFILE_DOES_NOT_EXIST("ERR-2", "Requested profile does not exist!"),
    DOCUMENT_NOT_FOUND("ERR-3", "Requested file not found"),
    GENERIC_ERROR("ERR-4", "Some error occurred!"),
    INVALID_STEP("ERR-5", "Invalid step"),
    STEP_IS_ALREADY_COMPLETE("ERR-7", "Step is already complete"),
    DOCUMENT_ALREADY_EXIST("ERR-8", "Document already exist for provided step," +
            " please delete to re-upload"),
    SHIPMENT_NOT_FOUND("ERR-9", "Shipment with given order Id not found");

    private final String code;
    private final String desc;

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
