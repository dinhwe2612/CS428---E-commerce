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
                        // auth
                        .route("auth_service_all", r -> r.path("/api/v1/auth/**")
                                .uri("lb://auth-service"))
                        .route("auth_docs", r -> r.path("/v3/api-docs/auth")
                                .uri("lb://auth-service"))
                        // image
                        .route("image_service_all", r -> r.path("/api/v1/images/**")
                                 .uri("lb://image-service"))
                        .route("image_docs", r -> r
                                .path("/v3/api-docs/images")
                                .uri("lb://image-service"))
                        // catalog
                        .route("catalog_service_all", r -> r.path("/api/v1/catalog/**")
                                .filters(f -> f.stripPrefix(3))
                                .uri("lb://catalog-service"))
                        .route("catalog_docs", r -> r
                                .path("/v3/api-docs/catalog")
                                .uri("lb://catalog-service"))
                        // cart
                        .route("cart_service_all", r -> r.path("/api/v1/cart/**")
                                .filters(f -> f.filter(authFilter.apply(new AuthenticationFilter.Config())))
                                .uri("lb://cart-service"))
                        .route("cart_docs", r -> r
                                .path("/v3/api-docs/cart")
                                .uri("lb://cart-service"))
                        // voucher
                        .route("voucher_service_all", r -> r.path("/api/v1/vouchers/**")
                                .filters(f -> f.filter(authFilter.apply(new AuthenticationFilter.Config())))
                                .uri("lb://voucher-service"))
                        // order
                        .route("order_service_all", r -> r.path("/api/v1/orders/**")
                                .filters(f -> f.filter(authFilter.apply(new AuthenticationFilter.Config())))
                                .uri("lb://order-service"))
                        .route("guest_order_service_all", r -> r.path("/api/v1/guest-orders/**")
                                .uri("lb://order-service"))
                        .route("order_docs", r -> r
                                .path("/v3/api-docs/order")
                                .filters(f -> f.rewritePath("/v3/api-docs/order", "/v3/api-docs/order"))
                                .uri("lb://order-service"))
                        //
                        .route("report_service_all", r -> r.path("/api/v1/reports/**")
                                .filters(f -> f.filter(authFilter.apply(new AuthenticationFilter.Config())))
                                .uri("lb://catalog-service"))
                        // chatbot
                        .route("chatbot_service_all", r -> r.path("/api/v1/chatbot/**")
                                .filters(f -> f.stripPrefix(3))
                                .uri("lb://CHATBOT-SERVICE"))
                        // notification
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
                        .route("notification_docs", r -> r
                                .path("/v3/api-docs/notification")
                                .filters(f -> f.rewritePath("/v3/api-docs/notification", "/v3/api-docs/notification"))
                                .uri("lb://notification-service"))
                        // recommend
                        .route("recommend_service_all", r -> r.path("/api/v1/recommend/**")
                                .filters(f -> f
                                        .stripPrefix(3)
                                        .filter(authFilter.apply(new AuthenticationFilter.Config())))
                                .uri("lb://RECOMMEND-SERVICE")
                        )
                        // payment
                        .route("guest_payment", r -> r.path("/api/v1/payments/guest")
                                .uri("lb://payment-service"))
                        .route("payment_service_all", r -> r.path("/api/v1/payments", "/api/v1/payments/**")
                                .filters(f -> f
                                        .filter(authFilter.apply(new AuthenticationFilter.Config())))
                                .uri("lb://PAYMENT-SERVICE")
                        )
                        .route("payment_docs", r -> r
                                .path("/v3/api-docs/payment")
                                .uri("lb://payment-service"))
                        // user
                        .route("user_service_all", r -> r.path("/api/v1/users/**")
                                .filters(f -> f
                                        .filter(authFilter.apply(new AuthenticationFilter.Config())))
                                .uri("lb://USER-SERVICE")
                        )
                        .route("user_docs", r -> r
                                .path("/v3/api-docs/user")
                                .uri("lb://user-service"))
                        .build();
        }

}
