package com.sparta.msa_exam.order;

import feign.RequestInterceptor;
import feign.RequestTemplate;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

@Configuration
public class FeignClientConfig {
    @Bean
    public RequestInterceptor requestTokenBearerInterceptor() {
        return new RequestInterceptor() {
            @Override
            public void apply(RequestTemplate template) {
                Authentication authentication =
                        SecurityContextHolder.getContext().getAuthentication();
                if (authentication != null) {
                    template.header("X-User-Id", authentication.getName());
                    template.header("X-User-Role", authentication.getAuthorities().toString());
                }
            }
        };
    }
}
