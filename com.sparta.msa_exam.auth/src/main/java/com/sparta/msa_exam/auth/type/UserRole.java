package com.sparta.msa_exam.auth.type;

import lombok.Getter;

@Getter
public enum UserRole {
    ROLE_GUEST("GUEST"),
    ROLE_USER("USER"),
    ROLE_SELLER("USER"),
    ROLE_ADMIN("ADMIN");

    private String authority;

    UserRole(String authority) {
        this.authority = authority;
    }
}
