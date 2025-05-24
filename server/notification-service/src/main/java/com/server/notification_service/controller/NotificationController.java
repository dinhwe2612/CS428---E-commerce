package com.server.notification_service.controller;

import com.server.notification_service.DTOs.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/notification")
@RequiredArgsConstructor
public class NotificationController {

    @GetMapping("/test")
    @PreAuthorize("hasAuthority('USER')")
    public ApiResponse<String> test() {
        return new ApiResponse<>(true, "test successfully", "you are authorized");
    }

    @GetMapping("/user/{username}")
    @PreAuthorize("hasAuthority('ADMIN') or #username == authentication.name")
    public ApiResponse<String> getUser(@PathVariable String username) {
        return new ApiResponse<>(true, "get user successfully", username);
    }
}