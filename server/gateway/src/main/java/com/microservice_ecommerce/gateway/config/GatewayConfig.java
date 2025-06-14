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
                        .route("auth_docs", r -> r.path("/v3/api-docs/auth")
                                .uri("lb://auth-service"))
                        .route("image_service_all", r -> r.path("/api/v1/images/**")
                                 .uri("lb://image-service"))
                        .route("image_docs", r -> r.path("/v3/api-docs/images")
                                .uri("lb://image-service"))
                        .route("catalog_service_all", r -> r.path("/api/v1/catalog/**")
                                .filters(f -> f.stripPrefix(3))
                                .uri("lb://catalog-service"))
                        .route("cart_service_all", r -> r.path("/api/v1/cart/**")
                                .filters(f -> f.filter(authFilter.apply(new AuthenticationFilter.Config())))
                                .uri("lb://cart-service"))
                        .route("voucher_service_all", r -> r.path("/api/v1/vouchers/**")
                                .filters(f -> f.filter(authFilter.apply(new AuthenticationFilter.Config())))
                                .uri("lb://voucher-service"))
                        .route("order_service_all", r -> r.path("/api/v1/orders/**")
                                .filters(f -> f.filter(authFilter.apply(new AuthenticationFilter.Config())))
                                .uri("lb://order-service"))
                        .route("report_service_all", r -> r.path("/api/v1/reports/**")
                                .filters(f -> f.filter(authFilter.apply(new AuthenticationFilter.Config())))
                                .uri("lb://catalog-service"))
                        .route("chatbot_service_all", r -> r.path("/api/v1/chatbot/**")
                                .filters(f -> f.stripPrefix(3))
                                .uri("lb://CHATBOT-SERVICE"))
                        .route("notification_ws", r -> r
                                .order(-1)
                                .path("/api/v1/notification/ws/**")
                                .and().header("Upgrade", "websocket")
                                .filters(f -> f
                                        .stripPrefix(3)
                                )
                                .uri("lb:ws://notification-service"))
                        .route("notification_service_all", r -> r.path("/api/v1/notification/**")
                                .filters(f -> f.filter(authFilter.apply(new AuthenticationFilter.Config())))
                                .uri("lb://notification-service"))
                        .route("recommend_service_all", r -> r.path("/api/v1/recommend/**")
                                .filters(f -> f
                                        .stripPrefix(3)
                                        .filter(authFilter.apply(new AuthenticationFilter.Config())))
                                .uri("lb://RECOMMEND-SERVICE")
                        )
                        .build();
        }

}
