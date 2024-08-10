package com.sparta.msa_exam.auth;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;


@Getter
@NoArgsConstructor
public class UserResponseDto {
    private Long id;
    private String username;
    private String role;

    @Builder(access = AccessLevel.PRIVATE)
    public UserResponseDto(Long id, String username, String role) {
        this.id = id;
        this.username = username;
        this.role = role;
    }

    public static UserResponseDto entityToDto(User user) {
        return UserResponseDto.builder()
                .id(user.getId())
                .username(user.getUsername())
                .role(user.getRole())
                .build();
    }
}

