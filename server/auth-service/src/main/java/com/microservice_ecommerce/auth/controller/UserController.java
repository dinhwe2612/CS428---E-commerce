package com.microservice_ecommerce.auth.controller;

import com.microservice_ecommerce.auth.DTOs.ApiResponse;
import com.microservice_ecommerce.auth.DTOs.AuthResponse;
import com.microservice_ecommerce.auth.DTOs.ForgotPasswordRequest;
import com.microservice_ecommerce.auth.DTOs.ResetPasswordRequest;
import com.microservice_ecommerce.auth.DTOs.SignInRequest;
import com.microservice_ecommerce.auth.DTOs.SignUpRequest;
import com.microservice_ecommerce.auth.model.User;
import com.microservice_ecommerce.auth.service.UserService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/auth")
@Tag(name = "Authentication", description = "Endpoints for user signup, signin, and password management")
public class UserController {

    @Autowired
    private UserService userService;

    @PostMapping("/signup")
    @Operation(summary = "Register a new user",
            description = "Creates a new user account with the provided signup details")
    public ResponseEntity<ApiResponse<User>> signup(
            @RequestBody SignUpRequest signUpRequest
    ) {
        User user = userService.createUser(signUpRequest);
        ApiResponse<User> response = new ApiResponse<>(true, "User registered successfully", user);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/signin")
    @Operation(summary = "Authenticate a user",
            description = "Validates credentials and returns a JWT auth token")
    public ResponseEntity<ApiResponse<AuthResponse>> signin(
            @RequestBody SignInRequest signInRequest
    ) {
        AuthResponse authResponse = userService.signin(signInRequest);
        ApiResponse<AuthResponse> response = new ApiResponse<>(true, "Sign in successful", authResponse);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/forgot-password")
    @Operation(summary = "Request password reset link",
            description = "Sends a password reset link to the user's email")
    public ResponseEntity<ApiResponse<String>> forgotPassword(
            @RequestBody ForgotPasswordRequest forgotPasswordRequest
    ) {
        userService.forgotPassword(forgotPasswordRequest);
        ApiResponse<String> response = new ApiResponse<>(true, "Password reset link sent to email", null);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/reset-password")
    @Operation(summary = "Reset user password",
            description = "Resets the password using the token and new password provided")
    public ResponseEntity<ApiResponse<String>> resetPassword(
            @RequestBody ResetPasswordRequest resetPasswordRequest
    ) {
        userService.resetPassword(resetPasswordRequest);
        ApiResponse<String> response = new ApiResponse<>(true, "Password reset successfully", null);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/test")
    @Operation(summary = "Test protected endpoint",
            description = "Verifies access to an endpoint secured by JWT")
    public ResponseEntity<ApiResponse<String>> testProtectedEndpoint() {
        ApiResponse<String> response = new ApiResponse<>(
                true,
                "Protected endpoint accessed successfully",
                "This is a protected endpoint"
        );
        return ResponseEntity.ok(response);
    }
}
