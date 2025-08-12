package com.microservice_ecommerce.cart.domain.service;

import com.microservice_ecommerce.cart.domain.dto.RecommendationRequest;
import com.microservice_ecommerce.cart.domain.dto.RecommendationResponse;

public interface RecommendationService {
    
    RecommendationResponse getRecommendations(RecommendationRequest request);
    
    RecommendationResponse getCartBasedRecommendations(Long userId, int limit);
    
    RecommendationResponse getSimilarProducts(Long productId, int limit);
    
    RecommendationResponse getPopularProducts(int limit);
} 