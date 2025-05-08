package com.microservice_ecommerce.auth.service.impl;

import com.microservice_ecommerce.auth.DTOs.SignUpRequest;
import com.microservice_ecommerce.auth.exception.ConfirmPasswordDoesNotMatch;
import com.microservice_ecommerce.auth.exception.InvalidEmailException;
import com.microservice_ecommerce.auth.exception.InvalidPasswordException;
import com.microservice_ecommerce.auth.exception.UserAlreadyExistsException;
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
    private static final String EMAIL_REGEX = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$";
    private static final String PASSWORD_REGEX = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=])(?=\\S+$).{8,}$";

    boolean isValidEmail(String email) {
        return email.matches(EMAIL_REGEX);
    }

    boolean isValidPassword(String password) {
        return password.matches(PASSWORD_REGEX);
    }

    @Override
    public User createUser(SignUpRequest signUpRequest) {
        String username = signUpRequest.getUsername();
        String email = signUpRequest.getEmail();
        String password = signUpRequest.getPassword();
        String confirmPassword = signUpRequest.getConfirmPassword();
        // validate email and poassword format tooo
        if (!password.equals(confirmPassword)) {
            throw new ConfirmPasswordDoesNotMatch("Password and confirm password do not match");
        }

        if (userRepository.findByUsername(username).isPresent()) {
            throw new UserAlreadyExistsException("User already exists");
        }

        if (!isValidEmail(email)) {
            throw new InvalidEmailException("Invalid email format");
        }

        if (!isValidPassword(password)) {
            throw new InvalidPasswordException("Invalid password format");
        }

        User user = new User();
        user.setUsername(username);
        user.setEmail(email);
        user.setPassword(password);

        return userRepository.save(user);

    }
}
