package com.microservice_ecommerce.voucher.domain.exception;

public class VoucherInvalidException extends RuntimeException {
    public VoucherInvalidException(String message) {
        super(message);
    }
} 