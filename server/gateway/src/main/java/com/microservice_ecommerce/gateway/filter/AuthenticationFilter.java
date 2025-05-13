package com.microservice_ecommerce.gateway.filter;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.microservice_ecommerce.gateway.util.JwtUtil;
import io.jsonwebtoken.Claims;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.concurrent.TimeUnit;

@Component
public class AuthenticationFilter extends AbstractGatewayFilterFactory<AuthenticationFilter.Config> {

    private final JwtUtil jwtUtil;
    private final Cache<String, Claims> tokenCache;

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
        return (exchange, chain) -> {
            ServerHttpRequest request = exchange.getRequest();

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
                    
                    if (userId == null) {
                        return onError(exchange, "User ID not found in token", HttpStatus.UNAUTHORIZED);
                    }

                    ServerHttpRequest modifiedRequest = request.mutate()
                            .header("X-User-ID", userId)
                            .header("X-User-Role", role != null ? role : "")
                            .build();

                    return chain.filter(exchange.mutate().request(modifiedRequest).build());
                } catch (Exception e) {
                    return onError(exchange, "Token validation failed: " + e.getMessage(), HttpStatus.UNAUTHORIZED);
                }
            }
            
            return chain.filter(exchange);
        };
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