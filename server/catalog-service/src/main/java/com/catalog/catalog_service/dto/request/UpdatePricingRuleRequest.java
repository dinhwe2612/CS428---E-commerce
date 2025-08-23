package com.catalog.catalog_service.dto.request;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

import com.catalog.catalog_service.model.PricingRule;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "Request to update an existing pricing rule. All fields are optional and only provided fields will be updated.")
public class UpdatePricingRuleRequest {
    
    @Schema(description = "Product IDs to apply the rule to", example = "[1, 2, 3]")
    private List<Long> productIds;
    
    @Schema(description = "Name of the pricing rule", example = "Updated Valentine's Day Discount")
    private String ruleName;
    
    @Schema(description = "Description of the pricing rule", example = "Updated 15% discount on all products during Valentine's Day")
    private String description;
    
    @Schema(description = "When the pricing rule should be triggered", example = "SPECIAL_DAY",
            allowableValues = {"TIME_OF_DAY", "PRODUCT_CONDITION", "SPECIAL_DAY", "COMBINED"})
    private PricingRule.TriggerType triggerType;
    
    @Schema(description = "Start date for the rule (ISO 8601 format)", example = "2024-02-14T00:00:00")
    private LocalDateTime startDate;
    
    @Schema(description = "End date for the rule (ISO 8601 format)", example = "2024-02-14T23:59:59")
    private LocalDateTime endDate;
    
    @Schema(description = "Start time for daily recurrence (HH:mm:ss format)", example = "09:00:00")
    private LocalTime startTime;
    
    @Schema(description = "End time for daily recurrence (HH:mm:ss format)", example = "17:00:00")
    private LocalTime endTime;
    
    @Schema(description = "Name of the special day", example = "Valentine's Day")
    private String specialDayName;
    
    @Schema(description = "Product condition that triggers the rule", example = "NEW",
            allowableValues = {"NEW", "OLD", "EXPIRING_SOON"})
    private PricingRule.ProductCondition productCondition;
    
    @Schema(description = "Type of pricing rule", example = "PERCENTAGE",
            allowableValues = {"PERCENTAGE", "FIXED", "DECAY"})
    private PricingRule.RuleType type;
    
    @DecimalMin(value = "0.0", message = "Modifier value must be positive")
    @Schema(description = "Discount value (percentage for PERCENTAGE type, amount for FIXED type)", example = "15.0")
    private BigDecimal modifierValue;
    
    @DecimalMin(value = "0.0", message = "Max discount amount must be positive")
    @Schema(description = "Maximum discount amount (optional cap)", example = "75.0")
    private BigDecimal maxDiscountAmount;
    
    @DecimalMin(value = "0.0", message = "Min price must be positive")
    @Schema(description = "Minimum price after discount", example = "3.0")
    private BigDecimal minPrice;
    
    @Min(value = 1, message = "Priority must be at least 1")
    @Max(value = 100, message = "Priority cannot exceed 100")
    @Schema(description = "Rule priority (1-100, lower number = higher priority)", example = "5")
    private Integer priority;
    
    @Schema(description = "Whether the rule is active", example = "true")
    private Boolean isActive;
    
    @Schema(description = "Whether to apply to all products", example = "false")
    private Boolean applyToAllProducts;
    
    @Schema(description = "Category IDs to apply the rule to", example = "[1, 2, 3]")
    private List<Long> categoryIds;
}
