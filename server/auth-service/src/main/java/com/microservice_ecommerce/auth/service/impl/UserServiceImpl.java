package com.microservice_ecommerce.auth.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.microservice_ecommerce.auth.DTOs.AuthResponse;
import com.microservice_ecommerce.auth.DTOs.ForgotPasswordRequest;
import com.microservice_ecommerce.auth.DTOs.ResetPasswordRequest;
import com.microservice_ecommerce.auth.DTOs.SignInRequest;
import com.microservice_ecommerce.auth.DTOs.SignUpRequest;
import com.microservice_ecommerce.auth.DTOs.UserCreatedMessage;
import com.microservice_ecommerce.auth.exception.ConfirmPasswordDoesNotMatch;
import com.microservice_ecommerce.auth.exception.InvalidEmailException;
import com.microservice_ecommerce.auth.exception.InvalidPasswordException;
import com.microservice_ecommerce.auth.exception.UserAlreadyExistsException;
import com.microservice_ecommerce.auth.model.Role;
import com.microservice_ecommerce.auth.model.User;
import com.microservice_ecommerce.auth.publisher.UserCreatedPublisher;
import com.microservice_ecommerce.auth.repository.UserRepository;
import com.microservice_ecommerce.auth.service.JWTService;
import com.microservice_ecommerce.auth.service.UserService;

@Service



public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    private final PasswordEncoder passwordEncoder;

    private final JWTService jwtService;

    private final AuthenticationManager authenticationManager;

    private final JavaMailSender mailSender;

    private final UserCreatedPublisher userCreatedPublisher;

   @Autowired
    public UserServiceImpl(UserRepository userRepository, PasswordEncoder passwordEncoder, JWTService jwtService, AuthenticationManager authenticationManager, JavaMailSender mailSender, UserCreatedPublisher userCreatedPublisher) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
        this.authenticationManager = authenticationManager;
        this.mailSender = mailSender;
        this.userCreatedPublisher = userCreatedPublisher;
    }

    @Value("${spring.reset-password.url}")
    private String resetPasswordUrl;

    @Value("${spring.mail.username}")
    private String senderEmail;

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

        userRepository.save(user);
        UserCreatedMessage userCreatedMessage = new UserCreatedMessage();
        userCreatedMessage.setId(user.getId());
        userCreatedMessage.setUsername(user.getUsername());
        userCreatedMessage.setEmail(user.getEmail());
        userCreatedMessage.setRole(user.getRole().name());
        userCreatedPublisher.publishUserCreated(userCreatedMessage);

        return user;
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

    @Override
    public void forgotPassword(ForgotPasswordRequest forgotPasswordRequest) {
        String email = forgotPasswordRequest.getEmail();
        if (email == null || email.trim().isEmpty()) {
            throw new IllegalArgumentException("Email cannot be empty");
        }
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with email: " + email));
        String token = jwtService.generateToken(user);
        String resetLink = resetPasswordUrl + "?token=" + token;
        String subject = "Password Reset Request";
        String body = "To reset your password, click the link below:\n" + resetLink;
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(senderEmail);
        message.setTo(email);
        message.setSubject(subject);
        message.setText(body);
        try {
            mailSender.send(message);
        } catch (Exception e) {
            throw new RuntimeException("Failed to send email: " + e.getMessage(), e);
        }
    }

    @Override
    public void resetPassword(ResetPasswordRequest resetPasswordRequest) {
        String token = resetPasswordRequest.getToken();
        String newPassword = resetPasswordRequest.getNewPassword();
        if (token == null || token.trim().isEmpty()) {
            throw new IllegalArgumentException("Token cannot be empty");
        }
        if (newPassword == null || newPassword.trim().isEmpty()) {
            throw new IllegalArgumentException("New password cannot be empty");
        }
        if (!isValidPassword(newPassword)) {
            throw new InvalidPasswordException(
                    "Password must be at least 8 characters long and contain at least one digit, one uppercase letter, one lowercase letter, and one special character");
        }

        String email = jwtService.extractUsername(token);
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with email: " + email));

        if (passwordEncoder.matches(newPassword, user.getPassword())) {
            throw new InvalidPasswordException("New password cannot be same as old password");
        }
        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);
    }
}
