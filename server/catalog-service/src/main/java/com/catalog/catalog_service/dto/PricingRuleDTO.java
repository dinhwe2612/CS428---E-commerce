package com.catalog.catalog_service.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.LocalTime;

import com.catalog.catalog_service.model.PricingRule;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "Pricing rule information")
public class PricingRuleDTO {
    
    @Schema(description = "Unique identifier of the pricing rule", example = "1")
    private Long id;
    
    @Schema(description = "Product ID this rule applies to", example = "1")
    private Long productId;
    
    @Schema(description = "Name of the pricing rule", example = "Valentine's Day Discount")
    private String ruleName;
    
    @Schema(description = "Description of the pricing rule", example = "10% discount on all products during Valentine's Day")
    private String description;
    
    @Schema(description = "Trigger type for the rule", example = "SPECIAL_DAY",
            allowableValues = {"TIME_OF_DAY", "PRODUCT_CONDITION", "SPECIAL_DAY", "COMBINED"})
    private PricingRule.TriggerType triggerType;
    
    @Schema(description = "Rule start date", example = "2024-02-14T00:00:00")
    private LocalDateTime startDate;
    
    @Schema(description = "Rule end date", example = "2024-02-14T23:59:59")
    private LocalDateTime endDate;
    
    @Schema(description = "Start time for daily recurrence", example = "09:00:00")
    private LocalTime startTime;
    
    @Schema(description = "End time for daily recurrence", example = "17:00:00")
    private LocalTime endTime;
    
    @Schema(description = "Special day name", example = "Valentine's Day")
    private String specialDayName;
    
    @Schema(description = "Product condition", example = "NEW",
            allowableValues = {"NEW", "OLD", "EXPIRING_SOON"})
    private PricingRule.ProductCondition productCondition;
    
    @Schema(description = "Type of pricing rule", example = "PERCENTAGE",
            allowableValues = {"PERCENTAGE", "FIXED", "DECAY"})
    private PricingRule.RuleType type;
    
    @Schema(description = "Discount modifier value", example = "10.0")
    private BigDecimal modifierValue;
    
    @Schema(description = "Maximum discount amount", example = "50.0")
    private BigDecimal maxDiscountAmount;
    
    @Schema(description = "Minimum price after discount", example = "5.0")
    private BigDecimal minPrice;
    
    @Schema(description = "Rule priority (lower number = higher priority)", example = "10")
    private Integer priority;
    
    @Schema(description = "Whether the rule is active", example = "true")
    private Boolean isActive;
    
    @Schema(description = "Whether the rule applies to all products", example = "false")
    private Boolean applyToAllProducts;
    
    @Schema(description = "Category ID this rule applies to", example = "2")
    private Long categoryId;
    
    @Schema(description = "Rule creation timestamp", example = "2024-01-15T10:30:00")
    private LocalDateTime createdAt;
    
    @Schema(description = "Rule last update timestamp", example = "2024-01-20T15:45:00")
    private LocalDateTime updatedAt;
    
    @Schema(description = "Version for optimistic locking", example = "1")
    private Long version;
}
