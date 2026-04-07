package com.lab2.authsystem.service.event;

import com.lab2.authsystem.model.Registration;

import java.util.List;

public interface RegistrationView {
    List<Registration> build(List<Registration> registrations);
}
