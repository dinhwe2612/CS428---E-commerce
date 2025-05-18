package com.microservice_ecommerce.integration.catalog.client;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import com.microservice_ecommerce.integration.catalog.config.CatalogServiceConfig;
import com.microservice_ecommerce.integration.catalog.constants.CatalogEndpoints;
import com.microservice_ecommerce.integration.catalog.dto.response.ProductResponse;


@Component
public class CatalogServiceClient {
    
    private static final String INTERNAL_API_KEY_HEADER = "X-Internal-Api-Key";
    
    private final RestTemplate restTemplate;
    private final CatalogServiceConfig config;
    
    @Value("${INTERNAL_API_KEY}")
    private String internalApiKey;
    
    public CatalogServiceClient(RestTemplate restTemplate, CatalogServiceConfig config) {
        this.restTemplate = restTemplate;
        this.config = config;
    }
    
    protected String buildUrl(String path) {
        return config.getBaseUrl() + path;
    }
    
    protected HttpHeaders createHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.set(INTERNAL_API_KEY_HEADER, internalApiKey);
        return headers;
    }
    
    public ProductResponse getProductDetails(Long productId) {
        String url = buildUrl(CatalogEndpoints.getProductDetails(productId));
        System.out.println("URL: " + url);
        try {
            HttpEntity<Void> requestEntity = new HttpEntity<>(createHeaders());
            return restTemplate.exchange(
                url, 
                HttpMethod.GET, 
                requestEntity, 
                ProductResponse.class
            ).getBody();
        } catch (Exception e) {
            System.err.println("Error connecting to catalog service: " + e.getMessage());
            throw e;
        }
    }
    
} 