package com.evantra.evantra.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.util.UUID;

@Getter
@Setter
@Entity
@Table(name = "events") // Using lowercase 'events' as this is the new convention we established
public class Event {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "event_id")
    private UUID eventId;

    @Column(nullable = false)
    private String title;

    @Column(length = 1024) // Allow for longer descriptions
    private String description;

    @Column(nullable = false)
    private String location;

    private String latitude;
    private String longitude;

    @Column(name = "brochure_url")
    private String brochureUrl;
    
    // Note: The participant-specific QR code URL is on the EventParticipant table.
    // This could be for a general event QR code, if needed.
    @Column(name = "qr_code_url")
    private String qrCodeUrl;

    @Column(nullable = false)
    private BigDecimal amount;

    @Column(nullable = false)
    private Integer capacity; // Use Integer for capacity as it represents a whole number

    private String status;
    
    // --- NEWLY ADDED FIELD ---
    // This field will store the actual date and time of the event
    // that the user picks on the front end.
    @Column(name = "event_timestamp")
    private LocalDateTime eventTimestamp;
    // -----------------------

    @Column(name = "created_at", nullable = false, updatable = false)
    private OffsetDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        createdAt = OffsetDateTime.now();
    }
}