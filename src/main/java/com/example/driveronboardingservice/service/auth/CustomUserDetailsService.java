package com.example.driveronboardingservice.service.auth;

import com.example.driveronboardingservice.config.SecurityConfig;
import com.example.driveronboardingservice.model.auth.CustomUser;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;

@Service
public class CustomUserDetailsService implements UserDetailsService {
    @Override
    public UserDetails loadUserByUsername(String username) {
        if("user".equals(username)) {
            return CustomUser.builder()
                .username("user")
                .fullName("user")
                .email("user@xyz.com")
                .phone("9876443210")
                .password(SecurityConfig.passwordEncoder().encode("password"))
                .roles(new String[]{"USER"}).build();

        } else if("admin".equals(username)) {
            return CustomUser.builder()
                .username("admin")
                .fullName("admin")
                .email("admin@xyz.com")
                .phone("8769543210")
                .password(SecurityConfig.passwordEncoder().encode("password"))
                .roles(new String[]{"ADMIN"}).build();
        }
        return null;
    }
}
