package com.lab2.authsystem.service.event;

import com.lab2.authsystem.model.Registration;

import java.util.List;

public abstract class RegistrationViewDecorator implements RegistrationView {

    private final RegistrationView delegate;

    protected RegistrationViewDecorator(RegistrationView delegate) {
        this.delegate = delegate;
    }

    @Override
    public List<Registration> build(List<Registration> registrations) {
        return delegate.build(registrations);
    }
}
