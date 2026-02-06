package com.lab2.authsystem.dto

data class AuthResponse(
    val message: String,
    val username: String? = null,
    val email: String? = null
)