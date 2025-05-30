package com.microservice_ecommerce.voucher.application.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.microservice_ecommerce.integration.catalog.client.CatalogServiceClient;
import com.microservice_ecommerce.voucher.application.dto.VoucherDTO;
import com.microservice_ecommerce.voucher.domain.exception.DuplicateVoucherException;
import com.microservice_ecommerce.voucher.domain.exception.VoucherNotFoundException;
import com.microservice_ecommerce.voucher.domain.model.Voucher;
import com.microservice_ecommerce.voucher.domain.model.VoucherType;
import com.microservice_ecommerce.voucher.domain.service.VoucherDomainService;
import com.microservice_ecommerce.voucher.infrastructure.repository.VoucherRepository;
import com.microservice_ecommerce.voucher.interfaces.mapper.VoucherMapper;

@ExtendWith(MockitoExtension.class)
class VoucherServiceTest {

    @Mock
    private VoucherRepository voucherRepository;

    @Mock
    private VoucherMapper voucherMapper;

    @Mock
    private VoucherDomainService voucherDomainService;

    @Mock
    private CatalogServiceClient catalogServiceClient;

    @InjectMocks
    private VoucherService voucherService;

    private VoucherDTO voucherDTO;
    private Voucher voucher;
    private Voucher savedVoucher;

    @BeforeEach
    void setUp() {
        voucherDTO = VoucherDTO.builder()
                .code("SALE2024")
                .name("Sale 2024")
                .description("Special discount")
                .type(VoucherType.PERCENTAGE)
                .value(BigDecimal.valueOf(10))
                .minOrderAmount(BigDecimal.valueOf(100))
                .maxDiscountAmount(BigDecimal.valueOf(50))
                .usageLimit(100)
                .usedCount(0)
                .startDate(LocalDateTime.now())
                .endDate(LocalDateTime.now().plusDays(30))
                .isActive(true)
                .build();

        voucher = Voucher.builder()
                .code("SALE2024")
                .name("Sale 2024")
                .description("Special discount")
                .type(VoucherType.PERCENTAGE)
                .value(BigDecimal.valueOf(10))
                .minOrderAmount(BigDecimal.valueOf(100))
                .maxDiscountAmount(BigDecimal.valueOf(50))
                .usageLimit(100)
                .usedCount(0)
                .startDate(LocalDateTime.now())
                .endDate(LocalDateTime.now().plusDays(30))
                .isActive(true)
                .build();

        savedVoucher = Voucher.builder()
                .id(UUID.randomUUID())
                .code("SALE2024")
                .name("Sale 2024")
                .description("Special discount")
                .type(VoucherType.PERCENTAGE)
                .value(BigDecimal.valueOf(10))
                .minOrderAmount(BigDecimal.valueOf(100))
                .maxDiscountAmount(BigDecimal.valueOf(50))
                .usageLimit(100)
                .usedCount(0)
                .startDate(LocalDateTime.now())
                .endDate(LocalDateTime.now().plusDays(30))
                .isActive(true)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
    }

    @Test
    void testCreateVoucher_Success() {
        when(voucherRepository.existsByCode("SALE2024")).thenReturn(false);
        when(voucherMapper.toEntity(voucherDTO)).thenReturn(voucher);
        when(voucherRepository.saveAndFlush(voucher)).thenReturn(savedVoucher);
        when(voucherMapper.toDTO(savedVoucher)).thenReturn(voucherDTO);

        VoucherDTO result = voucherService.createVoucher(voucherDTO);

        assertNotNull(result);
        assertEquals("SALE2024", result.getCode());
        assertEquals("Sale 2024", result.getName());
        verify(voucherRepository).existsByCode("SALE2024");
        verify(voucherRepository).saveAndFlush(voucher);
        verify(voucherMapper).toEntity(voucherDTO);
        verify(voucherMapper).toDTO(savedVoucher);
    }

    @Test
    void testCreateVoucher_DuplicateCode() {
        when(voucherRepository.existsByCode("SALE2024")).thenReturn(true);

        DuplicateVoucherException exception = assertThrows(
                DuplicateVoucherException.class,
                () -> voucherService.createVoucher(voucherDTO)
        );

        assertEquals("Voucher code 'SALE2024' already exists", exception.getMessage());
        verify(voucherRepository).existsByCode("SALE2024");
        verify(voucherRepository, never()).saveAndFlush(any());
        verify(voucherMapper, never()).toEntity(any());
    }

    @Test
    void testGetVoucherByCode_Success() {
        when(voucherRepository.findByCode("SALE2024")).thenReturn(Optional.of(savedVoucher));
        when(voucherMapper.toDTO(savedVoucher)).thenReturn(voucherDTO);

        VoucherDTO result = voucherService.getVoucherByCode("SALE2024");

        assertNotNull(result);
        assertEquals("SALE2024", result.getCode());
        assertEquals("Sale 2024", result.getName());
        verify(voucherRepository).findByCode("SALE2024");
        verify(voucherMapper).toDTO(savedVoucher);
    }

    @Test
    void testGetVoucherByCode_NotFound() {
        when(voucherRepository.findByCode("NONEXISTENT")).thenReturn(Optional.empty());

        VoucherNotFoundException exception = assertThrows(
                VoucherNotFoundException.class,
                () -> voucherService.getVoucherByCode("NONEXISTENT")
        );

        assertEquals("Voucher not found with code: NONEXISTENT", exception.getMessage());
        verify(voucherRepository).findByCode("NONEXISTENT");
        verify(voucherMapper, never()).toDTO(any());
    }
} 