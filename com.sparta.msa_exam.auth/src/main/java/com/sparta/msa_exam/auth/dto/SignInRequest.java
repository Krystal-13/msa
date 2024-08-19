package com.sparta.msa_exam.auth.dto;

import lombok.Getter;

@Getter
public class SignInRequest {
    private String username;
    private String password;
}
