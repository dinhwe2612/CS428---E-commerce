package com.microservice_ecommerce.gateway.config;

import com.microservice_ecommerce.gateway.filter.AuthenticationFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.cloud.gateway.filter.factory.SpringCloudCircuitBreakerFilterFactory;
import org.springframework.cloud.gateway.filter.factory.SpringCloudCircuitBreakerResilience4JFilterFactory;
import org.springframework.http.HttpMethod;

@Configuration
public class GatewayConfig {

    @Autowired
    private AuthenticationFilter authFilter;

    @Bean
    public RouteLocator routes(RouteLocatorBuilder builder) {
        return builder.routes()
                .route("auth_signin", r -> r.path("/api/v1/auth/signin")
                        .uri("lb://auth-service"))
                .route("auth_signup", r -> r.path("/api/v1/auth/signup")
                        .uri("lb://auth-service"))
                .route("auth_forgot_password", r -> r.path("/api/v1/auth/forgot-password")
                        .uri("lb://auth-service"))
                .route("auth_reset_password", r -> r.path("/api/v1/auth/reset-password")
                        .uri("lb://auth-service"))
                .route("swagger_resources", r -> r.path("/v3/api-docs/**")
                        .uri("lb://gateway"))
                .route("swagger_ui", r -> r.path("/swagger-ui/**")
                        .uri("lb://gateway"))
                .route("auth_protected", r -> r.path("/api/v1/auth/**")
                        .filters(f -> f.filter(authFilter.apply(new AuthenticationFilter.Config()))
                                    .circuitBreaker(c -> c.setName("authCircuitBreaker")
                                                      .setFallbackUri("forward:/fallback/auth")))
                        .uri("lb://auth-service"))
                .route("user_service", r -> r.path("/api/v1/users/**")
                        .filters(f -> f.filter(authFilter.apply(new AuthenticationFilter.Config()))
                                    .circuitBreaker(c -> c.setName("userCircuitBreaker")
                                                      .setFallbackUri("forward:/fallback/user")))
                        .uri("lb://user-service"))
                .route("product_public_get", r -> r.path("/api/v1/products/**")
                        .and().method(HttpMethod.GET)
                        .uri("lb://product-service"))
                .route("product_admin", r -> r.path("/api/v1/products/**")
                        .and().method(HttpMethod.POST, HttpMethod.PUT, HttpMethod.DELETE)
                        .filters(f -> f.filter(authFilter.apply(new AuthenticationFilter.Config()))
                                    .circuitBreaker(c -> c.setName("productCircuitBreaker")
                                                      .setFallbackUri("forward:/fallback/product")))
                        .uri("lb://product-service"))
                .build();
    }
} 