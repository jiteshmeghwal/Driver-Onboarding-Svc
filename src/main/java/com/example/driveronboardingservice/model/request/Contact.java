package com.example.driveronboardingservice.model.request;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class Contact {
    private String phone;
    private String email;
}
