package com.sparta.msa_exam.auth;

import com.sparta.msa_exam.auth.dto.SecretUserDto;
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

    private SecretUserDto secretUserDto;

    public CustomUserDetails(SecretUserDto secretUserDto) {
        this.secretUserDto = secretUserDto;
    }

    private GrantedAuthority getAuthority(UserRole role) {
        return new SimpleGrantedAuthority(role.name());
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        List<GrantedAuthority> authorityList =
                List.of(getAuthority(secretUserDto.getRole()));

        System.out.println(authorityList);

        return authorityList;
    }

    @Override
    public String getPassword() {
        return secretUserDto.getPassword();
    }

    @Override
    public String getUsername() {
        return secretUserDto.getUsername();
    }
}
