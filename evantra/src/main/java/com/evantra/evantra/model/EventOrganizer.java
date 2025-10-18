package com.evantra.evantra.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import java.time.OffsetDateTime;

@Getter
@Setter
@Entity
@Table(name = "Event_Organizers")
public class EventOrganizer {

    @EmbeddedId
    private EventOrganizerId id;

    @ManyToOne(fetch = FetchType.EAGER)
    @MapsId("eventId") // This maps the eventId from the EmbeddedId to this ManyToOne relationship
    @JoinColumn(name = "event_id", insertable = false, updatable = false)
    private Event event;

    @ManyToOne(fetch = FetchType.EAGER)
    @MapsId("userId") // This maps the userId from the EmbeddedId to this ManyToOne relationship
    @JoinColumn(name = "user_id", insertable = false, updatable = false)
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