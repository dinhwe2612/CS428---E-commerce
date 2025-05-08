package com.microservice_ecommerce.auth.service;

import com.microservice_ecommerce.auth.DTOs.AuthResponse;
import com.microservice_ecommerce.auth.DTOs.SignInRequest;
import com.microservice_ecommerce.auth.DTOs.SignUpRequest;
import com.microservice_ecommerce.auth.model.User;
import org.springframework.stereotype.Service;

@Service
public interface UserService {
    User createUser(SignUpRequest signUpRequest);

    AuthResponse signin(SignInRequest signInRequest);
}
