package com.evantra.evantra.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
public class PaymentVerificationRequest {
    private String razorpay_payment_id;
    private String razorpay_order_id;
    private String razorpay_signature;

    // We also need the context for what this payment was for
    private UUID userId;
    private UUID eventId;
}