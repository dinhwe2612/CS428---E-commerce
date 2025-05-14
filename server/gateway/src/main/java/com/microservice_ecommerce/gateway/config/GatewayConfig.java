package com.microservice_ecommerce.gateway.config;

import com.microservice_ecommerce.gateway.filter.AuthenticationFilter;
import org.springframework.beans.factory.annotation.Autowired;
<<<<<<< Updated upstream
=======
import org.springframework.cloud.client.discovery.DiscoveryClient;
>>>>>>> Stashed changes
import org.springframework.cloud.client.discovery.ReactiveDiscoveryClient;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.cloud.gateway.filter.factory.SpringCloudCircuitBreakerFilterFactory;
import org.springframework.cloud.gateway.filter.factory.SpringCloudCircuitBreakerResilience4JFilterFactory;
import org.springframework.http.HttpMethod;
import org.springframework.cloud.gateway.discovery.DiscoveryClientRouteDefinitionLocator;
import org.springframework.cloud.gateway.discovery.DiscoveryLocatorProperties;

@Configuration
public class GatewayConfig {

        @Autowired
        private AuthenticationFilter authFilter;

<<<<<<< Updated upstream
    @Bean
    public RouteLocator manualRoutes(RouteLocatorBuilder builder) {
        return builder.routes()
                .route("auth_service_all", r -> r.path("/api/v1/auth/**")
                        .uri("lb://auth-service"))
                .route("image_service_all", r -> r.path("/api/v1/images/**")
                        .uri("lb://image-service"))
                .build();
    }
    
    @Bean
    public DiscoveryClientRouteDefinitionLocator discoveryRoutes(
            ReactiveDiscoveryClient discoveryClient,
            DiscoveryLocatorProperties properties) {
        return new DiscoveryClientRouteDefinitionLocator(discoveryClient, properties);
    }
} 
=======
        @Bean
        public RouteLocator manualRoutes(RouteLocatorBuilder builder) {
                return builder.routes()
                                .route("auth_service_all", r -> r.path("/api/v1/auth/**")
                                                .uri("lb://auth-service"))
                                .route("image_service_all", r -> r.path("/api/images/**")
                                                .uri("lb://image-service"))
                                .build();
        }

}
>>>>>>> Stashed changes
