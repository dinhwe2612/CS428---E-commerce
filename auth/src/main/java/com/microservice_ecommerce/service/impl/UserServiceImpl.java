package com.microservice_ecommerce.service.impl;

import com.microservice_ecommerce.service.UserService;
import com.microservice_ecommerce.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service    
public class UserServiceImpl implements UserService {

    @Autowired
    private UserRepository userRepository;
    
}
