package com.catalog.catalog_service.dto.request;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.LocalTime;

import com.catalog.catalog_service.model.PricingRule;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UpdatePricingRuleRequest {
    
    private Long inventoryId;
    
    private Long productId;
    
    private String ruleName;
    
    private String description;
    
    private PricingRule.TriggerType triggerType;
    
    private LocalDateTime startDate;
    
    private LocalDateTime endDate;
    
    private LocalTime startTime;
    
    private LocalTime endTime;
    
    private String specialDayName;
    
    private PricingRule.ProductCondition productCondition;
    
    private PricingRule.RuleType type;
    
    @DecimalMin(value = "0.0", message = "Modifier value must be positive")
    private BigDecimal modifierValue;
    
    @DecimalMin(value = "0.0", message = "Max discount amount must be positive")
    private BigDecimal maxDiscountAmount;
    
    @DecimalMin(value = "0.0", message = "Min price must be positive")
    private BigDecimal minPrice;
    
    @Min(value = 1, message = "Priority must be at least 1")
    @Max(value = 100, message = "Priority cannot exceed 100")
    private Integer priority;
    
    private Boolean isActive;
    
    private Boolean applyToAllProducts;
    
    private Long categoryId;
}
