package com.lab2.authsystem.service.auth;

import com.lab2.authsystem.dto.AuthResponse;
import com.lab2.authsystem.model.User;
import org.springframework.stereotype.Component;

@Component
public class AuthResponseAdapter implements UserViewAdapter<AuthResponse> {

    @Override
    public AuthResponse adapt(User user) {
        return new AuthResponse(
            "Success",
            user.getFullName(),
            user.getEmail(),
            user.getRole().name(),
            user.getUserId()
        );
    }

    public AuthResponse adapt(User user, String message) {
        AuthResponse response = adapt(user);
        response.setMessage(message);
        return response;
    }
}
