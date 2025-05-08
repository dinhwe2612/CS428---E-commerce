package com.microservice_ecommerce.auth.util;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

public class PasswordEncoderUtil {
    private final BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

    public String encodePassword(String password) {
        return encoder.encode(password);
    }

    public boolean matches(String rawPassword, String encodedPassword) {
        return encoder.matches(rawPassword, encodedPassword);
    }
}
