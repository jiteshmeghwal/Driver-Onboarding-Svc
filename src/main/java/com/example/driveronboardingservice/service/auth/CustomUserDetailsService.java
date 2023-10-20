package com.example.driveronboardingservice.service.auth;

import com.example.driveronboardingservice.config.SecurityConfig;
import com.example.driveronboardingservice.model.auth.CustomUser;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CustomUserDetailsService implements UserDetailsService {
    @Override
    public UserDetails loadUserByUsername(String username) {
        String password = SecurityConfig.passwordEncoder().encode("password");
        if("user".equals(username)) {
            return new CustomUser("user", password
                    , List.of("USER"), "user", "9876443210", "user@xyz.com");
        } else if("admin".equals(username)) {
            return new CustomUser("admin", password,
                    List.of("ADMIN"), "admin", "8769543210", "admin@xyz.com");
        }
        return null;
    }
}
