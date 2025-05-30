package com.microservice_ecommerce.voucher.interfaces.mapper;

import org.springframework.stereotype.Component;

import com.microservice_ecommerce.voucher.application.dto.VoucherDTO;
import com.microservice_ecommerce.voucher.domain.model.Voucher;
import com.microservice_ecommerce.voucher.interfaces.dto.request.CreateVoucherRequestDTO;
import com.microservice_ecommerce.voucher.interfaces.dto.request.UpdateVoucherRequestDTO;
import com.microservice_ecommerce.voucher.interfaces.dto.response.VoucherResponseDTO;

@Component
public class VoucherMapper {
    
    public VoucherDTO toDTO(Voucher voucher) {
        return VoucherDTO.builder()
                .id(voucher.getId())
                .code(voucher.getCode())
                .name(voucher.getName())
                .description(voucher.getDescription())
                .type(voucher.getType())
                .value(voucher.getValue())
                .minOrderAmount(voucher.getMinOrderAmount())
                .maxDiscountAmount(voucher.getMaxDiscountAmount())
                .usageLimit(voucher.getUsageLimit())
                .usedCount(voucher.getUsedCount())
                .startDate(voucher.getStartDate())
                .endDate(voucher.getEndDate())
                .isActive(voucher.getIsActive())
                .createdAt(voucher.getCreatedAt())
                .updatedAt(voucher.getUpdatedAt())
                .build();
    }
    
    public Voucher toEntity(VoucherDTO voucherDTO) {
        return Voucher.builder()
                .id(voucherDTO.getId())
                .code(voucherDTO.getCode())
                .name(voucherDTO.getName())
                .description(voucherDTO.getDescription())
                .type(voucherDTO.getType())
                .value(voucherDTO.getValue())
                .minOrderAmount(voucherDTO.getMinOrderAmount())
                .maxDiscountAmount(voucherDTO.getMaxDiscountAmount())
                .usageLimit(voucherDTO.getUsageLimit())
                .usedCount(voucherDTO.getUsedCount())
                .startDate(voucherDTO.getStartDate())
                .endDate(voucherDTO.getEndDate())
                .isActive(voucherDTO.getIsActive())
                .build();
    }
    
    public VoucherDTO toVoucherDTO(CreateVoucherRequestDTO requestDTO) {
        return VoucherDTO.builder()
                .code(requestDTO.getCode())
                .name(requestDTO.getName())
                .description(requestDTO.getDescription())
                .type(requestDTO.getType())
                .value(requestDTO.getValue())
                .minOrderAmount(requestDTO.getMinOrderAmount())
                .maxDiscountAmount(requestDTO.getMaxDiscountAmount())
                .usageLimit(requestDTO.getUsageLimit())
                .usedCount(0)
                .startDate(requestDTO.getStartDate())
                .endDate(requestDTO.getEndDate())
                .isActive(requestDTO.getIsActive())
                .build();
    }
    
    public VoucherResponseDTO toVoucherResponseDTO(VoucherDTO voucherDTO) {
        return VoucherResponseDTO.builder()
                .id(voucherDTO.getId())
                .code(voucherDTO.getCode())
                .name(voucherDTO.getName())
                .description(voucherDTO.getDescription())
                .type(voucherDTO.getType())
                .value(voucherDTO.getValue())
                .minOrderAmount(voucherDTO.getMinOrderAmount())
                .maxDiscountAmount(voucherDTO.getMaxDiscountAmount())
                .usageLimit(voucherDTO.getUsageLimit())
                .usedCount(voucherDTO.getUsedCount())
                .startDate(voucherDTO.getStartDate())
                .endDate(voucherDTO.getEndDate())
                .isActive(voucherDTO.getIsActive())
                .createdAt(voucherDTO.getCreatedAt())
                .updatedAt(voucherDTO.getUpdatedAt())
                .build();
    }
    
    public Voucher updateEntity(Voucher existingVoucher, VoucherDTO voucherDTO) {
        if (voucherDTO.getName() != null) {
            existingVoucher.setName(voucherDTO.getName());
        }
        if (voucherDTO.getDescription() != null) {
            existingVoucher.setDescription(voucherDTO.getDescription());
        }
        if (voucherDTO.getType() != null) {
            existingVoucher.setType(voucherDTO.getType());
        }
        if (voucherDTO.getValue() != null) {
            existingVoucher.setValue(voucherDTO.getValue());
        }
        if (voucherDTO.getMinOrderAmount() != null) {
            existingVoucher.setMinOrderAmount(voucherDTO.getMinOrderAmount());
        }
        if (voucherDTO.getMaxDiscountAmount() != null) {
            existingVoucher.setMaxDiscountAmount(voucherDTO.getMaxDiscountAmount());
        }
        if (voucherDTO.getUsageLimit() != null) {
            existingVoucher.setUsageLimit(voucherDTO.getUsageLimit());
        }
        if (voucherDTO.getStartDate() != null) {
            existingVoucher.setStartDate(voucherDTO.getStartDate());
        }
        if (voucherDTO.getEndDate() != null) {
            existingVoucher.setEndDate(voucherDTO.getEndDate());
        }
        if (voucherDTO.getIsActive() != null) {
            existingVoucher.setIsActive(voucherDTO.getIsActive());
        }
        return existingVoucher;
    }
    
    public VoucherDTO toVoucherDTO(UpdateVoucherRequestDTO requestDTO) {
        return VoucherDTO.builder()
                .name(requestDTO.getName())
                .description(requestDTO.getDescription())
                .type(requestDTO.getType())
                .value(requestDTO.getValue())
                .minOrderAmount(requestDTO.getMinOrderAmount())
                .maxDiscountAmount(requestDTO.getMaxDiscountAmount())
                .usageLimit(requestDTO.getUsageLimit())
                .startDate(requestDTO.getStartDate())
                .endDate(requestDTO.getEndDate())
                .isActive(requestDTO.getIsActive())
                .build();
    }
} 