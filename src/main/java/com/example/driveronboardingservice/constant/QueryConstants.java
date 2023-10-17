package com.example.driveronboardingservice.constant;

public class QueryConstants {
    public static final String GET_ALL_ONBOARDING_STEP_INSTANCES_BY_USER =
            "SELECT OS.step_id as stepId, " +
            "OS.step_type_cd as stepTypeCd, " +
            "OS.step_title as stepTitle, " +
            "OS.step_desc as stepDesc, " +
            "OSI.complete_ind as completeInd, " +
            "FROM dbo.driver_profile DP " +
            "INNER JOIN dbo.onboarding_step_instance OSI " +
            "ON DP.driver_id = OSI.driver_id " +
            "RIGHT OUTER JOIN dbo.onboarding_step OS " +
            "ON OS.step_id = OSI.step_id " +
            "WHERE DP.driver_id = :driverId ";
}
