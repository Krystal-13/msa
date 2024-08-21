package com.sparta.msa_exam.auth.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
public class AuthUserInfoResponse {
    private String userId;
    private String role;

    @Builder
    public AuthUserInfoResponse(String userId, String role) {
        this.userId = userId;
        this.role = role;
    }
}

