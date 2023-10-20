package com.example.driveronboardingservice.dao;

import com.example.driveronboardingservice.constant.OnboardingStepType;
import com.example.driveronboardingservice.constant.QueryConstants;
import com.example.driveronboardingservice.dao.entity.OnboardingStep;
import com.example.driveronboardingservice.model.OnboardingStepDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class CustomJDBCQueryExecutor {
    @Autowired
    private NamedParameterJdbcTemplate jdbcTemplate;

    public List<OnboardingStepDTO> getOnboardingStepsByDriver(String driverId
            , Map<Short, OnboardingStep> onboardingStepMap) {
        MapSqlParameterSource parameterSource = new MapSqlParameterSource();
        parameterSource.addValue("driverId", driverId);
        return jdbcTemplate.query(QueryConstants.GET_ALL_ONBOARDING_STEP_INSTANCES_BY_USER,
                parameterSource,
                rs -> {
                    List<OnboardingStepDTO> onboardingSteps = new ArrayList<>();
                    Map<Short, Boolean> stepStatusMap = new HashMap<>();
                    while (rs.next()) {
                        stepStatusMap.put(rs.getShort("stepId"),
                                rs.getBoolean("completeInd"));
                    }
                    for(Short stepId : onboardingStepMap.keySet()) {
                        OnboardingStep onboardingStep = onboardingStepMap.get(stepId);
                        OnboardingStepDTO.OnboardingStepDTOBuilder builder = OnboardingStepDTO.builder()
                                .stepId(stepId)
                                .stepTypeCd(onboardingStep.getStepTypeCd())
                                .stepTypeDesc(OnboardingStepType.getByCode(onboardingStep.getStepTypeCd()).name())
                                .stepTitle(onboardingStep.getStepTitle())
                                .stepDesc(onboardingStep.getStepDesc());
                        if(stepStatusMap.containsKey(stepId)) {
                            builder.isComplete(stepStatusMap.get(stepId));
                        }
                        onboardingSteps.add(builder.build());
                    }
                    return onboardingSteps;
                });
    }
}
