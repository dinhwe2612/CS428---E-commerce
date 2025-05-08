package com.microservice_ecommerce.auth.service.impl;

import com.microservice_ecommerce.auth.model.User;
import com.microservice_ecommerce.auth.repository.UserRepository;
import com.microservice_ecommerce.auth.service.UserService;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service    
@AllArgsConstructor
@NoArgsConstructor
public class UserServiceImpl implements UserService {

    @Autowired
    private UserRepository userRepository;

    @Override
    public User createUser(User user) {
        return userRepository.save(user);
    }
}
