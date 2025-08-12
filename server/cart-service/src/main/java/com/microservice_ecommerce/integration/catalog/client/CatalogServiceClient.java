package com.microservice_ecommerce.integration.catalog.client;

import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import com.microservice_ecommerce.integration.catalog.config.CatalogServiceConfig;
import com.microservice_ecommerce.integration.catalog.constants.CatalogEndpoints;
import com.microservice_ecommerce.integration.catalog.dto.response.ProductResponse;
import com.microservice_ecommerce.integration.catalog.dto.response.ProductResponseSnakeCase;


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
    
    public List<ProductResponse> getAllProducts() {
        String url = buildUrl(CatalogEndpoints.getAllProducts());
        System.out.println("URL: " + url);
        try {
            HttpEntity<Void> requestEntity = new HttpEntity<>(createHeaders());
            return restTemplate.exchange(
                url,
                HttpMethod.GET,
                requestEntity,
                new ParameterizedTypeReference<List<ProductResponse>>() {}
            ).getBody();
        } catch (Exception e) {
            System.err.println("Error fetching products from catalog service: " + e.getMessage());
            return List.of();
        }
    }

    public List<ProductResponseSnakeCase> getAllProductsSnakeCase() {
        String url = buildUrl(CatalogEndpoints.getAllProducts());
        System.out.println("URL: " + url);
        try {
            HttpEntity<Void> requestEntity = new HttpEntity<>(createHeaders());
            return restTemplate.exchange(
                url,
                HttpMethod.GET,
                requestEntity,
                new ParameterizedTypeReference<List<ProductResponseSnakeCase>>() {}
            ).getBody();
        } catch (Exception e) {
            System.err.println("Error fetching products from catalog service: " + e.getMessage());
            return List.of();
        }
    }
    
} 