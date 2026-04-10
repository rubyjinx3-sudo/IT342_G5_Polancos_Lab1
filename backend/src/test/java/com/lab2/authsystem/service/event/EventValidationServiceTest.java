package com.lab2.authsystem.service.event;

import com.lab2.authsystem.model.Event;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.LocalTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class EventValidationServiceTest {

    private final EventValidationService eventValidationService = new EventValidationService();

    @Test
    void validateForSaveNormalizesTrimmedFields() {
        Event event = new Event();
        event.setTitle("  Sample Event  ");
        event.setDescription("  Demo description  ");
        event.setLocation("  Main Hall  ");
        event.setOrganizerName("  Org Team  ");
        event.setCategory("  TECHNOLOGY  ");
        event.setDepartment("  College of Computer Studies  ");
        event.setDate(LocalDate.now().plusDays(1));
        event.setTime(LocalTime.of(9, 0));
        event.setEndTime(LocalTime.of(11, 0));

        eventValidationService.validateForSave(event);

        assertEquals("Sample Event", event.getTitle());
        assertEquals("Demo description", event.getDescription());
        assertEquals("Main Hall", event.getLocation());
        assertEquals("Org Team", event.getOrganizerName());
        assertEquals("technology", event.getCategory());
        assertEquals("College of Computer Studies", event.getDepartment());
    }

    @Test
    void validateScheduleRejectsInvalidEndTime() {
        assertThrows(IllegalArgumentException.class, () ->
            eventValidationService.validateSchedule(
                LocalDate.now().plusDays(1),
                LocalTime.of(13, 0),
                LocalTime.of(12, 0)
            )
        );
    }
}
