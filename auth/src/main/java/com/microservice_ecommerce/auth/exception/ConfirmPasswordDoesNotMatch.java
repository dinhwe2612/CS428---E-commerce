package com.microservice_ecommerce.auth.exception;

public class ConfirmPasswordDoesNotMatch extends RuntimeException {
    public ConfirmPasswordDoesNotMatch(String message) {
        super(message);
    }
}
