package com.catalog.catalog_service.dto;

import java.math.BigDecimal;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class EnhancedProductDTO {
    private Long id;
    private Long categoryId;
    private String productPath;
    private String name;
    private String originalPrice;
    private BigDecimal dynamicPrice;
    private BigDecimal discountAmount;
    private Double discountPercentage;
    private List<String> appliedRules;
    private String priceReason;
    private boolean isPriceChanged;
    private String descriptionHtml;
    private String descriptionText;
    private List<String> imageUrls;
}
