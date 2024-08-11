package com.sparta.msa_exam.gateway;

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
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

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

        AuthenticationDetails authDetails = getAuthDetails(token);

        return authenticateUser(authDetails)
                .flatMap(userExists -> modifyRequest(authDetails, exchange, chain))
                .onErrorResume(e -> handleAuthenticationError(exchange));
    }

    private String extractToken(ServerWebExchange exchange) {
        String authHeader = exchange.getRequest().getHeaders().getFirst(AUTHORIZATION);
        if (authHeader != null && authHeader.startsWith(PREFIX)) {
            return authHeader.substring(PREFIX.length());
        }
        return null;
    }

    private AuthenticationDetails getAuthDetails(String token) {
        Jwt jwt = jwtDecoder.decode(token);

        String userId = jwt.getSubject();
        String role = jwt.getClaim(SCOPE).toString();

        return new AuthenticationDetails(userId, role);
    }

    private Mono<Boolean> authenticateUser(AuthenticationDetails authDetails) {

        return webClient.post()
                .uri("/auth/users/validate")
                .bodyValue(authDetails)
                .retrieve()
                .bodyToMono(Boolean.class);
    }

    private Mono<Void> modifyRequest(AuthenticationDetails authDetails, ServerWebExchange exchange, GatewayFilterChain chain) {

        ServerHttpRequest modifiedRequest = exchange.getRequest().mutate()
                .header("X-User-Id", authDetails.getUserId())
                .header("X-User-Role", authDetails.getRole())
                .build();
        return chain.filter(exchange.mutate().request(modifiedRequest).build());
    }

    private Mono<Void> handleAuthenticationError(ServerWebExchange exchange) {
        exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
        return exchange.getResponse().setComplete();
    }
}
