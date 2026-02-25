package com.lab2.authsystem.dto;

public class AuthResponse {

    private String message;
    private String fullName;
    private String email;
    private String role;
    private Long   userId;

    public AuthResponse(String message, String fullName, String email, String role) {
        this.message  = message;
        this.fullName = fullName;
        this.email    = email;
        this.role     = role;
    }

    public AuthResponse(String message, String fullName, String email, String role, Long userId) {
        this(message, fullName, email, role);
        this.userId = userId;
    }

    public String getMessage()              { return message; }
    public void setMessage(String message)  { this.message = message; }

    public String getFullName()              { return fullName; }
    public void setFullName(String fullName) { this.fullName = fullName; }

    public String getEmail()               { return email; }
    public void setEmail(String email)     { this.email = email; }

    public String getRole()              { return role; }
    public void setRole(String role)     { this.role = role; }

    public Long getUserId()               { return userId; }
    public void setUserId(Long userId)    { this.userId = userId; }
}