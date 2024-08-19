package com.sparta.msa_exam.auth.dto;

import lombok.Getter;

@Getter
public class AuthUserInfoRequest {
    private String userId;
    private String authority;

    public AuthUserInfoRequest(String userId, String authority) {
        this.userId = userId;
        this.authority = authority;
    }
}