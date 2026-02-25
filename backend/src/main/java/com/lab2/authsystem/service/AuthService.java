package com.lab2.authsystem.service;

import com.lab2.authsystem.dto.*;
import com.lab2.authsystem.model.User;
import com.lab2.authsystem.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
public class AuthService {

    @Autowired
    private UserRepository userRepository;

    private BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    // ── REGISTER ──────────────────────────────────────────
    public AuthResponse register(RegisterRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("Email already exists");
        }

        User user = new User();
        user.setFullName(request.getFullName());
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));

        try {
            user.setRole(User.Role.valueOf(request.getRole().toUpperCase()));
        } catch (Exception e) {
            user.setRole(User.Role.STUDENT);
        }

        userRepository.save(user);

        return new AuthResponse(
            "Registration successful",
            user.getFullName(),
            user.getEmail(),
            user.getRole().name(),
            user.getUserId()
        );
    }

    // ── LOGIN ─────────────────────────────────────────────
    public AuthResponse login(LoginRequest request) {
        Optional<User> optUser = userRepository.findByEmail(request.getEmail());

        if (optUser.isEmpty()) {
            throw new RuntimeException("Invalid email or password");
        }

        User user = optUser.get();

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new RuntimeException("Invalid email or password");
        }

        user.setLastLogin(LocalDateTime.now());
        userRepository.save(user);

        return new AuthResponse(
            "Login successful",
            user.getFullName(),
            user.getEmail(),
            user.getRole().name(),
            user.getUserId()
        );
    }

    // ── GET USER BY EMAIL ─────────────────────────────────
    public UserResponse getUserByEmail(String email) {
        User user = userRepository.findByEmail(email)
            .orElseThrow(() -> new RuntimeException("User not found"));

        return new UserResponse(
            user.getUserId(),
            user.getFullName(),
            user.getEmail(),
            user.getRole().name(),
            user.getCreatedAt(),
            user.getLastLogin()
        );
    }

    // ── UPDATE PROFILE ────────────────────────────────────
    public void updateProfile(Long userId, String fullName,
                              String currentPassword, String newPassword) {
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new RuntimeException("User not found"));

        // Update full name
        if (fullName != null && !fullName.isBlank()) {
            user.setFullName(fullName);
        }

        // Change password if provided
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