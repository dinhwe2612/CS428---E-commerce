package com.server.user_service.controller;

import com.server.user_service.DTOs.ApiResponse;
import com.server.user_service.DTOs.UpdateRequest;
import com.server.user_service.DTOs.UserResponse;
import com.server.user_service.model.Role;
import com.server.user_service.service.UserService;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
@Slf4j
public class UserController {
    private final UserService userService;

    @PostConstruct
    public void init() {
        log.info("UserController initialized");
    }

    @PostMapping("/update/{id}")
    @PreAuthorize("hasAuthority('USER')")
    public ResponseEntity<ApiResponse<?>> update(@PathVariable("id") Long id, @RequestBody UpdateRequest updateRequest) {
        userService.update(id, updateRequest);
        return ResponseEntity.ok(new ApiResponse<>(true, "User updated successfully", null));
    }

    @GetMapping("/user/{id}")
    @PreAuthorize("hasAuthority('USER')")
    public ResponseEntity<ApiResponse<UserResponse>> getUserById(@PathVariable Long id) {
        UserResponse userResponse = userService.getUserById(id);
        return ResponseEntity.ok(new ApiResponse<>(true, "User found successfully", userResponse));
    }

    @DeleteMapping("/delete/{id}")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<ApiResponse<?>> delete(@PathVariable("id") Long id) {
        userService.delete(id);
        return ResponseEntity.ok(new ApiResponse<>(true, "User deleted successfully", null));
    }

    @GetMapping("/admin/all")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<ApiResponse<List<UserResponse>>> getAllUsers() {
        List<UserResponse> users = userService.getAllUsers();
        return ResponseEntity.ok(new ApiResponse<>(true, "Users retrieved successfully", users));
    }

    @GetMapping("/admin/role/{role}")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<ApiResponse<List<UserResponse>>> getUsersByRole(@PathVariable Role role) {
        List<UserResponse> users = userService.getUsersByRole(role);
        return ResponseEntity.ok(new ApiResponse<>(true, "Users by role retrieved successfully", users));
    }

    @PutMapping("/admin/role/{id}")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<ApiResponse<?>> updateUserRole(@PathVariable("id") Long id, @RequestParam Role role) {
        userService.updateUserRole(id, role);
        return ResponseEntity.ok(new ApiResponse<>(true, "User role updated successfully", null));
    }

    @GetMapping("/test")
    public ResponseEntity<ApiResponse<String>> testProtectedEndpoint() {
        ApiResponse<String> response = new ApiResponse<>(true, "Protected endpoint accessed successfully",
                "This is a protected endpoint");
        return ResponseEntity.ok(response);
    }
}
