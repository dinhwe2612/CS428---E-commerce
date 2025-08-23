package com.catalog.catalog_service.client;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class CartServiceClient {
    
    private static final int MAX_RETRIES = 3;
    private static final long RETRY_DELAY_MS = 1000;

    private final RestTemplate restTemplate;
    
    @Value("${services.cart.base-url:http://cart-service:8088}")
    private String cartServiceBaseUrl;

    @Value("${INTERNAL_API_KEY}")
    private String internalApiKey;

    public CartServiceClient(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public void invalidateProductsCache() {
        for (int attempt = 1; attempt <= MAX_RETRIES; attempt++) {
            try {
                HttpHeaders headers = new HttpHeaders();
                headers.set("X-Internal-Api-Key", internalApiKey);
                HttpEntity<Void> entity = new HttpEntity<>(headers);
                
                restTemplate.exchange(
                    cartServiceBaseUrl + "/api/v1/cart/cache/invalidate/products",
                    HttpMethod.POST,
                    entity,
                    String.class
                );
                log.info("Cart service products cache invalidated successfully");
                return;
            } catch (Exception e) {
                log.warn("Attempt {}/{} failed to invalidate cart service cache: {}", attempt, MAX_RETRIES, e.getMessage());
                if (attempt < MAX_RETRIES) {
                    try {
                        Thread.sleep(RETRY_DELAY_MS * attempt);
                    } catch (InterruptedException ie) {
                        Thread.currentThread().interrupt();
                        return;
                    }
                }
            }
        }
        log.error("Failed to invalidate cart service cache after {} attempts", MAX_RETRIES);
    }
}
