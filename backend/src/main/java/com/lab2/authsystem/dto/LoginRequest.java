package com.lab2.authsystem.dto;

import jakarta.validation.constraints.NotBlank;

public class LoginRequest {

    @NotBlank(message = "Email is required")
    private String email;

    @NotBlank(message = "Password is required")
    private String password;

    // Optional: used to validate role matches
    private String role;

    public LoginRequest() {}

    public String getEmail()               { return email; }
    public void setEmail(String email)     { this.email = email; }

    public String getPassword()               { return password; }
    public void setPassword(String password)  { this.password = password; }

    public String getRole()            { return role; }
    public void setRole(String role)   { this.role = role; }
}