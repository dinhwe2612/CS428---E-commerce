package com.microservice_ecommerce.auth.service.impl;

import com.microservice_ecommerce.auth.DTOs.AuthResponse;
import com.microservice_ecommerce.auth.DTOs.SignInRequest;
import com.microservice_ecommerce.auth.DTOs.SignUpRequest;
import com.microservice_ecommerce.auth.exception.ConfirmPasswordDoesNotMatch;
import com.microservice_ecommerce.auth.exception.InvalidEmailException;
import com.microservice_ecommerce.auth.exception.InvalidPasswordException;
import com.microservice_ecommerce.auth.exception.UserAlreadyExistsException;
import com.microservice_ecommerce.auth.model.Role;
import com.microservice_ecommerce.auth.model.User;
import com.microservice_ecommerce.auth.repository.UserRepository;
import com.microservice_ecommerce.auth.service.JWTService;
import com.microservice_ecommerce.auth.service.UserService;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
@NoArgsConstructor
public class UserServiceImpl implements UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JWTService jwtService;

    @Autowired
    private AuthenticationManager authenticationManager;

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
        try {
            String username = signUpRequest.getUsername();
            String email = signUpRequest.getEmail();
            String password = signUpRequest.getPassword();
            String confirmPassword = signUpRequest.getConfirmPassword();

            if (username == null || username.trim().isEmpty()) {
                throw new IllegalArgumentException("Username cannot be empty");
            }

            if (email == null || email.trim().isEmpty()) {
                throw new IllegalArgumentException("Email cannot be empty");
            }

            if (password == null || password.trim().isEmpty()) {
                throw new IllegalArgumentException("Password cannot be empty");
            }

            if (!password.equals(confirmPassword)) {
                throw new ConfirmPasswordDoesNotMatch("Password and confirm password do not match");
            }

            if (userRepository.findByUsername(username).isPresent()) {
                throw new UserAlreadyExistsException("Username already exists");
            }

            if (userRepository.findByEmail(email).isPresent()) {
                throw new UserAlreadyExistsException("Email already exists");
            }

            if (!isValidEmail(email)) {
                throw new InvalidEmailException("Invalid email format");
            }

            if (!isValidPassword(password)) {
                throw new InvalidPasswordException(
                        "Password must be at least 8 characters long and contain at least one digit, one uppercase letter, one lowercase letter, and one special character");
            }

            User user = new User();
            user.setUsername(username);
            user.setEmail(email);
            user.setPassword(passwordEncoder.encode(password));
            user.setRole(Role.USER);
            user.setFull_name(username);
            user.setAvatar_url("");
            user.setPhone_number("");
            user.setAddress("");

            return userRepository.save(user);
        } catch (Exception e) {
            throw new RuntimeException("Error creating user: " + e.getMessage(), e);
        }
    }

    @Override
    public AuthResponse signin(SignInRequest signInRequest) {
        try {
            if (signInRequest.getEmail() == null || signInRequest.getEmail().trim().isEmpty()) {
                throw new IllegalArgumentException("Email cannot be empty");
            }
            if (signInRequest.getPassword() == null || signInRequest.getPassword().trim().isEmpty()) {
                throw new IllegalArgumentException("Password cannot be empty");
            }

            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            signInRequest.getEmail(),
                            signInRequest.getPassword()));

            if (authentication.isAuthenticated()) {
                User user = (User) authentication.getPrincipal();
                String token = jwtService.generateToken(user);

                return AuthResponse.builder()
                        .token(token)
                        .username(user.getUsername())
                        .build();
            } else {
                throw new BadCredentialsException("Invalid email or password");
            }
        } catch (BadCredentialsException e) {
            throw new BadCredentialsException("Invalid email or password");
        } catch (Exception e) {
            throw new RuntimeException("Authentication failed: " + e.getMessage(), e);
        }
    }
}
