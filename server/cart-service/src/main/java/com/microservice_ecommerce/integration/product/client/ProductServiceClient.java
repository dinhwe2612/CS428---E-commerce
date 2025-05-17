package com.microservice_ecommerce.integration.product.client;

import java.util.UUID;

import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import com.microservice_ecommerce.integration.product.config.ProductServiceConfig;
import com.microservice_ecommerce.integration.product.dto.response.ProductResponse;
import com.microservice_ecommerce.integration.product.constants.ProductEndpoints;
@Component
public class ProductServiceClient {
    
    private final RestTemplate restTemplate;
    private final ProductServiceConfig config;
    
    public ProductServiceClient(RestTemplate restTemplate, ProductServiceConfig config) {
        this.restTemplate = restTemplate;
        this.config = config;
    }
    
    public ProductResponse detailProduct(UUID productId) {
        String url = config.getBaseUrl() + ProductEndpoints.getProductDetail(productId.toString());
        return restTemplate.getForObject(url, ProductResponse.class);
    }
} 