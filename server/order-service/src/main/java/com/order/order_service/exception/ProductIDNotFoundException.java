package com.order.order_service.exception;

public class ProductIDNotFoundException extends RuntimeException {
    public ProductIDNotFoundException(String message) {
        super(message);
    }
}
