package com.lab2.authsystem.service.event;

import com.lab2.authsystem.model.Event;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class EventMetadataServiceTest {

    private final EventMetadataService eventMetadataService =
        new EventMetadataService(new EventCategoryResolver());

    @Test
    void normalizeFillsMissingCategoryAndDepartment() {
        Event event = new Event();
        event.setTitle("Research Seminar 2026");

        boolean updated = eventMetadataService.normalize(event);

        assertTrue(updated);
        assertEquals("academic", event.getCategory());
        assertEquals("All Colleges", event.getDepartment());
    }
}
