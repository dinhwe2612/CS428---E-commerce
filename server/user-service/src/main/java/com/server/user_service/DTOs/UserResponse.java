package com.server.user_service.DTOs;

import com.server.user_service.model.Role;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@RequiredArgsConstructor
public class UserResponse {
    private Long id;
    private String username;
    private String email;
    private String password;
    private String avatar_url;
    private String full_name;
    private String phone_number;
    private String address;
    @Enumerated(EnumType.STRING)
    private Role role;
}
