package com.microservice_ecommerce.voucher.interfaces.controller;

import java.util.List;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.microservice_ecommerce.voucher.application.dto.ApiResponse;
import com.microservice_ecommerce.voucher.application.dto.ApplyVoucherRequestDTO;
import com.microservice_ecommerce.voucher.application.dto.ApplyVoucherResponseDTO;
import com.microservice_ecommerce.voucher.application.service.VoucherService;
import com.microservice_ecommerce.voucher.interfaces.dto.request.CreateVoucherRequestDTO;
import com.microservice_ecommerce.voucher.interfaces.dto.request.UpdateVoucherRequestDTO;
import com.microservice_ecommerce.voucher.interfaces.dto.response.VoucherResponseDTO;
import com.microservice_ecommerce.voucher.interfaces.mapper.VoucherMapper;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/v1/vouchers")
@RequiredArgsConstructor
public class VoucherController {
    
    private final VoucherService voucherService;
    private final VoucherMapper voucherMapper;
    
    @PostMapping
    public ResponseEntity<ApiResponse<VoucherResponseDTO>> createVoucher(@Valid @RequestBody CreateVoucherRequestDTO requestDTO) {
        VoucherResponseDTO voucher = voucherMapper.toVoucherResponseDTO(
            voucherService.createVoucher(voucherMapper.toVoucherDTO(requestDTO))
        );
        ApiResponse<VoucherResponseDTO> response = new ApiResponse<>(true, "Voucher created successfully", voucher);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<VoucherResponseDTO>> getVoucherById(@PathVariable UUID id) {
        VoucherResponseDTO voucher = voucherMapper.toVoucherResponseDTO(voucherService.getVoucherById(id));
        ApiResponse<VoucherResponseDTO> response = new ApiResponse<>(true, "Voucher retrieved successfully", voucher);
        return ResponseEntity.ok(response);
    }
    
    @GetMapping("/code/{code}")
    public ResponseEntity<ApiResponse<VoucherResponseDTO>> getVoucherByCode(@PathVariable String code) {
        VoucherResponseDTO voucher = voucherMapper.toVoucherResponseDTO(voucherService.getVoucherByCode(code));
        ApiResponse<VoucherResponseDTO> response = new ApiResponse<>(true, "Voucher retrieved successfully", voucher);
        return ResponseEntity.ok(response);
    }
    
    @GetMapping
    public ResponseEntity<ApiResponse<Page<VoucherResponseDTO>>> getAllVouchers(Pageable pageable) {
        Page<VoucherResponseDTO> vouchers = voucherService.getAllVouchers(pageable)
                .map(voucherMapper::toVoucherResponseDTO);
        ApiResponse<Page<VoucherResponseDTO>> response = new ApiResponse<>(true, "Vouchers retrieved successfully", vouchers);
        return ResponseEntity.ok(response);
    }
    
    @GetMapping("/all")
    public ResponseEntity<ApiResponse<List<VoucherResponseDTO>>> getAllVouchers() {
        List<VoucherResponseDTO> vouchers = voucherService.getAllVouchers()
                .stream()
                .map(voucherMapper::toVoucherResponseDTO)
                .toList();
        ApiResponse<List<VoucherResponseDTO>> response = new ApiResponse<>(true, "Vouchers retrieved successfully", vouchers);
        return ResponseEntity.ok(response);
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<VoucherResponseDTO>> updateVoucher(@PathVariable UUID id, @RequestBody UpdateVoucherRequestDTO requestDTO) {
        VoucherResponseDTO voucher = voucherMapper.toVoucherResponseDTO(
            voucherService.updateVoucher(id, voucherMapper.toVoucherDTO(requestDTO))
        );
        ApiResponse<VoucherResponseDTO> response = new ApiResponse<>(true, "Voucher updated successfully", voucher);
        return ResponseEntity.ok(response);
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteVoucher(@PathVariable UUID id) {
        voucherService.deleteVoucher(id);
        ApiResponse<Void> response = new ApiResponse<>(true, "Voucher deleted successfully", null);
        return ResponseEntity.ok(response);
    }
    
    @PostMapping("/apply")
    public Mono<ResponseEntity<ApiResponse<ApplyVoucherResponseDTO>>> applyVoucher(@Valid @RequestBody ApplyVoucherRequestDTO requestDTO) {
        return voucherService.applyVoucher(requestDTO)
                .map(result -> {
                    ApiResponse<ApplyVoucherResponseDTO> response = new ApiResponse<>(true, "Voucher application processed", result);
                    return ResponseEntity.ok(response);
                });
    }
    
    @PostMapping("/confirm/{voucherCode}")
    public ResponseEntity<ApiResponse<Void>> confirmVoucherUsage(@PathVariable String voucherCode) {
        voucherService.confirmVoucherUsage(voucherCode);
        ApiResponse<Void> response = new ApiResponse<>(true, "Voucher usage confirmed", null);
        return ResponseEntity.ok(response);
    }
} 