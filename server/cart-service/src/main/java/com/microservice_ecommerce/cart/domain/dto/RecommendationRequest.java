package com.microservice_ecommerce.cart.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RecommendationRequest {
    private Long userId;
    private String recommendationType = "CART_BASED"; // CART_BASED, POPULAR, SIMILAR
    private Integer limit = 10;
    private Long productId; // Optional: for similar products
} 