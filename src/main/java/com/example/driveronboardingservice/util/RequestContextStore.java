package com.example.driveronboardingservice.util;

import com.example.driveronboardingservice.model.auth.CustomUser;
import org.springframework.security.core.context.SecurityContextHolder;

public class RequestContextStore {
    public static CustomUser getUser() {
        return (CustomUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    }
}
