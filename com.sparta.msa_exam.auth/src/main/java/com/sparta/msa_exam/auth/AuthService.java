package com.sparta.msa_exam.auth;


import com.sparta.msa_exam.auth.domain.User;
import com.sparta.msa_exam.auth.dto.AuthUserInfoResponse;
import com.sparta.msa_exam.auth.dto.AuthUserResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.Instant;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AuthService {

    @Value("${spring.application.name}")
    private String issuer;

    @Value("${jwt.access-expiration}")
    private Long accessExpiration;

    private final JwtEncoder encoder;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final CustomAuthenticationProvider customAuthenticationProvider;

    public String signIn(String username, String password) {

        Authentication authToken =
                new UsernamePasswordAuthenticationToken(username, password);

        Authentication authenticate =
                customAuthenticationProvider.authenticate(authToken);

        return createAccessToken(authenticate);
    }

    public AuthUserResponse signUp(String username, String password) {

        if (userRepository.existsByUsername(username)) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST, "Email address is already in use");
        }

        User user = User.createUser(username, passwordEncoder.encode(password));
        User savedUser = userRepository.save(user);

        return AuthUserResponse.entityToDto(savedUser);
    }

    public String createAccessToken(Authentication authentication) {

        Instant now = Instant.now();

        String scope = authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.joining(" "));

        JwtClaimsSet claims = JwtClaimsSet.builder()
                .issuer(issuer)
                .issuedAt(now)
                .expiresAt(now.plusSeconds(accessExpiration))
                .subject(authentication.getName())
                .claim("scope", scope)
                .build();

        return this.encoder.encode(JwtEncoderParameters.from(claims)).getTokenValue();
    }

    public AuthUserInfoResponse validateUserWithAuthority(String userId, String authority) {

        User user = userRepository.findById(Long.valueOf(userId))
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "User Not Found"));

        if (!user.getRole().getAuthority().equals(authority)) {
            throw new ResponseStatusException(
                    HttpStatus.UNAUTHORIZED, "Unauthorized User");
        }

        return AuthUserInfoResponse.EntityToDto(user);
    }
}