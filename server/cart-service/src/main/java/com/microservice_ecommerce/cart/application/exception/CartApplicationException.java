package com.microservice_ecommerce.cart.application.exception;

public class CartApplicationException extends RuntimeException {
    public CartApplicationException(String message) {
        super(message);
    }
    
    public CartApplicationException(String message, Throwable cause) {
        super(message, cause);
    }
} 