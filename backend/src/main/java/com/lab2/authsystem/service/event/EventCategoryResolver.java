package com.lab2.authsystem.service.event;

import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class EventCategoryResolver {

    private static final String DEFAULT_CATEGORY = "social";

    private final List<CategoryRule> categoryRules;

    public EventCategoryResolver() {
        this.categoryRules = List.of(
            new RegexCategoryRule(".*(tech|hack|science|code|program|robot|innovation|digital|ict).*", "technology"),
            new RegexCategoryRule(".*(academic|research|seminar|workshop|quiz|forum|lecture).*", "academic"),
            new RegexCategoryRule(".*(cultur|music|art|festival|dance|perform).*", "cultural"),
            new RegexCategoryRule(".*(career|fair|job|summit|entrepreneur|business).*", "career"),
            new RegexCategoryRule(".*(sport|game|tournament|athletic).*", "sports")
        );
    }

    public String resolve(String title) {
        return categoryRules.stream()
            .filter(rule -> rule.matches(title))
            .map(CategoryRule::getCategory)
            .findFirst()
            .orElse(DEFAULT_CATEGORY);
    }
}
