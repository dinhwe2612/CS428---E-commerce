package com.server.user_service.service;

import com.server.user_service.DTOs.UpdateRequest;
import com.server.user_service.DTOs.UserResponse;

public interface UserService {
    void update(Long id, UpdateRequest updateRequest);
    void delete(Long id);
    UserResponse getUserById(Long id);
}
