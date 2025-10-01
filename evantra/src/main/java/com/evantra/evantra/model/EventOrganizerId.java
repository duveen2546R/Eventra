package com.evantra.evantra.model;

import java.io.Serializable;
import java.util.UUID;

public class EventOrganizerId implements Serializable {

    private UUID eventId;
    private UUID organizerId;

    // Default constructor
    public EventOrganizerId() {}

    public EventOrganizerId(UUID eventId, UUID organizerId) {
        this.eventId = eventId;
        this.organizerId = organizerId;
    }

    // equals() and hashCode() — required for IdClass
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof EventOrganizerId)) return false;
        EventOrganizerId that = (EventOrganizerId) o;
        return eventId.equals(that.eventId) && organizerId.equals(that.organizerId);
    }

    @Override
    public int hashCode() {
        return eventId.hashCode() ^ organizerId.hashCode();
    }
}
