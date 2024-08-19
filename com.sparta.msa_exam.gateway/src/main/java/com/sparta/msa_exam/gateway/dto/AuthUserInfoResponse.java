package com.sparta.msa_exam.gateway.dto;

import lombok.Getter;

@Getter
public class AuthUserInfoResponse {
    private String userId;
    private String role;

    public AuthUserInfoResponse(String userId, String role) {
        this.userId = userId;
        this.role = role;
    }
}

