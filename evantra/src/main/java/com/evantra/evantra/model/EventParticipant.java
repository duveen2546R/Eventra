package com.evantra.evantra.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import java.time.OffsetDateTime;
import java.util.UUID;

@Getter
@Setter
@Entity
@Table(name = "event_participants")  // ✅ lowercase to match DB
public class EventParticipant {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "participant_id")  // ✅ matches schema
    private UUID participantId;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "event_id")  // ✅ FK → events.event_id
    private Event event;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "user_id")  // ✅ FK → users.user_id
    private User user;

    @Column(name = "pass_id")
    private String passId;

    @Column(name = "qr_code_url")
    private String qrCodeUrl;

    @Column(name = "checked_in")
    private boolean checkedIn = false;

    @Column(name = "registered_at", nullable = false, updatable = false)
    private OffsetDateTime registeredAt;

    @PrePersist
    protected void onCreate() {
        registeredAt = OffsetDateTime.now();
    }
}
