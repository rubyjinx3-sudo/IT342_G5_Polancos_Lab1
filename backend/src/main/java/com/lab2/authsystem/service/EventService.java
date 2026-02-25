package com.lab2.authsystem.service;

import com.lab2.authsystem.model.Event;
import com.lab2.authsystem.model.Registration;
import com.lab2.authsystem.model.User;
import com.lab2.authsystem.repository.EventRepository;
import com.lab2.authsystem.repository.RegistrationRepository;
import com.lab2.authsystem.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class EventService {

    @Autowired
    private EventRepository eventRepository;

    @Autowired
    private RegistrationRepository registrationRepository;

    @Autowired
    private UserRepository userRepository;

    // ── EVENTS ────────────────────────────────────────────

    public List<Event> getAllEvents() {
        return eventRepository.findAll();
    }

    public Optional<Event> getEventById(Long id) {
        return eventRepository.findById(id);
    }

    public List<Event> getEventsByOrganizer(Long organizerId) {
        return eventRepository.findByOrganizerId(organizerId);
    }

    public Event createEvent(Event event) {
        return eventRepository.save(event);
    }

    // ── REGISTRATIONS ─────────────────────────────────────

    public Registration registerForEvent(Long userId, Long eventId) {
        // Check if already registered
        if (registrationRepository.existsByUserIdAndEventId(userId, eventId)) {
            throw new RuntimeException("Already registered for this event");
        }

        Registration reg = new Registration();
        reg.setUserId(userId);
        reg.setEventId(eventId);
        return registrationRepository.save(reg);
    }

    public List<Registration> getRegistrationsByUser(Long userId) {
        return registrationRepository.findByUserId(userId);
    }

    public List<Registration> getRegistrationsByEvent(Long eventId) {
        // Fetch registrations and enrich with student info
        List<Registration> regs = registrationRepository.findByEventId(eventId);
        for (Registration reg : regs) {
            userRepository.findById(reg.getUserId()).ifPresent(user -> {
                reg.setStudentName(user.getFullName());
                reg.setStudentEmail(user.getEmail());
            });
        }
        return regs;
    }

    public boolean isRegistered(Long userId, Long eventId) {
        return registrationRepository.existsByUserIdAndEventId(userId, eventId);
    }
}