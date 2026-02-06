package com.lab2.authsystem.dto;

public class AuthResponse {
    
    private String message;
    private String username;
    private String email;
    
    // Constructors
    public AuthResponse() {}
    
    public AuthResponse(String message) {
        this.message = message;
    }
    
    public AuthResponse(String message, String username, String email) {
        this.message = message;
        this.username = username;
        this.email = email;
    }
    
    // Getters and Setters
    public String getMessage() {
        return message;
    }
    
    public void setMessage(String message) {
        this.message = message;
    }
    
    public String getUsername() {
        return username;
    }
    
    public void setUsername(String username) {
        this.username = username;
    }
    
    public String getEmail() {
        return email;
    }
    
    public void setEmail(String email) {
        this.email = email;
    }
}