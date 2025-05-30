package com.microservice_ecommerce.voucher.application.dto;

import java.math.BigDecimal;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ApplyVoucherRequestDTO {
    private String voucherCode;
    private String code; 
    private BigDecimal orderAmount;
    private List<Long> productIds;
    private Long productId; 
    private Integer quantity; 
    
    public String getCode() {
        return code != null ? code : voucherCode;
    }
    
    public Long getProductId() {
        return productId != null ? productId : (productIds != null && !productIds.isEmpty() ? productIds.get(0) : null);
    }
} 