package com.microservice_ecommerce.voucher.interfaces.dto.request;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import com.microservice_ecommerce.voucher.domain.model.VoucherType;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CreateVoucherRequestDTO {
    
    @NotBlank(message = "Voucher code is required")
    private String code;
    
    @NotBlank(message = "Voucher name is required")
    private String name;
    
    private String description;
    
    @NotNull(message = "Voucher type is required")
    private VoucherType type;
    
    @NotNull(message = "Voucher value is required")
    @Positive(message = "Voucher value must be positive")
    private BigDecimal value;
    
    private BigDecimal minOrderAmount;
    
    private BigDecimal maxDiscountAmount;
    
    private Integer usageLimit;
    
    @NotNull(message = "Start date is required")
    private LocalDateTime startDate;
    
    @NotNull(message = "End date is required")
    private LocalDateTime endDate;
    
    @Builder.Default
    private Boolean isActive = true;
} 