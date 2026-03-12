package com.lab2.authsystem.controller;

import com.lab2.authsystem.model.Event;
import com.lab2.authsystem.model.Registration;
import com.lab2.authsystem.service.EventService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "http://localhost:3000")
public class EventController {

    @Autowired
    private EventService eventService;

    // ── GET all events ─────────────────────────────────────
    @GetMapping("/events")
    public ResponseEntity<List<Event>> getAllEvents() {
        List<Event> events = eventService.getAllEvents();
        return ResponseEntity.ok(events);
    }

    // ── GET event by ID ────────────────────────────────────
    @GetMapping("/events/{id}")
    public ResponseEntity<?> getEventById(@PathVariable Long id) {
        return eventService.getEventById(id)
            .map(ResponseEntity::ok)
            .orElse(ResponseEntity.notFound().build());
    }

    // ── GET events by organizer ────────────────────────────
    @GetMapping("/events/organizer/{organizerId}")
    public ResponseEntity<List<Event>> getEventsByOrganizer(@PathVariable Long organizerId) {
        List<Event> events = eventService.getEventsByOrganizer(organizerId);
        return ResponseEntity.ok(events);
    }

    // ── POST create event ──────────────────────────────────
    @PostMapping("/events")
    public ResponseEntity<Event> createEvent(@RequestBody Event event) {
        Event saved = eventService.createEvent(event);
        return ResponseEntity.ok(saved);
    }

    // ── POST register for event ────────────────────────────
    @PostMapping("/registrations")
    public ResponseEntity<?> registerForEvent(@RequestBody Map<String, Long> body) {
        try {
            Long userId  = body.get("userId");
            Long eventId = body.get("eventId");
            Registration reg = eventService.registerForEvent(userId, eventId);
            return ResponseEntity.ok(reg);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // ── GET registrations by user ──────────────────────────
    @GetMapping("/registrations/user/{userId}")
    public ResponseEntity<List<Registration>> getRegistrationsByUser(@PathVariable Long userId) {
        List<Registration> regs = eventService.getRegistrationsByUser(userId);
        return ResponseEntity.ok(regs);
    }

    // ── GET registrations by event (organizer view) ────────
    @GetMapping("/registrations/event/{eventId}")
    public ResponseEntity<List<Registration>> getRegistrationsByEvent(@PathVariable Long eventId) {
        List<Registration> regs = eventService.getRegistrationsByEvent(eventId);
        return ResponseEntity.ok(regs);
    }

    // ── GET check if user is registered ───────────────────
    @GetMapping("/registrations/check")
    public ResponseEntity<Boolean> isRegistered(
            @RequestParam Long userId,
            @RequestParam Long eventId) {
        boolean registered = eventService.isRegistered(userId, eventId);
        return ResponseEntity.ok(registered);
    }

    // ── DELETE cancel registration ─────────────────────────
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