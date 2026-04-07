package com.lab2.authsystem.service.auth;

import com.lab2.authsystem.dto.UserResponse;
import com.lab2.authsystem.model.User;
import org.springframework.stereotype.Component;

@Component
public class UserProfileAdapter implements UserViewAdapter<UserResponse> {

    @Override
    public UserResponse adapt(User user) {
        return new UserResponse(
            user.getUserId(),
            user.getFullName(),
            user.getEmail(),
            user.getRole().name(),
            user.getCreatedAt(),
            user.getLastLogin()
        );
    }
}
