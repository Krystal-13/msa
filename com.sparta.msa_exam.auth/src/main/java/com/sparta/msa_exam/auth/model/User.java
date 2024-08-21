package com.sparta.msa_exam.auth.model;

import com.sparta.msa_exam.auth.type.UserRole;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@Entity(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String username;
    private String password;
    @Enumerated(EnumType.STRING)
    private UserRole role;

    @Builder(access = AccessLevel.PRIVATE)
    public User(String username, String password, UserRole role) {
        this.username = username;
        this.password = password;
        this.role = role;
    }

    public static User createUser(String username, String encodedPassword) {
        return User.builder()
                .username(username)
                .password(encodedPassword)
                .role(UserRole.ROLE_USER)
                .build();
    }
}
