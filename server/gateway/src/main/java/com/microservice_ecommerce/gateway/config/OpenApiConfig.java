package com.microservice_ecommerce.gateway.config;

import org.springdoc.core.models.GroupedOpenApi;
import org.springdoc.core.properties.SwaggerUiConfigParameters;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.cloud.gateway.route.RouteDefinition;
import org.springframework.cloud.gateway.route.RouteDefinitionLocator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;

import java.util.ArrayList;
import java.util.List;

@Configuration
public class OpenApiConfig {

    @Bean
    @Lazy(false)
    public List<GroupedOpenApi> apis(
            SwaggerUiConfigParameters swaggerUiConfigParameters,
            RouteDefinitionLocator routeDefinitionLocator,
            DiscoveryClient discoveryClient) {
        
        List<RouteDefinition> definitions = routeDefinitionLocator.getRouteDefinitions()
                .collectList().block();
        
        for (String serviceId : discoveryClient.getServices()) {
            serviceId = serviceId.toLowerCase();
            swaggerUiConfigParameters.addGroup(serviceId);
        }
        
        if (definitions != null) {
            definitions.stream()
                    .filter(routeDefinition -> routeDefinition.getId() != null)
                    .forEach(routeDefinition -> {
                        String id = routeDefinition.getId().toLowerCase();
                        if (!id.startsWith("reactivecommons") && !id.contains("openapi")) {
                            if (id.contains("_")) {
                                 String[] parts = id.split("_");
                                if (parts.length > 0) {
                                    swaggerUiConfigParameters.addGroup(parts[0]);
                                }
                            } else {
                                swaggerUiConfigParameters.addGroup(id);
                            }
                        }
                    });
        }
        
        return new ArrayList<>();
    }
} 