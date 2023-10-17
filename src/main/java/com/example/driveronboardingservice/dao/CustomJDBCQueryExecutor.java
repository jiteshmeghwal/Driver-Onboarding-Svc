package com.example.driveronboardingservice.dao;

import com.example.driveronboardingservice.constant.OnboardingStepType;
import com.example.driveronboardingservice.constant.QueryConstants;
import com.example.driveronboardingservice.model.OnboardingStepDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;

import java.util.ArrayList;
import java.util.List;

public class CustomJDBCQueryExecutor {
    @Autowired
    private NamedParameterJdbcTemplate jdbcTemplate;

    public List<OnboardingStepDTO> getOnboardingStepsByUser(String userId) {
        MapSqlParameterSource parameterSource = new MapSqlParameterSource();
        parameterSource.addValue("driverId", userId);
        return jdbcTemplate.query(QueryConstants.GET_ALL_ONBOARDING_STEP_INSTANCES_BY_USER,
                parameterSource,
                rs -> {
                    List<OnboardingStepDTO> onboardingSteps = new ArrayList<>();
                    while (rs.next()) {
                        long stepId = rs.getLong("stepId");
                        short stepTypeCd = rs.getShort("stepTypeCd");
                        OnboardingStepDTO.OnboardingStepDTOBuilder builder = OnboardingStepDTO.builder()
                                .stepTypeCd(stepTypeCd)
                                .stepTypeDesc(OnboardingStepType.getByCode(stepTypeCd).name())
                                .stepTitle(rs.getString("stepTitle"))
                                .stepDesc(rs.getString("stepDesc"));

                        if (stepId != 0) {
                            builder.isComplete(rs.getBoolean("completeInd"));
                        } else {
                            builder.isComplete(false);
                        }

                        onboardingSteps.add(builder.build());
                    }
                    return onboardingSteps;
                });
    }
}
