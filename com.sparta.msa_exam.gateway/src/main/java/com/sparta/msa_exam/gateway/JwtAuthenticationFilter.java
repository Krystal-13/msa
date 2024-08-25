package com.sparta.msa_exam.gateway;

import com.sparta.msa_exam.gateway.dto.AuthUserInfoRequest;
import com.sparta.msa_exam.gateway.dto.AuthUserInfoResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.time.Instant;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter implements GlobalFilter {

    private static final String AUTHORIZATION = "Authorization";
    private static final String PREFIX = "Bearer ";
    private static final String SCOPE = "scope";

    private final JwtDecoder jwtDecoder;
    private final WebClient webClient;

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        String path = exchange.getRequest().getURI().getPath();
        if (path.equals("/auth/signIn") || path.equals("/auth/signUp")) {
            return chain.filter(exchange);
        }

        String token = extractToken(exchange);
        if (token == null) {
            exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
            return exchange.getResponse().setComplete();
        }

        AuthUserInfoRequest authDetails = getAuthDetails(token);

        return authenticateUser(authDetails)
                .flatMap(AuthUserInfoResponse -> modifyRequest(AuthUserInfoResponse, exchange, chain))
                .onErrorResume(e -> handleAuthenticationError(exchange));
    }

    private String extractToken(ServerWebExchange exchange) {
        String authHeader = exchange.getRequest().getHeaders().getFirst(AUTHORIZATION);
        if (authHeader != null && authHeader.startsWith(PREFIX)) {
            return authHeader.substring(PREFIX.length());
        }
        return null;
    }

    private AuthUserInfoRequest getAuthDetails(String token) {
        Jwt jwt = jwtDecoder.decode(token);

        if (jwt.getExpiresAt() == null || jwt.getExpiresAt().isBefore(Instant.now())) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED);
        }

        String userId = jwt.getSubject();
        String authority = jwt.getClaim(SCOPE).toString();

        return new AuthUserInfoRequest(userId, authority);
    }

    private Mono<AuthUserInfoResponse> authenticateUser(AuthUserInfoRequest authDetails) {

        return webClient.post()
                .uri("/auth/users/validate")
                .bodyValue(authDetails)
                .retrieve()
                .bodyToMono(AuthUserInfoResponse.class);
    }

    private Mono<Void> modifyRequest(AuthUserInfoResponse userInfo, ServerWebExchange exchange, GatewayFilterChain chain) {
        ServerHttpRequest modifiedRequest = exchange.getRequest().mutate()
                .header("X-User-Id", userInfo.getUserId())
                .header("X-User-Role", userInfo.getRole())
                .build();
        return chain.filter(exchange.mutate().request(modifiedRequest).build());
    }

    private Mono<Void> handleAuthenticationError(ServerWebExchange exchange) {
        exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
        return exchange.getResponse().setComplete();
    }
}
