package com.catalog.catalog_service.dto.request;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.LocalTime;

import com.catalog.catalog_service.model.PricingRule;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "Request to create a new pricing rule")
public class CreatePricingRuleRequest {
    
    @Schema(description = "Product ID to apply the rule to (optional if applying to category or all products)", example = "1")
    private Long productId;
    
    @NotBlank(message = "Rule name is required")
    @Schema(description = "Name of the pricing rule", example = "Valentine's Day Discount", required = true)
    private String ruleName;
    
    @Schema(description = "Description of the pricing rule", example = "10% discount on all products during Valentine's Day")
    private String description;
    
    @NotNull(message = "Trigger type is required")
    @Schema(description = "When the pricing rule should be triggered", example = "SPECIAL_DAY", required = true,
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
    
    @NotNull(message = "Rule type is required")
    @Schema(description = "Type of pricing rule", example = "PERCENTAGE", required = true,
            allowableValues = {"PERCENTAGE", "FIXED", "DECAY"})
    private PricingRule.RuleType type;
    
    @NotNull(message = "Modifier value is required")
    @DecimalMin(value = "0.0", message = "Modifier value must be positive")
    @Schema(description = "Discount value (percentage for PERCENTAGE type, amount for FIXED type)", 
            example = "10.0", required = true)
    private BigDecimal modifierValue;
    
    @DecimalMin(value = "0.0", message = "Max discount amount must be positive")
    @Schema(description = "Maximum discount amount (optional cap)", example = "50.0")
    private BigDecimal maxDiscountAmount;
    
    @DecimalMin(value = "0.0", message = "Min price must be positive")
    @Schema(description = "Minimum price after discount", example = "5.0")
    private BigDecimal minPrice;
    
    @NotNull(message = "Priority is required")
    @Min(value = 1, message = "Priority must be at least 1")
    @Max(value = 100, message = "Priority cannot exceed 100")
    @Schema(description = "Rule priority (1-100, lower number = higher priority)", example = "10", required = true)
    private Integer priority;
    
    @Schema(description = "Whether the rule is active", example = "true", defaultValue = "true")
    private Boolean isActive = true;
    
    @Schema(description = "Whether to apply to all products", example = "false", defaultValue = "false")
    private Boolean applyToAllProducts = false;
    
    @Schema(description = "Category ID to apply the rule to (optional if applying to specific product or all products)", 
            example = "2")
    private Long categoryId;
}
