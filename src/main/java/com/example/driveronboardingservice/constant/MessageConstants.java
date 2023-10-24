package com.example.driveronboardingservice.constant;

public enum MessageConstants {
    PROFILE_ALREADY_EXIST("ERR-1", "Profile with given email or phone already exist!"),
    PROFILE_DOES_NOT_EXIST("ERR-2", "Requested profile does not exist!"),
    DOCUMENT_NOT_FOUND("ERR-3", "Requested file not found"),
    GENERIC_ERROR("ERR-4", "Some error occurred!"),
    INVALID_STEP("ERR-5", "Invalid step"),
    SHIPMENT_ALREADY_EXIST("ERR-6", "Shipment already exist for given step Id and driver Id"),
    STEP_IS_ALREADY_COMPLETE("ERR-7", "Step is already complete"),
    DOCUMENT_ALREADY_EXIST("ERR-8", "Document already exist for provided step," +
            " please delete to re-upload"),
    SHIPMENT_NOT_FOUND("ERR-9", "Shipment not found"),
    FILE_NAME_IS_NULL("ERR-10", "Uploaded file name shouldn't be null"),
    INVALID_REQUEST("ERR-11", "Invalid request"),
    ONBOARDING_STEPS_PENDING("ERR-12", "Onboarding steps are pending"),
    STEP_ALREADY_IN_REQUESTED_STATUS("ERR-13", "Onboarding step already in requested status");
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
