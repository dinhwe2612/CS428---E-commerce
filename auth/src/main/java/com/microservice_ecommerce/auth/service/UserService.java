package com.microservice_ecommerce.auth.service;

import com.microservice_ecommerce.auth.model.User;
import org.springframework.stereotype.Service;

@Service
public interface UserService {
    User createUser(User user);
}
