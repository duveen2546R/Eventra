package com.evantra.evantra.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonIgnore;

@Getter
@Setter
@Entity
@Table(name = "events")
public class Event {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "event_id")
    private UUID eventId;

    @Column(nullable = false)
    private String title;

    @Column(length = 1024)
    private String description;

    @Column(nullable = false)
    private String location;

    private String latitude;
    private String longitude;

    @Column(name = "qr_code_url", columnDefinition = "TEXT")
    private String qrCodeUrl;

    @Column(name = "brochure_url", columnDefinition = "TEXT")
    private String brochureUrl;

    @Column(nullable = false)
    private BigDecimal amount;

    @Column(nullable = false)
    private Integer capacity;

    /**
     * Tracks the number of spots remaining for the event
     * This should be updated when participants register/cancel
     */
    @Column(name = "remaining_capacity")
    private Integer remainingCapacity;

    @Column(nullable = false)
    private String status;
    
    /**
     * The actual date and time when the event will occur
     * Set by the organizer during event creation/editing
     */
    @Column(name = "event_timestamp")
    private LocalDateTime eventTimestamp;

    /**
     * When this event record was created in the database
     */
    @Column(name = "created_at", nullable = false, updatable = false)
    private OffsetDateTime createdAt;

    @JsonIgnore
    @OneToMany(mappedBy = "event", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    private List<EventOrganizer> eventOrganizers = new ArrayList<>();

    @PrePersist
    protected void onCreate() {
        createdAt = OffsetDateTime.now();
        
        // Initialize remaining capacity to match total capacity if not set
        if (remainingCapacity == null && capacity != null) {
            remainingCapacity = capacity;
        }
    }
}