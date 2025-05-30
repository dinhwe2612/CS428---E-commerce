package com.microservice_ecommerce.voucher.interfaces.dto.request;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import com.microservice_ecommerce.voucher.domain.model.VoucherType;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UpdateVoucherRequestDTO {
    
    private String name;
    private String description;
    private VoucherType type;
    private BigDecimal value;
    private BigDecimal minOrderAmount;
    private BigDecimal maxDiscountAmount;
    private Integer usageLimit;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private Boolean isActive;
} 