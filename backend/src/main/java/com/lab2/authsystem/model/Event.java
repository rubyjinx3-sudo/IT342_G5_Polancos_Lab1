package com.lab2.authsystem.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.LocalDateTime;

@Entity
@Table(name = "events")
public class Event {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Event title is required")
    @Size(max = 120, message = "Event title must not exceed 120 characters")
    @Column(nullable = false)
    private String title;

    @Size(max = 2000, message = "Description must not exceed 2000 characters")
    @Column(columnDefinition = "TEXT")
    private String description;

    @NotNull(message = "Event date is required")
    @Column(nullable = false)
    private LocalDate date;

    @NotNull(message = "Start time is required")
    @Column(nullable = false)
    private LocalTime time;

    @Column(name = "end_time")
    private LocalTime endTime;

    @NotBlank(message = "Event location is required")
    @Size(max = 150, message = "Location must not exceed 150 characters")
    @Column(nullable = false)
    private String location;

    @Column(name = "category")
    private String category;

    @Column(name = "department")
    private String department;

    @Column(name = "image_url", columnDefinition = "TEXT")
    private String imageUrl;

    @NotNull(message = "Organizer ID is required")
    @Column(name = "organizer_id", nullable = false)
    private Long organizerId;

    @Column(name = "organizer_name")
    private String organizerName;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }

    // ── Getters & Setters ──────────────────────────────────

    public Long getId()                  { return id; }
    public void setId(Long id)           { this.id = id; }

    public String getTitle()                 { return title; }
    public void setTitle(String title)       { this.title = title; }

    public String getDescription()                   { return description; }
    public void setDescription(String description)   { this.description = description; }

    public LocalDate getDate()               { return date; }
    public void setDate(LocalDate date)      { this.date = date; }

    public LocalTime getTime()               { return time; }
    public void setTime(LocalTime time)      { this.time = time; }

    public LocalTime getEndTime()                { return endTime; }
    public void setEndTime(LocalTime endTime)    { this.endTime = endTime; }

    public String getLocation()                  { return location; }
    public void setLocation(String location)     { this.location = location; }

    public String getCategory()                    { return category; }
    public void setCategory(String category)       { this.category = category; }

    public String getDepartment()                    { return department; }
    public void setDepartment(String department)     { this.department = department; }

    public String getImageUrl()                    { return imageUrl; }
    public void setImageUrl(String imageUrl)       { this.imageUrl = imageUrl; }

    public Long getOrganizerId()                     { return organizerId; }
    public void setOrganizerId(Long organizerId)     { this.organizerId = organizerId; }

    public String getOrganizerName()                     { return organizerName; }
    public void setOrganizerName(String organizerName)   { this.organizerName = organizerName; }

    public LocalDateTime getCreatedAt()                  { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt)    { this.createdAt = createdAt; }
}
