package com.microservice_ecommerce.auth.controller;

import com.microservice_ecommerce.auth.DTOs.ApiResponse;
import com.microservice_ecommerce.auth.DTOs.SignUpRequest;
import com.microservice_ecommerce.auth.model.User;
import com.microservice_ecommerce.auth.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/auth")
public class UserController {

    @Autowired
    private UserService userService;

    @PostMapping("/signup")
    public ResponseEntity<ApiResponse<User>> signup(@RequestBody SignUpRequest signUpRequest) {
        User user = userService.createUser(signUpRequest);
        ApiResponse<User> response = new ApiResponse<>(true, "User registered successfully", user);
        return ResponseEntity.ok(response);
    }

}
