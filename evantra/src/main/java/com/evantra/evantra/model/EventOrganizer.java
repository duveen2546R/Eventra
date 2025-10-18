package com.evantra.evantra.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import java.time.OffsetDateTime;

@Getter
@Setter
@Entity
@Table(name = "Event_Organizers")
@IdClass(EventOrganizerId.class) // This points to the class above
public class EventOrganizer {

    // --- THIS FIELD MUST BE NAMED 'event' ---
    @Id
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "event_id")
    private Event event;
    // ------------------------------------

    // --- THIS FIELD MUST BE NAMED 'user' ---
    @Id
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;
    // ------------------------------------

    private String role;

    @Column(name = "created_at", nullable = false, updatable = false)
    private OffsetDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        createdAt = OffsetDateTime.now();
    }
}