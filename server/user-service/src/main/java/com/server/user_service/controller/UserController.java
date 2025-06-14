package com.server.user_service.controller;

import com.server.user_service.DTOs.ApiResponse;
import com.server.user_service.DTOs.UpdateRequest;
import com.server.user_service.DTOs.UserResponse;
import com.server.user_service.model.Role;
import com.server.user_service.service.UserService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.*;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/users")
@Tag(name = "Users", description = "Endpoints for user management and administration")
@RequiredArgsConstructor
@Slf4j
public class UserController {

    private final UserService userService;

    @PostConstruct
    public void init() {
        log.info("UserController initialized");
    }

    @Operation(
            summary = "Update user profile",
            description = "Updates the fields of an existing user",
            responses = {
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "User updated successfully",
                            content = @Content(schema = @Schema(implementation = ApiResponse.class)))
            }
    )
    @PostMapping("/update/{id}")
    @PreAuthorize("hasAuthority('USER')")
    public ResponseEntity<ApiResponse<?>> update(
            @Parameter(description = "ID of the user to update", required = true) @PathVariable("id") Long id,
            @Parameter(description = "Fields to update", required = true) @RequestBody UpdateRequest updateRequest
    ) {
        userService.update(id, updateRequest);
        return ResponseEntity.ok(new ApiResponse<>(true, "User updated successfully", null));
    }

    @Operation(
            summary = "Get user by ID",
            description = "Retrieves a single user by their ID",
            responses = {
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "User found",
                            content = @Content(schema = @Schema(implementation = UserResponse.class)))
            }
    )
    @GetMapping("/user/{id}")
    @PreAuthorize("hasAuthority('USER')")
    public ResponseEntity<ApiResponse<UserResponse>> getUserById(
            @Parameter(description = "ID of the user", required = true) @PathVariable Long id
    ) {
        UserResponse userResponse = userService.getUserById(id);
        return ResponseEntity.ok(new ApiResponse<>(true, "User found successfully", userResponse));
    }

    @Operation(
            summary = "Delete a user",
            description = "Deletes a user by their ID (admin only)",
            responses = {
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "User deleted successfully",
                            content = @Content(schema = @Schema(implementation = ApiResponse.class)))
            }
    )
    @DeleteMapping("/delete/{id}")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<ApiResponse<?>> delete(
            @Parameter(description = "ID of the user to delete", required = true) @PathVariable("id") Long id
    ) {
        userService.delete(id);
        return ResponseEntity.ok(new ApiResponse<>(true, "User deleted successfully", null));
    }

    @Operation(
            summary = "List all users",
            description = "Retrieves all users (admin only)",
            responses = {
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Users retrieved successfully",
                            content = @Content(schema = @Schema(implementation = UserResponse.class)))
            }
    )
    @GetMapping("/admin/all")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<ApiResponse<List<UserResponse>>> getAllUsers() {
        List<UserResponse> users = userService.getAllUsers();
        return ResponseEntity.ok(new ApiResponse<>(true, "Users retrieved successfully", users));
    }

    @Operation(
            summary = "List users by role",
            description = "Retrieves users filtered by role (admin only)",
            responses = {
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Users by role retrieved successfully",
                            content = @Content(schema = @Schema(implementation = UserResponse.class)))
            }
    )
    @GetMapping("/admin/role/{role}")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<ApiResponse<List<UserResponse>>> getUsersByRole(
            @Parameter(description = "Role to filter by", required = true) @PathVariable Role role
    ) {
        List<UserResponse> users = userService.getUsersByRole(role);
        return ResponseEntity.ok(new ApiResponse<>(true, "Users by role retrieved successfully", users));
    }

    @Operation(
            summary = "Update user role",
            description = "Updates the role of a user (admin only)",
            responses = {
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "User role updated successfully",
                            content = @Content(schema = @Schema(implementation = ApiResponse.class)))
            }
    )
    @PutMapping("/admin/role/{id}")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<ApiResponse<?>> updateUserRole(
            @Parameter(description = "ID of the user", required = true) @PathVariable("id") Long id,
            @Parameter(description = "New role", required = true) @RequestParam Role role
    ) {
        userService.updateUserRole(id, role);
        return ResponseEntity.ok(new ApiResponse<>(true, "User role updated successfully", null));
    }

    @Operation(
            summary = "Test protected endpoint",
            description = "A sample endpoint to verify authentication"
    )
    @GetMapping("/test")
    public ResponseEntity<ApiResponse<String>> testProtectedEndpoint() {
        ApiResponse<String> response = new ApiResponse<>(true, "Protected endpoint accessed successfully",
                "This is a protected endpoint");
        return ResponseEntity.ok(response);
    }
}
