package com.lab2.authsystem.service;

import com.lab2.authsystem.model.Event;
import com.lab2.authsystem.model.Registration;
import com.lab2.authsystem.model.User;
import com.lab2.authsystem.repository.EventRepository;
import com.lab2.authsystem.repository.RegistrationRepository;
import com.lab2.authsystem.repository.UserRepository;
import com.lab2.authsystem.service.event.EventMetadataService;
import com.lab2.authsystem.service.event.EventValidationService;
import com.lab2.authsystem.service.event.RegistrationView;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class EventService {

    private final EventRepository eventRepository;
    private final RegistrationRepository registrationRepository;
    private final UserRepository userRepository;
    private final EventMetadataService eventMetadataService;
    private final EventValidationService eventValidationService;
    private final RegistrationView registrationView;

    public EventService(
            EventRepository eventRepository,
            RegistrationRepository registrationRepository,
            UserRepository userRepository,
            EventMetadataService eventMetadataService,
            EventValidationService eventValidationService,
            RegistrationView registrationView) {
        this.eventRepository = eventRepository;
        this.registrationRepository = registrationRepository;
        this.userRepository = userRepository;
        this.eventMetadataService = eventMetadataService;
        this.eventValidationService = eventValidationService;
        this.registrationView = registrationView;
    }

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
        eventValidationService.validateForSave(event);
        eventMetadataService.normalize(event);
        return eventRepository.save(event);
    }

    public Event updateEvent(Long id, Event eventDetails) {
        Event existing = eventRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Event not found"));

        existing.setTitle(eventDetails.getTitle());
        existing.setDescription(eventDetails.getDescription());
        existing.setDate(eventDetails.getDate());
        existing.setTime(eventDetails.getTime());
        existing.setEndTime(eventDetails.getEndTime());
        existing.setLocation(eventDetails.getLocation());
        existing.setCategory(eventDetails.getCategory());
        existing.setDepartment(eventDetails.getDepartment());
        existing.setImageUrl(eventDetails.getImageUrl());
        existing.setOrganizerName(eventDetails.getOrganizerName());

        eventValidationService.validateForSave(existing);
        eventMetadataService.normalize(existing);
        return eventRepository.save(existing);
    }

    public void backfillEventMetadata() {
        List<Event> events = eventRepository.findAll();
        boolean updated = false;

        for (Event event : events) {
            updated = eventMetadataService.normalize(event) || updated;
        }

        if (updated) {
            eventRepository.saveAll(events);
        }
    }

    public Registration registerForEvent(Long userId, Long eventId) {
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new RuntimeException("User not found"));

        Event event = eventRepository.findById(eventId)
            .orElseThrow(() -> new RuntimeException("Event not found"));

        if (user.getRole() != User.Role.STUDENT) {
            throw new IllegalArgumentException("Only students can register for events");
        }

        eventValidationService.validateSchedule(event.getDate(), event.getTime(), event.getEndTime());

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
        return registrationView.build(registrationRepository.findByEventId(eventId));
    }

    public boolean isRegistered(Long userId, Long eventId) {
        return registrationRepository.existsByUserIdAndEventId(userId, eventId);
    }

    @Transactional
    public void cancelRegistration(Long userId, Long eventId) {
        userRepository.findById(userId)
            .orElseThrow(() -> new RuntimeException("User not found"));

        eventRepository.findById(eventId)
            .orElseThrow(() -> new RuntimeException("Event not found"));

        if (!registrationRepository.existsByUserIdAndEventId(userId, eventId)) {
            throw new RuntimeException("Registration not found");
        }

        registrationRepository.deleteByUserIdAndEventId(userId, eventId);
    }
}
