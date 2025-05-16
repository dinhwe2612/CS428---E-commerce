package com.server.user_service.DTOs;

import lombok.Data;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@RequiredArgsConstructor
public class UpdateRequest {
    private String username;
    private String email;
    private String password;
    private String avatar_url;
    private String full_name;
    private String phone_number;
    private String address;
}
