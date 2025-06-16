package com.microservice_ecommerce.voucher.application.service;

import java.util.List;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.microservice_ecommerce.integration.catalog.client.CatalogServiceClient;
import com.microservice_ecommerce.integration.catalog.dto.response.ProductResponse;
import com.microservice_ecommerce.voucher.application.dto.ApplyVoucherRequestDTO;
import com.microservice_ecommerce.voucher.application.dto.ApplyVoucherResponseDTO;
import com.microservice_ecommerce.voucher.application.dto.VoucherDTO;
import com.microservice_ecommerce.voucher.domain.exception.DuplicateVoucherException;
import com.microservice_ecommerce.voucher.domain.exception.VoucherNotFoundException;
import com.microservice_ecommerce.voucher.domain.model.Voucher;
import com.microservice_ecommerce.voucher.domain.service.VoucherDomainService;
import com.microservice_ecommerce.voucher.infrastructure.repository.VoucherRepository;
import com.microservice_ecommerce.voucher.interfaces.mapper.VoucherMapper;

import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
@Transactional
public class VoucherService {
    // Repository
    private final VoucherRepository voucherRepository;
    private final VoucherDomainService voucherDomainService;
    private final VoucherMapper voucherMapper;
    private final CatalogServiceClient catalogServiceClient;
    
    public VoucherDTO createVoucher(VoucherDTO voucherDTO) {
        if (voucherRepository.existsByCode(voucherDTO.getCode())) {
            throw new DuplicateVoucherException("Voucher code '" + voucherDTO.getCode() + "' already exists");
        }
        
        Voucher voucher = voucherMapper.toEntity(voucherDTO);
        Voucher savedVoucher = voucherRepository.saveAndFlush(voucher);
        return voucherMapper.toDTO(savedVoucher);
    }
    
    @Transactional(readOnly = true)
    public VoucherDTO getVoucherById(UUID id) {
        Voucher voucher = voucherRepository.findById(id)
                .orElseThrow(() -> new VoucherNotFoundException("Voucher not found with id: " + id));
        return voucherMapper.toDTO(voucher);
    }
    
    @Transactional(readOnly = true)
    public VoucherDTO getVoucherByCode(String code) {
        Voucher voucher = voucherRepository.findByCode(code)
                .orElseThrow(() -> new VoucherNotFoundException("Voucher not found with code: " + code));
        return voucherMapper.toDTO(voucher);
    }
    
    @Transactional(readOnly = true)
    public Page<VoucherDTO> getAllVouchers(Pageable pageable) {
        return voucherRepository.findAll(pageable)
                .map(voucherMapper::toDTO);
    }
    
    @Transactional(readOnly = true)
    public List<VoucherDTO> getAllVouchers() {
        return voucherRepository.findAll()
                .stream()
                .map(voucherMapper::toDTO)
                .toList();
    }
    
    public VoucherDTO updateVoucher(UUID id, VoucherDTO voucherDTO) {
        Voucher existingVoucher = voucherRepository.findById(id)
                .orElseThrow(() -> new VoucherNotFoundException("Voucher not found with id: " + id));
        
        Voucher updatedVoucher = voucherMapper.updateEntity(existingVoucher, voucherDTO);
        Voucher savedVoucher = voucherRepository.save(updatedVoucher);
        return voucherMapper.toDTO(savedVoucher);
    }
    
    public void deleteVoucher(UUID id) {
        if (!voucherRepository.existsById(id)) {
            throw new VoucherNotFoundException("Voucher not found with id: " + id);
        }
        voucherRepository.deleteById(id);
    }
    
    public Mono<ApplyVoucherResponseDTO> applyVoucher(ApplyVoucherRequestDTO request) {
        return Mono.fromCallable(() -> {
            Voucher voucher = voucherRepository.findByCode(request.getCode())
                    .orElseThrow(() -> new VoucherNotFoundException("Voucher not found with code: " + request.getCode()));
            
            ProductResponse product = catalogServiceClient.getProductDetails(request.getProductId());
            
            if (product == null) {
                throw new RuntimeException("Product not found with id: " + request.getProductId());
            }
            
            return voucherDomainService.applyVoucher(voucher, request.getQuantity(), product.getPrice());
        });
    }
    
    public void confirmVoucherUsage(String voucherCode) {
        voucherDomainService.incrementVoucherUsage(voucherCode);
    }
} 