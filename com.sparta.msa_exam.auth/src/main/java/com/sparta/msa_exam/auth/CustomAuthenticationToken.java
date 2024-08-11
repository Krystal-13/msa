package com.sparta.msa_exam.auth;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;

public class CustomAuthenticationToken extends UsernamePasswordAuthenticationToken {
    private final String role;

    public CustomAuthenticationToken(
            Object principal,
            Object credentials,
            String role
    ) {
        super(principal, credentials);
        this.role = role;
        super.setAuthenticated(false);
    }

    public CustomAuthenticationToken(
            Object principal,
            Object credentials,
            Collection<? extends GrantedAuthority> authorities,
            String role
    ) {
        super(principal, credentials, authorities);
        this.role = role;
        super.setAuthenticated(true);
    }

    public String getRole() {
        return role;
    }

}

