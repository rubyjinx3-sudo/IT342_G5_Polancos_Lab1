package com.lab2.authsystem.service.event;

import com.lab2.authsystem.model.Event;
import org.springframework.stereotype.Component;

@Component
public class EventMetadataService {

    private static final String DEFAULT_DEPARTMENT = "All Colleges";

    private final EventCategoryResolver categoryResolver;

    public EventMetadataService(EventCategoryResolver categoryResolver) {
        this.categoryResolver = categoryResolver;
    }

    public boolean normalize(Event event) {
        boolean updated = false;

        if (event.getCategory() == null || event.getCategory().isBlank()) {
            event.setCategory(categoryResolver.resolve(event.getTitle()));
            updated = true;
        }

        if (event.getDepartment() == null || event.getDepartment().isBlank()) {
            event.setDepartment(DEFAULT_DEPARTMENT);
            updated = true;
        }

        return updated;
    }
}
