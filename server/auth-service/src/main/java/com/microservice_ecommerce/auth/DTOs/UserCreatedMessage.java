package com.microservice_ecommerce.auth.DTOs;

import lombok.Data;

@Data
public class UserCreatedMessage {
    private Long id;
    private String username;
    private String email;
    private String role;
    private String password;
    
}
