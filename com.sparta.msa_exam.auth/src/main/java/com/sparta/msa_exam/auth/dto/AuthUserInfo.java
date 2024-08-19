package com.sparta.msa_exam.auth.dto;

import com.sparta.msa_exam.auth.domain.User;
import com.sparta.msa_exam.auth.domain.UserRole;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;

@Getter
public class AuthUserInfo {
    private String username;
    private String password;
    private UserRole role;

    @Builder(access = AccessLevel.PRIVATE)
    public AuthUserInfo(String username, String password, UserRole role) {
        this.username = username;
        this.password = password;
        this.role = role;
    }

    public static AuthUserInfo EntityToDto(User user) {
        return AuthUserInfo.builder()
                .username(String.valueOf(user.getId()))
                .password(user.getPassword())
                .role(user.getRole())
                .build();
    }
}
