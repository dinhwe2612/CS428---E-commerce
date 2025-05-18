package com.microservice_ecommerce.cart.domain.exception;

public class ProductUnavailableException extends RuntimeException {
    
    public ProductUnavailableException(String message) {
        super(message);
    }
    
    public ProductUnavailableException(String message, Throwable cause) {
        super(message, cause);
    }
} 