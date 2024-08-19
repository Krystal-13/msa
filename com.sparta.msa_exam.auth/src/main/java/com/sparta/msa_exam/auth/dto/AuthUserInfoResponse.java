package com.sparta.msa_exam.auth.dto;

import com.sparta.msa_exam.auth.domain.User;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;

@Getter
public class AuthUserInfoResponse {
    private String userId;
    private String role;

    @Builder(access = AccessLevel.PRIVATE)
    public AuthUserInfoResponse(String userId, String role) {
        this.userId = userId;
        this.role = role;
    }

    public static AuthUserInfoResponse EntityToDto(User user) {
        return AuthUserInfoResponse.builder()
                .userId(String.valueOf(user.getId()))
                .role(user.getRole().name())
                .build();
    }
}

