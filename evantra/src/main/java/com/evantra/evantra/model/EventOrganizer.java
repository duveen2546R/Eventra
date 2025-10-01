package com.evantra.evantra.model;

import jakarta.persistence.*;
import java.io.Serializable;
import java.util.UUID;

@Entity
@IdClass(EventOrganizerId.class)
@Table(name = "event_organizers") // adjust table name if needed
public class EventOrganizer implements Serializable {

    @Id
    @Column(name = "event_id", nullable = false)
    private UUID eventId;  // Must match EventOrganizerId

    @Id
    @Column(name = "organizer_id", nullable = false)
    private UUID organizerId; // Must match EventOrganizerId

    // Additional fields
    @Column(name = "role")
    private String role;

    // Constructors
    public EventOrganizer() {}

    public EventOrganizer(UUID eventId, UUID organizerId, String role) {
        this.eventId = eventId;
        this.organizerId = organizerId;
        this.role = role;
    }

    // Getters and Setters
    public UUID getEventId() {
        return eventId;
    }

    public void setEventId(UUID eventId) {
        this.eventId = eventId;
    }

    public UUID getOrganizerId() {
        return organizerId;
    }

    public void setOrganizerId(UUID organizerId) {
        this.organizerId = organizerId;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }
}
