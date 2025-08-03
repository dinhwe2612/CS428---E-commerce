package com.catalog.catalog_service.dto;

import java.math.BigDecimal;
import java.util.List;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "Dynamic pricing information for a product")
public class DynamicPriceDTO {
    
    @Schema(description = "Product ID", example = "1")
    private Long productId;
    
    @Schema(description = "Original price before any discounts", example = "100.00")
    private BigDecimal originalPrice;
    
    @Schema(description = "Final price after applying dynamic pricing rules", example = "85.00")
    private BigDecimal dynamicPrice;
    
    @Schema(description = "Total discount amount", example = "15.00")
    private BigDecimal discountAmount;
    
    @Schema(description = "Discount percentage", example = "15.0")
    private Double discountPercentage;
    
    @Schema(description = "List of applied pricing rules", example = "[\"Valentine's Day Discount\", \"Flash Sale\"]")
    private List<String> appliedRules;
    
    @Schema(description = "Explanation of the pricing calculation", example = "Special pricing due to: Valentine's Day Discount")
    private String priceReason;
    
    @Schema(description = "Whether the price has changed from original", example = "true")
    private boolean isPriceChanged;
}
