package com.lab2.authsystem.service.event;

import com.lab2.authsystem.model.Registration;
import org.springframework.stereotype.Component;

import java.util.List;

@Component("baseRegistrationView")
public class BaseRegistrationView implements RegistrationView {

    @Override
    public List<Registration> build(List<Registration> registrations) {
        return registrations;
    }
}
