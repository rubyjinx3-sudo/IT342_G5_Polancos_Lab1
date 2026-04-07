package com.lab2.authsystem.service.auth;

import com.lab2.authsystem.model.User;

public interface UserViewAdapter<T> {
    T adapt(User user);
}
