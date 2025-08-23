package com.catalog.catalog_service.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.EqualsAndHashCode;

@Entity
@Table(name = "pricing_rules")
@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString(exclude = {"pricingRuleProducts", "pricingRuleCategories"})
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class PricingRule {
    
    public enum RuleType {
        PERCENTAGE,
        FIXED,
        DECAY
    }
    
    public enum TriggerType {
        TIME_OF_DAY,
        PRODUCT_CONDITION,
        SPECIAL_DAY,
        COMBINED
    }
    
    public enum ProductCondition {
        NEW,
        OLD,
        EXPIRING_SOON
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Include
    private Long id;

    
    @OneToMany(mappedBy = "pricingRule", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private Set<PricingRuleProduct> pricingRuleProducts = new HashSet<>();
    
    @OneToMany(mappedBy = "pricingRule", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private Set<PricingRuleCategory> pricingRuleCategories = new HashSet<>();

    @Column(name = "rule_name", nullable = false)
    private String ruleName;
    
    @Column(name = "description")
    private String description;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "trigger_type", nullable = false)
    private TriggerType triggerType;
    
    @Column(name = "start_date")
    private LocalDateTime startDate;
    
    @Column(name = "end_date")
    private LocalDateTime endDate;
    
    @Column(name = "start_time")
    private LocalTime startTime;
    //
    @Column(name = "end_time")
    private LocalTime endTime;
    
    @Column(name = "special_day_name")
    private String specialDayName;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "product_condition")
    private ProductCondition productCondition;

    @Enumerated(EnumType.STRING)
    @Column(name = "type", nullable = false)
    private RuleType type;

    @Column(name = "modifier_value", nullable = false)
    private BigDecimal modifierValue;
    
    @Column(name = "max_discount_amount")
    private BigDecimal maxDiscountAmount;
    
    @Column(name = "min_price")
    private BigDecimal minPrice;
    
    @Column(name = "priority", nullable = false)
    private Integer priority;
    
    @Column(name = "is_active", nullable = false)
    private Boolean isActive = true;
    
    @Column(name = "apply_to_all_products")
    private Boolean applyToAllProducts = false;
    
    
    
    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;
    
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @Version
    private Long version;
    
    @PrePersist
    protected void onCreate() {
        LocalDateTime now = LocalDateTime.now(java.time.ZoneId.of("Asia/Ho_Chi_Minh"));
        createdAt = now;
        updatedAt = now;
    }
    
    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now(java.time.ZoneId.of("Asia/Ho_Chi_Minh"));
    }
    
    public boolean isApplicableNow() {
        LocalDateTime now = LocalDateTime.now(java.time.ZoneId.of("Asia/Ho_Chi_Minh"));
        
        if (startDate != null && now.isBefore(startDate)) {
            return false;
        }
        
        if (endDate != null && now.isAfter(endDate)) {
            return false;
        }
        
        return true;
    }
}
