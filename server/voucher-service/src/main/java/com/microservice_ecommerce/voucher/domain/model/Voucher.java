package com.microservice_ecommerce.voucher.domain.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "vouchers")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Voucher {
    
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    
    @Column(name = "code", nullable = false, unique = true)
    private String code;
    
    @Column(name = "name", nullable = false)
    private String name;
    
    @Column(name = "description")
    private String description;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "type", nullable = false)
    private VoucherType type;
    
    @Column(name = "value", nullable = false, precision = 19, scale = 2)
    private BigDecimal value;
    
    @Column(name = "min_order_amount", precision = 19, scale = 2)
    private BigDecimal minOrderAmount;
    
    @Column(name = "max_discount_amount", precision = 19, scale = 2)
    private BigDecimal maxDiscountAmount;
    
    @Column(name = "usage_limit")
    private Integer usageLimit;
    
    @Column(name = "used_count", nullable = false)
    @Builder.Default
    private Integer usedCount = 0;
    
    @Column(name = "start_date", nullable = false)
    private LocalDateTime startDate;
    
    @Column(name = "end_date", nullable = false)
    private LocalDateTime endDate;
    
    @Column(name = "is_active", nullable = false)
    @Builder.Default
    private Boolean isActive = true;
    
    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;
    
    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
    
    public boolean isValid() {
        return isActive && 
               LocalDateTime.now().isAfter(startDate) && 
               LocalDateTime.now().isBefore(endDate) &&
               (usageLimit == null || usedCount < usageLimit);
    }
    
    public BigDecimal calculateDiscount(BigDecimal orderAmount) {
        if (!isValid() || (minOrderAmount != null && orderAmount.compareTo(minOrderAmount) < 0)) {
            return BigDecimal.ZERO;
        }
        
        BigDecimal discount;
        if (type == VoucherType.PERCENTAGE) {
            discount = orderAmount.multiply(value.divide(BigDecimal.valueOf(100)));
        } else {
            discount = value;
        }
        
        if (maxDiscountAmount != null && discount.compareTo(maxDiscountAmount) > 0) {
            discount = maxDiscountAmount;
        }
        
        return discount;
    }
    
    public void incrementUsage() {
        this.usedCount++;
    }
} 