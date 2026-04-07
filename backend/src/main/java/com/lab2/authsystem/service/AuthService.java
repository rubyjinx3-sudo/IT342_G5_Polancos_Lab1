package com.lab2.authsystem.service;

import com.lab2.authsystem.dto.AuthResponse;
import com.lab2.authsystem.dto.LoginRequest;
import com.lab2.authsystem.dto.RegisterRequest;
import com.lab2.authsystem.dto.UserResponse;
import com.lab2.authsystem.model.User;
import com.lab2.authsystem.repository.UserRepository;
import com.lab2.authsystem.service.auth.AuthResponseAdapter;
import com.lab2.authsystem.service.auth.UserFactory;
import com.lab2.authsystem.service.auth.UserProfileAdapter;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final UserFactory userFactory;
    private final AuthResponseAdapter authResponseAdapter;
    private final UserProfileAdapter userProfileAdapter;

    public AuthService(
            UserRepository userRepository,
            PasswordEncoder passwordEncoder,
            UserFactory userFactory,
            AuthResponseAdapter authResponseAdapter,
            UserProfileAdapter userProfileAdapter) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.userFactory = userFactory;
        this.authResponseAdapter = authResponseAdapter;
        this.userProfileAdapter = userProfileAdapter;
    }

    public AuthResponse register(RegisterRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("Email already exists");
        }

        User user = userFactory.createFrom(request);
        userRepository.save(user);
        return authResponseAdapter.adapt(user, "Registration successful");
    }

    public AuthResponse login(LoginRequest request) {
        User user = userRepository.findByEmail(request.getEmail())
            .orElseThrow(() -> new RuntimeException("Invalid email or password"));

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new RuntimeException("Invalid email or password");
        }

        user.setLastLogin(LocalDateTime.now());
        userRepository.save(user);
        return authResponseAdapter.adapt(user, "Login successful");
    }

    public UserResponse getUserByEmail(String email) {
        User user = userRepository.findByEmail(email)
            .orElseThrow(() -> new RuntimeException("User not found"));

        return userProfileAdapter.adapt(user);
    }

    public void updateProfile(Long userId, String fullName,
                              String currentPassword, String newPassword) {
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new RuntimeException("User not found"));

        if (fullName != null && !fullName.isBlank()) {
            user.setFullName(fullName);
        }

        if (newPassword != null && !newPassword.isBlank()) {
            if (currentPassword == null ||
                !passwordEncoder.matches(currentPassword, user.getPassword())) {
                throw new RuntimeException("Current password is incorrect");
            }
            user.setPassword(passwordEncoder.encode(newPassword));
        }

        userRepository.save(user);
    }
}
