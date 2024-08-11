package com.sparta.msa_exam.auth;

import lombok.Getter;

@Getter
public class AuthenticationDetails {
    private String userId;
    private String role;

    public AuthenticationDetails(String userId, String role) {
        this.userId = userId;
        this.role = role;
    }
}