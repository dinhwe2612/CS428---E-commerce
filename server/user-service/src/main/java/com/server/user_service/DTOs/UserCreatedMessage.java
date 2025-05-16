    package com.server.user_service.DTOs;

import lombok.Data;

@Data
public class UserCreatedMessage {
    private Long id;
    private String username;
    private String email;
    private String role;
    private String password;
}
