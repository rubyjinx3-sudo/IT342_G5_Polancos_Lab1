package com.lab2.authsystem.service.event;

import com.lab2.authsystem.model.Event;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalTime;

@Component
public class EventValidationService {

    public void validateForSave(Event event) {
        if (event.getTitle() != null) {
            event.setTitle(event.getTitle().trim());
        }

        if (event.getDescription() != null) {
            event.setDescription(event.getDescription().trim());
        }

        if (event.getLocation() != null) {
            event.setLocation(event.getLocation().trim());
        }

        if (event.getOrganizerName() != null) {
            event.setOrganizerName(event.getOrganizerName().trim());
        }

        if (event.getCategory() != null) {
            event.setCategory(event.getCategory().trim().toLowerCase());
        }

        if (event.getDepartment() != null) {
            event.setDepartment(event.getDepartment().trim());
        }

        validateSchedule(event.getDate(), event.getTime(), event.getEndTime());
    }

    public void validateSchedule(LocalDate date, LocalTime startTime, LocalTime endTime) {
        if (date != null && date.isBefore(LocalDate.now())) {
            throw new IllegalArgumentException("Event date cannot be in the past");
        }

        if (startTime != null && endTime != null && !endTime.isAfter(startTime)) {
            throw new IllegalArgumentException("End time must be later than the start time");
        }
    }
}
