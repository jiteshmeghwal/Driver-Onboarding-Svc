package com.example.driveronboardingservice.model;


import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ErrorResponse {
    private String errorCode, errorDesc;
}
