package com.microservice_ecommerce.voucher.application.exception;

public class VoucherApplicationException extends RuntimeException {
    public VoucherApplicationException(String message) {
        super(message);
    }
    
    public VoucherApplicationException(String message, Throwable cause) {
        super(message, cause);
    }
} 