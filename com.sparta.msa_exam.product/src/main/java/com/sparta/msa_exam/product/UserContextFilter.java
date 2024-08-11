package com.sparta.msa_exam.product;

import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;

import java.io.IOException;
import java.util.Collections;

@Slf4j
public class UserContextFilter implements Filter {

    @Override
    public void doFilter(ServletRequest request,
                         ServletResponse response,
                         FilterChain chain
    ) throws IOException, ServletException {

        HttpServletRequest httpRequest = (HttpServletRequest) request;

        String userId = httpRequest.getHeader("X-User-Id");
        String role = httpRequest.getHeader("X-User-Role");
        SimpleGrantedAuthority authority = new SimpleGrantedAuthority(role);

        UsernamePasswordAuthenticationToken authentication =
                new UsernamePasswordAuthenticationToken(
                        userId,
                        null,
                        Collections.singletonList(authority)
                );

        SecurityContextHolder.getContext().setAuthentication(authentication);

        chain.doFilter(request, response);
    }
}
