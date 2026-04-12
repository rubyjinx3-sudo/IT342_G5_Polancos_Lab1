package com.lab2.authsystem.controller;

import com.lab2.authsystem.dto.EventRegistrationRequest;
import com.lab2.authsystem.model.Event;
import com.lab2.authsystem.model.Registration;
import com.lab2.authsystem.service.EventService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
public class EventController {

    @Autowired
    private EventService eventService;

    @GetMapping("/events")
    public ResponseEntity<List<Event>> getAllEvents() {
        return ResponseEntity.ok(eventService.getAllEvents());
    }

    @GetMapping("/events/{id}")
    public ResponseEntity<?> getEventById(@PathVariable Long id) {
        return eventService.getEventById(id)
            .map(ResponseEntity::ok)
            .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/events/organizer/{organizerId}")
    public ResponseEntity<List<Event>> getEventsByOrganizer(@PathVariable Long organizerId) {
        return ResponseEntity.ok(eventService.getEventsByOrganizer(organizerId));
    }

    @PostMapping("/events")
    public ResponseEntity<Event> createEvent(@Valid @RequestBody Event event) {
        return ResponseEntity.ok(eventService.createEvent(event));
    }

    @PutMapping("/events/{id}")
    public ResponseEntity<?> updateEvent(@PathVariable Long id, @Valid @RequestBody Event event) {
        try {
            return ResponseEntity.ok(eventService.updateEvent(id, event));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/registrations")
    public ResponseEntity<?> registerForEvent(@Valid @RequestBody EventRegistrationRequest body) {
        try {
            Registration registration = eventService.registerForEvent(body.getUserId(), body.getEventId());
            return ResponseEntity.ok(registration);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/registrations/user/{userId}")
    public ResponseEntity<List<Registration>> getRegistrationsByUser(@PathVariable Long userId) {
        return ResponseEntity.ok(eventService.getRegistrationsByUser(userId));
    }

    @GetMapping("/registrations/event/{eventId}")
    public ResponseEntity<List<Registration>> getRegistrationsByEvent(@PathVariable Long eventId) {
        return ResponseEntity.ok(eventService.getRegistrationsByEvent(eventId));
    }

    @GetMapping("/registrations/check")
    public ResponseEntity<Boolean> isRegistered(
            @RequestParam Long userId,
            @RequestParam Long eventId) {
        return ResponseEntity.ok(eventService.isRegistered(userId, eventId));
    }

    @DeleteMapping("/registrations")
    public ResponseEntity<?> cancelRegistration(
            @RequestParam Long userId,
            @RequestParam Long eventId) {
        try {
            eventService.cancelRegistration(userId, eventId);
            return ResponseEntity.ok("Registration cancelled");
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}
