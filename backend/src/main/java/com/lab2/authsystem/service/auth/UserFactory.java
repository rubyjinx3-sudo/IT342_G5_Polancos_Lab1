package com.lab2.authsystem.service.auth;

import com.lab2.authsystem.dto.RegisterRequest;
import com.lab2.authsystem.model.User;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class UserFactory {

    private final PasswordEncoder passwordEncoder;

    public UserFactory(PasswordEncoder passwordEncoder) {
        this.passwordEncoder = passwordEncoder;
    }

    public User createFrom(RegisterRequest request) {
        User user = new User();
        user.setFullName(request.getFullName());
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setRole(resolveRole(request.getRole()));
        return user;
    }

    private User.Role resolveRole(String rawRole) {
        if (rawRole == null || rawRole.isBlank()) {
            return User.Role.STUDENT;
        }

        try {
            return User.Role.valueOf(rawRole.trim().toUpperCase());
        } catch (IllegalArgumentException ex) {
            return User.Role.STUDENT;
        }
    }
}
