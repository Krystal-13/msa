package com.sparta.msa_exam.auth.controller;

import com.sparta.msa_exam.auth.service.AuthService;
import com.sparta.msa_exam.auth.dto.*;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequiredArgsConstructor
@RequestMapping("/auth")
public class AuthController {

    private final AuthService authService;

    @PostMapping("/signIn")
    public ResponseEntity<AuthResponse> signIn(
            @RequestBody SignInRequest signInRequest
    ) {
        String token = authService.signIn(
                signInRequest.getUsername(), signInRequest.getPassword());
        return ResponseEntity.ok(new AuthResponse(token));
    }


    @PostMapping("/signUp")
    public ResponseEntity<Boolean> signUp(
            @RequestBody SignUpRequest signUpRequest
    ) {
        boolean response = authService.signUp(
                signUpRequest.getUsername(), signUpRequest.getPassword());
        return ResponseEntity.ok(response);
    }

    /**
     * 사용자 검증 API
     * <p>Gateway에서 JWT 토큰이 유효한 사용자 ID와 권한 (예: USER, ADMIN)을 받아서, 사용자 검증 후 사용자 ID와 역할 (예: ROLE_USER, ROLE_SELLER)을 응답<p/>
     * @param details <code>AuthUserInfoRequest<code/> 인증 요청 객체
     * @return <code>AuthUserInfoResponse<code/> 인증 응답 객체
     * @throws ResponseStatusException 401, 404 <br/>
     * 401 Unauthorized: 사용자 검증 실패 (유효하지 않은 JWT 토큰 또는 사용자 권한 불일치)<br/>
     * 404 NotFound: 사용자 검증 실패 (사용자 정보 불일치)
     */
    @PostMapping("/users/validate")
    public ResponseEntity<AuthUserInfoResponse> validateUserWithRole(
            @RequestBody AuthUserInfoRequest details
    ) {
        AuthUserInfoResponse authUserInfoResponse =
                authService.validateUserWithAuthority(
                        details.getUserId(), details.getAuthority()
                );
        return ResponseEntity.ok(authUserInfoResponse);
    }
}
