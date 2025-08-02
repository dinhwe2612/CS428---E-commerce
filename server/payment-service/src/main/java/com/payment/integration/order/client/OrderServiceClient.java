package com.payment.integration.order.client;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import com.payment.integration.order.config.OrderServiceConfig;
import com.payment.integration.order.constants.OrderEndpoints;
import com.payment.integration.order.dto.OrderResponseDTO;
import com.payment.integration.order.dto.GuestOrderResponseDTO;

@Component
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
    
    public OrderResponseDTO getOrderById(Long orderId) {
        String url = buildUrl(OrderEndpoints.getOrderById(orderId));
        System.out.println("Calling URL: " + url);
        System.out.println("Using API Key: " + internalApiKey);
        try {
            HttpEntity<Void> requestEntity = new HttpEntity<>(createHeaders());
            return restTemplate.exchange(
                url, 
                HttpMethod.GET, 
                requestEntity, 
                OrderResponseDTO.class
            ).getBody();
        } catch (Exception e) {
            System.out.println("Error details: " + e.getMessage());
            throw new RuntimeException("Error connecting to order service: " + e.getMessage(), e);
        }
    }
    
    public GuestOrderResponseDTO getGuestOrderById(Long orderId) {
        String url = buildUrl(OrderEndpoints.getGuestOrderById(orderId));
        System.out.println("Calling Guest Order URL: " + url);
        System.out.println("Using API Key: " + internalApiKey);
        System.out.println("Base URL: " + config.getBaseUrl());
        try {
            HttpEntity<Void> requestEntity = new HttpEntity<>(createHeaders());
            System.out.println("Request headers: " + createHeaders());
            GuestOrderResponseDTO response = restTemplate.exchange(
                url, 
                HttpMethod.GET, 
                requestEntity, 
                GuestOrderResponseDTO.class
            ).getBody();
            System.out.println("Guest Order response: " + response);
            return response;
        } catch (Exception e) {
            System.out.println("Error calling guest order service: " + e.getClass().getSimpleName());
            System.out.println("Error message: " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("Error connecting to order service for guest order: " + e.getMessage(), e);
        }
    }
} 