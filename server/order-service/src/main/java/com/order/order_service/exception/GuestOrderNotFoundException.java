package com.order.order_service.exception;

public class GuestOrderNotFoundException extends RuntimeException {
    public GuestOrderNotFoundException(Long id) {
        super("Guest order not found with id: " + id);
    }
} 