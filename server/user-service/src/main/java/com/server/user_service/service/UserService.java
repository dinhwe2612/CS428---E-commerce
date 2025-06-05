package com.server.user_service.service;

import com.server.user_service.DTOs.UpdateRequest;
import com.server.user_service.DTOs.UserResponse;
import com.server.user_service.model.Role;
import com.server.user_service.model.User;

import java.util.List;

public interface UserService {
    void update(Long id, UpdateRequest updateRequest);
    void delete(Long id);
    UserResponse getUserById(Long id);
    void createUser(User user);
    List<UserResponse> getAllUsers();
    List<UserResponse> getUsersByRole(Role role);
    void updateUserRole(Long id, Role role);
}
