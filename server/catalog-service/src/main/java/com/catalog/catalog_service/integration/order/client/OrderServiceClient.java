package com.catalog.catalog_service.integration.order.client;

import com.catalog.catalog_service.integration.order.config.OrderServiceConfig;
import com.catalog.catalog_service.integration.order.constants.OrderEndpoints;
import com.catalog.catalog_service.integration.order.dto.OrderResponseDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.List;

@Component
@RequiredArgsConstructor
public class OrderServiceClient {
    
    private static final String INTERNAL_API_KEY_HEADER = "X-Internal-Api-Key";
    
    private final RestTemplate restTemplate;
    private final OrderServiceConfig config;
    
    @Value("${INTERNAL_API_KEY}")
    private String internalApiKey;
    
    protected String buildUrl(String path) {
        return config.getBaseUrl() + path;
    }
    
    protected HttpHeaders createHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.set(INTERNAL_API_KEY_HEADER, internalApiKey);
        return headers;
    }
    
    public OrderResponseDTO getOrderById(Long orderId) {
        String url = buildUrl(OrderEndpoints.getOrderById(orderId));
        try {
            HttpEntity<Void> requestEntity = new HttpEntity<>(createHeaders());
            return restTemplate.exchange(
                url, 
                HttpMethod.GET, 
                requestEntity, 
                OrderResponseDTO.class
            ).getBody();
        } catch (Exception e) {
            throw new RuntimeException("Error connecting to order service: " + e.getMessage(), e);
        }
    }
    
    public List<OrderResponseDTO> getOrdersByStatus(String status) {
        String url = buildUrl(OrderEndpoints.getOrdersByStatus(status));
        try {
            HttpEntity<Void> requestEntity = new HttpEntity<>(createHeaders());
            ParameterizedTypeReference<List<OrderResponseDTO>> responseType = 
                new ParameterizedTypeReference<List<OrderResponseDTO>>() {};
            
            return restTemplate.exchange(
                url,
                HttpMethod.GET,
                requestEntity,
                responseType
            ).getBody();
        } catch (Exception e) {
            throw new RuntimeException("Error connecting to order service: " + e.getMessage(), e);
        }
    }
    
    public List<OrderResponseDTO> getAllOrders() {
        String url = buildUrl(OrderEndpoints.getAllOrders());
        try {
            HttpEntity<Void> requestEntity = new HttpEntity<>(createHeaders());
            ParameterizedTypeReference<List<OrderResponseDTO>> responseType = 
                new ParameterizedTypeReference<List<OrderResponseDTO>>() {};
            
            return restTemplate.exchange(
                url,
                HttpMethod.GET,
                requestEntity,
                responseType
            ).getBody();
        } catch (Exception e) {
            throw new RuntimeException("Error connecting to order service: " + e.getMessage(), e);
        }
    }
    
    public List<OrderResponseDTO> getOrdersByUserId(String userId) {
        String url = buildUrl(OrderEndpoints.getOrdersByUserId(userId));
        try {
            HttpEntity<Void> requestEntity = new HttpEntity<>(createHeaders());
            ParameterizedTypeReference<List<OrderResponseDTO>> responseType = 
                new ParameterizedTypeReference<List<OrderResponseDTO>>() {};
            
            return restTemplate.exchange(
                url,
                HttpMethod.GET,
                requestEntity,
                responseType
            ).getBody();
        } catch (Exception e) {
            throw new RuntimeException("Error connecting to order service: " + e.getMessage(), e);
        }
    }
} 