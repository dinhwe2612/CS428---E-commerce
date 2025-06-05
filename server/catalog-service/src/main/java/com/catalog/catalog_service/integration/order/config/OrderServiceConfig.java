package com.catalog.catalog_service.integration.order.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "integration.order")
@Data
public class OrderServiceConfig {
    private String baseUrl = "http://order-service";
} 