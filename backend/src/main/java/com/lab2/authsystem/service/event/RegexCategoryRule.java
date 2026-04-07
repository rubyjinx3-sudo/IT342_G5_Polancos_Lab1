package com.lab2.authsystem.service.event;

import java.util.regex.Pattern;

public class RegexCategoryRule implements CategoryRule {

    private final Pattern pattern;
    private final String category;

    public RegexCategoryRule(String regex, String category) {
        this.pattern = Pattern.compile(regex, Pattern.CASE_INSENSITIVE);
        this.category = category;
    }

    @Override
    public boolean matches(String title) {
        return pattern.matcher(title == null ? "" : title).matches();
    }

    @Override
    public String getCategory() {
        return category;
    }
}
