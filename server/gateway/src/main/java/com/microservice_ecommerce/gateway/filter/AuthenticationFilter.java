package com.microservice_ecommerce.gateway.filter;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.microservice_ecommerce.gateway.util.JwtUtil;
import io.jsonwebtoken.Claims;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Component
@Slf4j
public class AuthenticationFilter extends AbstractGatewayFilterFactory<AuthenticationFilter.Config> {

    private final JwtUtil jwtUtil;
    private final Cache<String, Claims> tokenCache;
    private final List<String> openApiEndpoints = Arrays.asList(
            "/api/v1/auth/signin",
            "/api/v1/auth/signup",
            "/api/v1/auth/forgot-password",
            "/api/v1/auth/reset-password",
            "/api/v1/images/upload",
            "/api/v1/images/get"
    );

    public AuthenticationFilter(JwtUtil jwtUtil) {
        super(Config.class);
        this.jwtUtil = jwtUtil;
        this.tokenCache = Caffeine.newBuilder()
                .expireAfterWrite(5, TimeUnit.MINUTES)
                .maximumSize(10_000)
                .build();
    }

    @Override
    public GatewayFilter apply(Config config) {
        log.info("Authentication filter is enabled: {}", config.isSecured);
        return (exchange, chain) -> {
            ServerHttpRequest request = exchange.getRequest();
            String path = request.getPath().toString();
            System.out.println("Path1: " + path);
            if (isOpenEndpoint(path)) {
                return chain.filter(exchange);
            }
            System.out.println("Path2: " + path);
            if (config.isSecured) {
                if (!request.getHeaders().containsKey("Authorization")) {
                    return onError(exchange, "No Authorization header", HttpStatus.UNAUTHORIZED);
                }

                String authHeader = request.getHeaders().getFirst("Authorization");
                if (authHeader == null || !authHeader.startsWith("Bearer ")) {
                    return onError(exchange, "Invalid Authorization header", HttpStatus.UNAUTHORIZED);
                }

                String token = authHeader.substring(7);
                try {
                    Claims claims = tokenCache.getIfPresent(token);
                    if (claims == null) {
                        if (!jwtUtil.validateToken(token)) {
                            return onError(exchange, "Token is not valid", HttpStatus.UNAUTHORIZED);
                        }
                        claims = jwtUtil.extractAllClaims(token);
                        tokenCache.put(token, claims);
                    }

                    String userId = claims.get("userId", String.class);
                    String role = claims.get("role", String.class);
                    String username = claims.get("username", String.class);

                    System.out.println("User ID: " + userId + ", Role: " + role + ", Username: " + username);
                    
                    if (userId == null) {
                        return onError(exchange, "User ID not found in token", HttpStatus.UNAUTHORIZED);
                    }

                    ServerHttpRequest modifiedRequest = request.mutate()
                            .header("X-User-ID", userId)
                            .header("X-User-Role", role != null ? role : "")
                            .header("X-User-Name", username != null ? username : "")
                            .build();

                    return chain.filter(exchange.mutate().request(modifiedRequest).build());
                } catch (Exception e) {
                    return onError(exchange, "Token validation failed: " + e.getMessage(), HttpStatus.UNAUTHORIZED);
                }
            }
            
            return chain.filter(exchange);
        };
    }

    private boolean isOpenEndpoint(String path) {
        return openApiEndpoints.stream().anyMatch(path::startsWith);
    }

    private Mono<Void> onError(ServerWebExchange exchange, String message, HttpStatus status) {
        ServerHttpResponse response = exchange.getResponse();
        response.setStatusCode(status);
        return response.setComplete();
    }

    public static class Config {
        private boolean isSecured = true;

        public boolean isSecured() {
            return isSecured;
        }

        public void setSecured(boolean isSecured) {
            this.isSecured = isSecured;
        }
    }
} 