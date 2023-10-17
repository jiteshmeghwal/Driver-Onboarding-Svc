package com.example.driveronboardingservice.model.auth;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.springframework.security.core.CredentialsContainer;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;

/**
 * Custom User class to represent authenticated user
 * @author Jitesh Meghwal
 */
@Getter
@Setter
@Builder
public class CustomUser implements UserDetails, CredentialsContainer {
    private String username;
    private String password;
    private Collection<? extends GrantedAuthority> authorities;
    private String fullName;
    private String phone;
    private String email;
    private String[] roles;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return username;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

    @Override
    public void eraseCredentials() {

    }
}
