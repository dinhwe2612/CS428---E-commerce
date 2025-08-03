package com.catalog.catalog_service.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.LocalTime;

import com.catalog.catalog_service.model.PricingRule;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PricingRuleDTO {
    private Long id;
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
    private BigDecimal modifierValue;
    private BigDecimal maxDiscountAmount;
    private BigDecimal minPrice;
    private Integer priority;
    private Boolean isActive;
    private Boolean applyToAllProducts;
    private Long categoryId;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private Long version;
}
