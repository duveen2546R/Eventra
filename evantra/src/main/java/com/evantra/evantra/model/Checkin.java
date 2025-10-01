package com.evantra.evantra.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.OffsetDateTime;
import java.util.UUID;

@Getter
@Setter
@Entity
@Table(name = "checkins") // ✅ match your DB table name
public class Checkin {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "checkin_id")
    private UUID checkinId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "reg_id") // ✅ matches DB foreign key
    private EventParticipant registration;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "scanned_by") // ✅ matches DB foreign key
    private User scannedBy;

    @Column(name = "scanned_at", nullable = false, updatable = false)
    private OffsetDateTime scannedAt;

    @Column(name = "status")
    private String status;

    @PrePersist
    protected void onCreate() {
        scannedAt = OffsetDateTime.now();
    }
}
