package com.lab2.authsystem.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "registrations",
       uniqueConstraints = @UniqueConstraint(columnNames = {"user_id", "event_id"}))
public class Registration {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "event_id", nullable = false)
    private Long eventId;

    @Column(name = "registered_at", updatable = false)
    private LocalDateTime registeredAt;

    // Joined fields for response (not stored in DB)
    @Transient
    private String studentName;

    @Transient
    private String studentEmail;

    @PrePersist
    protected void onCreate() {
        registeredAt = LocalDateTime.now();
    }

    // ── Getters & Setters ──────────────────────────────────

    public Long getId()              { return id; }
    public void setId(Long id)       { this.id = id; }

    public Long getUserId()                  { return userId; }
    public void setUserId(Long userId)       { this.userId = userId; }

    public Long getEventId()                 { return eventId; }
    public void setEventId(Long eventId)     { this.eventId = eventId; }

    public LocalDateTime getRegisteredAt()                       { return registeredAt; }
    public void setRegisteredAt(LocalDateTime registeredAt)      { this.registeredAt = registeredAt; }

    public String getStudentName()                   { return studentName; }
    public void setStudentName(String studentName)   { this.studentName = studentName; }

    public String getStudentEmail()                      { return studentEmail; }
    public void setStudentEmail(String studentEmail)     { this.studentEmail = studentEmail; }
}