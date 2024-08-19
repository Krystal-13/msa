package com.sparta.msa_exam.auth;

import com.sparta.msa_exam.auth.dto.AuthUserInfo;
import com.sparta.msa_exam.auth.domain.UserRole;
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

    private AuthUserInfo authUserInfo;

    public CustomUserDetails(AuthUserInfo authUserInfo) {
        this.authUserInfo = authUserInfo;
    }

    private GrantedAuthority getAuthority(UserRole role) {
        return new SimpleGrantedAuthority(role.getAuthority());
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {

        return List.of(getAuthority(authUserInfo.getRole()));
    }

    @Override
    public String getPassword() {
        return authUserInfo.getPassword();
    }

    @Override
    public String getUsername() {
        return authUserInfo.getUsername();
    }
}
