package com.evantra.evantra.model;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.UUID;

@Entity
@Table(name = "payments")  // ✅ lowercase to match schema
public class Payment {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "payment_id")  // ✅ match schema
    private UUID paymentId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "reg_id")  // ✅ matches schema (FK → event_participants.participant_id)
    private EventParticipant registration;

    @Column(name = "amount")
    private BigDecimal amount;

    @Column(name = "status")
    private String status;

    @Column(name = "transaction_ref")
    private String transactionRef;

    @Column(name = "paid_at", nullable = false, updatable = false)
    private OffsetDateTime paidAt;

    @PrePersist
    protected void onCreate() {
        paidAt = OffsetDateTime.now();
    }

    // --- Getters and Setters ---
    public UUID getPaymentId() { return paymentId; }
    public void setPaymentId(UUID paymentId) { this.paymentId = paymentId; }

    public EventParticipant getRegistration() { return registration; }
    public void setRegistration(EventParticipant registration) { this.registration = registration; }

    public BigDecimal getAmount() { return amount; }
    public void setAmount(BigDecimal amount) { this.amount = amount; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getTransactionRef() { return transactionRef; }
    public void setTransactionRef(String transactionRef) { this.transactionRef = transactionRef; }

    public OffsetDateTime getPaidAt() { return paidAt; }
    public void setPaidAt(OffsetDateTime paidAt) { this.paidAt = paidAt; }
}
