package com.lab2.authsystem.config;

import com.lab2.authsystem.service.EventService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class EventMetadataBackfillConfig {

    @Bean
    CommandLineRunner backfillEventMetadata(EventService eventService) {
        return args -> eventService.backfillEventMetadata();
    }
}
