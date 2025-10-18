package com.evantra.evantra.model;

import java.io.Serializable;
import java.util.Objects;
import java.util.UUID;

public class EventOrganizerId implements Serializable {

    // --- THIS IS THE FIX ---
    // The names here MUST match the @Id fields in EventOrganizer.java
    // The type MUST be the primary key type of the Event and User entities.
    private UUID event;
    private UUID user;
    // -----------------------

    public EventOrganizerId() {}

    public EventOrganizerId(UUID event, UUID user) {
        this.event = event;
        this.user = user;
    }

    // equals() and hashCode() are essential
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        EventOrganizerId that = (EventOrganizerId) o;
        return Objects.equals(event, that.event) && Objects.equals(user, that.user);
    }

    @Override
    public int hashCode() {
        return Objects.hash(event, user);
    }
    
    // Getters and Setters
    public UUID getEvent() { return event; }
    public void setEvent(UUID event) { this.event = event; }
    public UUID getUser() { return user; }
    public void setUser(UUID user) { this.user = user; }
}