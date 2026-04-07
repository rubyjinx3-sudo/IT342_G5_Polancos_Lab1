package com.lab2.authsystem.service.auth;

import com.lab2.authsystem.dto.RegisterRequest;
import com.lab2.authsystem.model.User;
import org.junit.jupiter.api.Test;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

class UserFactoryTest {

    private final PasswordEncoder passwordEncoder = new PasswordEncoder() {
        @Override
        public String encode(CharSequence rawPassword) {
            return "encoded-" + rawPassword;
        }

        @Override
        public boolean matches(CharSequence rawPassword, String encodedPassword) {
            return encodedPassword.equals(encode(rawPassword));
        }
    };

    private final UserFactory userFactory = new UserFactory(passwordEncoder);

    @Test
    void createFromDefaultsToStudentWhenRoleIsInvalid() {
        RegisterRequest request = new RegisterRequest();
        request.setFullName("Jane Doe");
        request.setEmail("jane@example.com");
        request.setPassword("secret");
        request.setRole("invalid-role");

        User user = userFactory.createFrom(request);

        assertEquals("Jane Doe", user.getFullName());
        assertEquals("jane@example.com", user.getEmail());
        assertEquals(User.Role.STUDENT, user.getRole());
        assertNotEquals("secret", user.getPassword());
        assertEquals("encoded-secret", user.getPassword());
    }
}
