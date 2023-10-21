package com.example.driveronboardingservice.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ForbiddenException extends Exception{
    private String code, desc;
}
