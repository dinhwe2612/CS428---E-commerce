package com.microservice_ecommerce.cart.infrastructure.client;

import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import com.microservice_ecommerce.cart.infrastructure.config.OrderServiceConfig;
import com.microservice_ecommerce.cart.infrastructure.constants.OrderEndpoints;
import com.microservice_ecommerce.cart.infrastructure.dto.OrderResponseDTO;

import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class OrderServiceClient {
    
    private static final String INTERNAL_API_KEY_HEADER = "X-Internal-Api-Key";
    
    private final RestTemplate restTemplate;
    private final OrderServiceConfig config;
    
    @Value("${INTERNAL_API_KEY}")
    private String internalApiKey;
    
    public OrderServiceClient(RestTemplate restTemplate, OrderServiceConfig config) {
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
    
    public List<OrderResponseDTO> getOrdersByUserId(String userId) {
        String url = buildUrl(OrderEndpoints.getOrdersByUserId(userId));
        log.info("Calling URL: {}", url);
        try {
            HttpEntity<Void> requestEntity = new HttpEntity<>(createHeaders());
            return restTemplate.exchange(
                url, 
                HttpMethod.GET, 
                requestEntity, 
                new ParameterizedTypeReference<List<OrderResponseDTO>>() {}
            ).getBody();
        } catch (Exception e) {
            log.error("Error fetching orders for user {}: {}", userId, e.getMessage());
            return List.of();
        }
    }
    
    public OrderResponseDTO getOrderById(Long orderId) {
        String url = buildUrl(OrderEndpoints.getOrderById(orderId));
        log.info("Calling URL: {}", url);
        try {
            HttpEntity<Void> requestEntity = new HttpEntity<>(createHeaders());
            return restTemplate.exchange(
                url, 
                HttpMethod.GET, 
                requestEntity, 
                OrderResponseDTO.class
            ).getBody();
        } catch (Exception e) {
            log.error("Error fetching order {}: {}", orderId, e.getMessage());
            return null;
        }
    }
} 