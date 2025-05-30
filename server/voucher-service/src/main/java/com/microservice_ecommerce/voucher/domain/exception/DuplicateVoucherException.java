package com.microservice_ecommerce.voucher.domain.exception;

public class DuplicateVoucherException extends RuntimeException {
    public DuplicateVoucherException(String message) {
        super(message);
    }
} 