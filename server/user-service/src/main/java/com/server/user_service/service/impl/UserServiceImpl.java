package com.server.user_service.service.impl;

import com.server.user_service.DTOs.UpdateRequest;
import com.server.user_service.DTOs.UserResponse;
import com.server.user_service.model.User;
import com.server.user_service.repository.UserRepository;
import com.server.user_service.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    @Override
    public void update(Long id, UpdateRequest updateRequest) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("User not found with id: " + id));
        user.setAvatar_url(updateRequest.getAvatar_url());
        user.setPassword(updateRequest.getPassword());
        user.setAddress(updateRequest.getAddress());
        user.setEmail(updateRequest.getEmail());
        user.setPhone_number(updateRequest.getPhone_number());
        user.setFull_name(updateRequest.getFull_name());
        userRepository.save(user);
    }

    @Override
    public void delete(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("User not found with id: " + id));
        userRepository.delete(user);
    }

    @Override
    public UserResponse getUserById(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("User not found with id: " + id));
        UserResponse userResponse = new UserResponse();
        userResponse.setId(user.getId());
        userResponse.setAvatar_url(user.getAvatar_url());
        userResponse.setEmail(user.getEmail());
        userResponse.setFull_name(user.getFull_name());
        userResponse.setPhone_number(user.getPhone_number());
        userResponse.setAddress(user.getAddress());
        userResponse.setRole(user.getRole());
        userResponse.setUsername(user.getUsername());
        return userResponse;
    }
}
