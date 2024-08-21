package com.sparta.msa_exam.auth.model;

import com.sparta.msa_exam.auth.type.UserRole;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;

@Getter
@NoArgsConstructor
public class CustomUserDetails implements UserDetails {

    private String username;
    private String password;
    private UserRole role;

    @Builder
    public CustomUserDetails(String username, String password, UserRole role) {
        this.username = username;
        this.password = password;
        this.role = role;
    }

    private GrantedAuthority getAuthority(UserRole role) {
        return new SimpleGrantedAuthority(role.getAuthority());
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {

        return List.of(getAuthority(role));
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return username;
    }
}
