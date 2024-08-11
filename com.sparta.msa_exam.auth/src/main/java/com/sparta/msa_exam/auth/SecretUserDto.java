package com.sparta.msa_exam.auth;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;

@Getter
public class SecretUserDto {
    private String username;
    private String password;
    private UserRole role;

    @Builder(access = AccessLevel.PRIVATE)
    public SecretUserDto(String username, String password, UserRole role) {
        this.username = username;
        this.password = password;
        this.role = role;
    }

    public static SecretUserDto EntityToDto(User user) {
        return SecretUserDto.builder()
                .username(String.valueOf(user.getId()))
                .password(user.getPassword())
                .role(user.getRole())
                .build();
    }
}
