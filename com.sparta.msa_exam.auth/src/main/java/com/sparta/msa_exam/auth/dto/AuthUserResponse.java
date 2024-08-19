package com.sparta.msa_exam.auth.dto;

import com.sparta.msa_exam.auth.domain.User;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;


@Getter
@NoArgsConstructor
public class AuthUserResponse {
    private Long id;
    private String username;
    private String role;

    @Builder(access = AccessLevel.PRIVATE)
    public AuthUserResponse(Long id, String username, String role) {
        this.id = id;
        this.username = username;
        this.role = role;
    }

    public static AuthUserResponse entityToDto(User user) {
        return AuthUserResponse.builder()
                .id(user.getId())
                .username(user.getUsername())
                .role(String.valueOf(user.getRole()))
                .build();
    }
}