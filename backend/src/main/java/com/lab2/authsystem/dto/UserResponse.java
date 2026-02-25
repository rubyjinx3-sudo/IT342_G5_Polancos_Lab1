package com.lab2.authsystem.dto;

import java.time.LocalDateTime;

public class UserResponse {

    private Long userId;
    private String fullName;
    private String email;
    private String role;
    private LocalDateTime createdAt;
    private LocalDateTime lastLogin;

    public UserResponse(Long userId, String fullName, String email, String role,
                        LocalDateTime createdAt, LocalDateTime lastLogin) {
        this.userId    = userId;
        this.fullName  = fullName;
        this.email     = email;
        this.role      = role;
        this.createdAt = createdAt;
        this.lastLogin = lastLogin;
    }

    // ── Getters & Setters ──────────────────────────────────

    public Long getUserId()               { return userId; }
    public void setUserId(Long userId)    { this.userId = userId; }

    public String getFullName()              { return fullName; }
    public void setFullName(String fullName) { this.fullName = fullName; }

    public String getEmail()               { return email; }
    public void setEmail(String email)     { this.email = email; }

    public String getRole()              { return role; }
    public void setRole(String role)     { this.role = role; }

    public LocalDateTime getCreatedAt()                    { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt)      { this.createdAt = createdAt; }

    public LocalDateTime getLastLogin()                    { return lastLogin; }
    public void setLastLogin(LocalDateTime lastLogin)      { this.lastLogin = lastLogin; }
}