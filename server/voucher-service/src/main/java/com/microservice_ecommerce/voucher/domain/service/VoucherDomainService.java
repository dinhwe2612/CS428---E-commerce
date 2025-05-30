package com.microservice_ecommerce.voucher.domain.service;

import java.math.BigDecimal;

import org.springframework.stereotype.Service;

import com.microservice_ecommerce.voucher.application.dto.ApplyVoucherResponseDTO;
import com.microservice_ecommerce.voucher.domain.exception.VoucherInvalidException;
import com.microservice_ecommerce.voucher.domain.exception.VoucherNotFoundException;
import com.microservice_ecommerce.voucher.domain.model.Voucher;
import com.microservice_ecommerce.voucher.infrastructure.repository.VoucherRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class VoucherDomainService {
    
    private final VoucherRepository voucherRepository;
    
    public ApplyVoucherResponseDTO applyVoucher(Voucher voucher, Integer quantity, BigDecimal productPrice) {
        if (!voucher.isValid()) {
            BigDecimal totalAmount = productPrice.multiply(BigDecimal.valueOf(quantity));
            return ApplyVoucherResponseDTO.builder()
                    .voucherCode(voucher.getCode())
                    .discountAmount(BigDecimal.ZERO)
                    .originalAmount(totalAmount)
                    .finalAmount(totalAmount)
                    .isValid(false)
                    .message("Voucher is not valid or has expired")
                    .build();
        }
        
        BigDecimal totalAmount = productPrice.multiply(BigDecimal.valueOf(quantity));
        
        if (voucher.getMinOrderAmount() != null && totalAmount.compareTo(voucher.getMinOrderAmount()) < 0) {
            return ApplyVoucherResponseDTO.builder()
                    .voucherCode(voucher.getCode())
                    .discountAmount(BigDecimal.ZERO)
                    .originalAmount(totalAmount)
                    .finalAmount(totalAmount)
                    .isValid(false)
                    .message("Order amount does not meet minimum requirement: " + voucher.getMinOrderAmount())
                    .build();
        }
        
        BigDecimal discountAmount = voucher.calculateDiscount(totalAmount);
        BigDecimal finalAmount = totalAmount.subtract(discountAmount);
        
        return ApplyVoucherResponseDTO.builder()
                .voucherCode(voucher.getCode())
                .discountAmount(discountAmount)
                .originalAmount(totalAmount)
                .finalAmount(finalAmount)
                .isValid(true)
                .message("Voucher applied successfully")
                .build();
    }
    
    public void incrementVoucherUsage(String voucherCode) {
        Voucher voucher = voucherRepository.findByCode(voucherCode)
                .orElseThrow(() -> new VoucherNotFoundException("Voucher not found with code: " + voucherCode));
        
        if (voucher.getUsedCount() != null && voucher.getUsageLimit() != null 
                && voucher.getUsedCount() >= voucher.getUsageLimit()) {
            throw new VoucherInvalidException("Voucher usage limit exceeded");
        }
        
        voucher.incrementUsage();
        voucherRepository.save(voucher);
    }
} 