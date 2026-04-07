package com.lab2.authsystem.service.event;

public interface CategoryRule {
    boolean matches(String title);
    String getCategory();
}
