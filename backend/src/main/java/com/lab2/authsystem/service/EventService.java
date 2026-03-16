package com.lab2.authsystem.service;

import org.springframework.transaction.annotation.Transactional;
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
        normalizeEvent(event);
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

        normalizeEvent(existing);
        return eventRepository.save(existing);
    }

    public void backfillEventMetadata() {
        List<Event> events = eventRepository.findAll();
        boolean updated = false;

        for (Event event : events) {
            boolean eventUpdated = normalizeEvent(event);
            updated = updated || eventUpdated;
        }

        if (updated) {
            eventRepository.saveAll(events);
        }
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

    // ── CANCEL REGISTRATION (Only ONE method!) ───────────
    @Transactional
    public void cancelRegistration(Long userId, Long eventId) {
        if (!registrationRepository.existsByUserIdAndEventId(userId, eventId)) {
            throw new RuntimeException("Registration not found");
        }
        registrationRepository.deleteByUserIdAndEventId(userId, eventId);
    }

    private boolean normalizeEvent(Event event) {
        boolean updated = false;

        if (event.getCategory() == null || event.getCategory().isBlank()) {
            event.setCategory(inferCategory(event.getTitle()));
            updated = true;
        }

        if (event.getDepartment() == null || event.getDepartment().isBlank()) {
            event.setDepartment("All Colleges");
            updated = true;
        }

        return updated;
    }

    private String inferCategory(String title) {
        String safeTitle = title == null ? "" : title;

        if (safeTitle.matches("(?i).*(tech|hack|science|code|program|robot|innovation|digital|ict).*")) {
            return "technology";
        }
        if (safeTitle.matches("(?i).*(academic|research|seminar|workshop|quiz|forum|lecture).*")) {
            return "academic";
        }
        if (safeTitle.matches("(?i).*(cultur|music|art|festival|dance|perform).*")) {
            return "cultural";
        }
        if (safeTitle.matches("(?i).*(career|fair|job|summit|entrepreneur|business).*")) {
            return "career";
        }
        if (safeTitle.matches("(?i).*(sport|game|tournament|athletic).*")) {
            return "sports";
        }
        return "social";
    }
}
