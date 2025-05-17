package com.microservice_ecommerce.integration.product.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

@Configuration
public class ProductServiceConfig {
    
    @Value("${services.product.base-url}")
    private String baseUrl;
    
    @Value("${services.product.timeout:5000}")
    private int timeout;
    
    public String getBaseUrl() {
        return baseUrl;
    }
    
    public int getTimeout() {
        return timeout;
    }
    
    @Bean
    public RestTemplate productRestTemplate() {
        return new RestTemplate();
    }
} 