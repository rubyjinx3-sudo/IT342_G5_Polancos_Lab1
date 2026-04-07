package com.lab2.authsystem.service.event;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class EventCategoryResolverTest {

    private final EventCategoryResolver resolver = new EventCategoryResolver();

    @Test
    void resolveReturnsTechnologyCategoryForTechTitles() {
        assertEquals("technology", resolver.resolve("Campus Tech Innovation Summit"));
    }

    @Test
    void resolveFallsBackToSocialCategory() {
        assertEquals("social", resolver.resolve("Freshmen Welcome Night"));
    }
}
