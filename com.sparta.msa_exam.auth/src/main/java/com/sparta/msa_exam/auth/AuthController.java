package com.sparta.msa_exam.auth;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/auth")
public class AuthController {

    private final AuthService authService;

    @PostMapping("/signIn")
    public ResponseEntity<AuthResponse> signIn(@RequestBody SignInRequest signInRequest) {
        String token = authService.signIn(signInRequest.getUsername(), signInRequest.getPassword());
        return ResponseEntity.ok(new AuthResponse(token));
    }


    @PostMapping("/signUp")
    public ResponseEntity<UserResponseDto> signUp(@RequestBody SignUpRequest signUpRequest) {
        UserResponseDto userResponseDto = authService.signUp(
                signUpRequest.getUsername(), signUpRequest.getPassword());
        return ResponseEntity.ok(userResponseDto);
    }

    @Getter
    @AllArgsConstructor
    static class AuthResponse {
        private String access_token;
    }

    @Getter
    static class SignInRequest {
        private String username;
        private String password;
    }

    @Getter
    static class SignUpRequest {
        private String username;
        private String password;
    }
}
