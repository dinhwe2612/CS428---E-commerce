package com.microservice_ecommerce.gateway.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.microservice_ecommerce.gateway.filter.AuthenticationFilter;

@Configuration
public class GatewayConfig {

        @Autowired
        private AuthenticationFilter authFilter;

        @Bean
        public RouteLocator manualRoutes(RouteLocatorBuilder builder) {
                return builder.routes()
                                .route("auth_service_all", r -> r.path("/api/v1/auth/**")
                                                .uri("lb://auth-service"))
                                .route("image_service_all", r -> r.path("/api/v1/images/**")
                                                 .uri("lb://image-service"))
                                .route("catalog_service_all", r -> r.path("/api/v1/catalog/**")
                                                .filters(f -> f.stripPrefix(3))
                                                .uri("lb://catalog-service"))
                                .route("cart_service_all", r -> r.path("/api/v1/cart/**")
                                                .filters(f -> f.filter(authFilter.apply(new AuthenticationFilter.Config())))
                                                .uri("lb://cart-service"))
                                .route("order_service_all", r -> r.path("/api/v1/orders/**")
                                                .filters(f -> f.filter(authFilter.apply(new AuthenticationFilter.Config())))
                                                .uri("lb://order-service"))
                                .route("notification_service_all", r -> r.path("/api/v1/notification/**")
                                                .filters(f -> f.filter(authFilter.apply(new AuthenticationFilter.Config())))
                                                .uri("lb://notification-service"))
                                .build();
        }

}
