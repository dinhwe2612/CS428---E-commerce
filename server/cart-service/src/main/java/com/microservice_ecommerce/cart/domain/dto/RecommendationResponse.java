package com.microservice_ecommerce.cart.domain.dto;

import java.math.BigDecimal;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RecommendationResponse {
    private Long userId;
    private String recommendationType;
    private List<ProductDTO> recommendedProducts;
    private String reasoning;
    private Double confidence;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ProductDTO {
        private Long id;
        private String name;
        private String description;
        private Long categoryId;
        private List<Long> imageIds;
        private List<String> imageUrls;
        private BigDecimal price;
    }
}
