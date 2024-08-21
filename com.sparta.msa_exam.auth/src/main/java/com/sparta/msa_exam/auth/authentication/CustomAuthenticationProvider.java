package com.sparta.msa_exam.auth.authentication;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;

import java.util.Optional;

@Component
@RequiredArgsConstructor
public class CustomAuthenticationProvider implements AuthenticationProvider {

    private final CustomUserDetailsService customUserDetailsService;
    private final PasswordEncoder passwordEncoder;

    @Override
    public Authentication authenticate(
            Authentication authToken) throws AuthenticationException {

        String username = authToken.getName();
        Optional.ofNullable(username).orElseThrow(
                () -> new UsernameNotFoundException(
                        "Invalid User name or User Password"));
        try {
            UserDetails userDetails =
                    customUserDetailsService.loadUserByUsername(username);
            String password = userDetails.getPassword();
            verifyCredentials(authToken.getCredentials(), password);

            UsernamePasswordAuthenticationToken authenticated =
                    new UsernamePasswordAuthenticationToken(
                    userDetails.getUsername(), password, userDetails.getAuthorities()
            );

            authenticated.eraseCredentials();

            return authenticated;
        } catch (Exception ex) {
            throw new UsernameNotFoundException(ex.getMessage());
        }
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return UsernamePasswordAuthenticationToken.class.equals(authentication);
    }

    private void verifyCredentials(Object credentials, String password) {
        if (!passwordEncoder.matches((String)credentials, password)) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST, "Invalid User name or User Password");
        }
    }
}
