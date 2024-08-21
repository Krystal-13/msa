package com.sparta.msa_exam.auth.authentication;

import com.sparta.msa_exam.auth.model.CustomUserDetails;
import com.sparta.msa_exam.auth.model.User;
import com.sparta.msa_exam.auth.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@RequiredArgsConstructor
@Service
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username)
            throws UsernameNotFoundException {

        User user = userRepository.findByUsername(username)
                .orElseThrow(
                        () -> new ResponseStatusException(
                                HttpStatus.NOT_FOUND, "User Not Found"
                        )
                );

        return CustomUserDetails.builder()
                .username(String.valueOf(user.getId()))
                .password(user.getPassword())
                .role(user.getRole())
                .build();
    }
}