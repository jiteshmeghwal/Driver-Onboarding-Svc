package com.example.driveronboardingservice.model.auth;

import lombok.Getter;
import lombok.Setter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Custom User class to represent authenticated user
 * @author Jitesh Meghwal
 */
@Getter
@Setter
public class CustomUser extends User {
    private String fullName;
    private String phone;
    private String email;

    public CustomUser(String username, String password, List<String> roles,
                      String fullName, String phone, String email) {
        super(username, password, mapToGrantedAuthorities(roles));
        this.fullName = fullName;
        this.phone = phone;
        this.email = email;
    }

    private static Collection<? extends GrantedAuthority> mapToGrantedAuthorities(List<String> roles) {
        return roles.stream()
                .map(SimpleGrantedAuthority::new)
                .collect(Collectors.toList());
    }
}
