package com.example.driveronboardingservice.constant;

public class QueryConstants {
    public static final String GET_ALL_ONBOARDING_STEP_INSTANCES_BY_USER =
            "SELECT   OSI.step_id as stepId,  " +
            "            OSI.complete_ind as completeInd," +
            "            DP.driver_id as driverId " +
            "            FROM dbo.driver_profile DP " +
            "            INNER JOIN dbo.onboarding_step_instance OSI  " +
            "            ON DP.driver_id = OSI.driver_id " +
            "            WHERE DP.driver_id = :driverId ";
}
