package com.order.order_service.integration.catalog.client;

import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import com.order.order_service.DTOs.ProductDTO;
import com.order.order_service.integration.catalog.config.CatalogServiceConfig;
import com.order.order_service.integration.catalog.constants.CatalogEndpoints;
import com.order.order_service.integration.catalog.dto.response.ProductResponse;


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
    public List<ProductDTO> getProductsByIds(List<Long> ids) {
        String url = buildUrl(CatalogEndpoints.getProductsByIds(ids));
        try {
            HttpEntity<Void> requestEntity = new HttpEntity<>(createHeaders());
            return restTemplate.exchange(
                url,
                HttpMethod.GET,
                requestEntity,
                new ParameterizedTypeReference<List<ProductDTO>>() {}
                
               
            ).getBody();
        } catch (Exception e) {
            System.err.println("Error connecting to catalog service: " + e.getMessage());
            throw e;
        }
    }
} 