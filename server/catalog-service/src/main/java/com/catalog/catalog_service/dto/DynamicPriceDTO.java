package com.catalog.catalog_service.dto;

import java.math.BigDecimal;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DynamicPriceDTO {
    private Long productId;
    private BigDecimal originalPrice;
    private BigDecimal dynamicPrice;
    private BigDecimal discountAmount;
    private Double discountPercentage;
    private List<String> appliedRules;
    private String priceReason;
    private boolean isPriceChanged;
}
