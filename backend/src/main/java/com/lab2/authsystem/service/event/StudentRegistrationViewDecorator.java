package com.lab2.authsystem.service.event;

import com.lab2.authsystem.model.Registration;
import com.lab2.authsystem.repository.UserRepository;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;

import java.util.List;

@Primary
@Component
public class StudentRegistrationViewDecorator extends RegistrationViewDecorator {

    private final UserRepository userRepository;

    public StudentRegistrationViewDecorator(
            @Qualifier("baseRegistrationView") RegistrationView delegate,
            UserRepository userRepository) {
        super(delegate);
        this.userRepository = userRepository;
    }

    @Override
    public List<Registration> build(List<Registration> registrations) {
        List<Registration> enrichedRegistrations = super.build(registrations);

        for (Registration registration : enrichedRegistrations) {
            userRepository.findById(registration.getUserId()).ifPresent(user -> {
                registration.setStudentName(user.getFullName());
                registration.setStudentEmail(user.getEmail());
            });
        }

        return enrichedRegistrations;
    }
}
