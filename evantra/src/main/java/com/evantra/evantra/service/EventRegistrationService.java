package com.evantra.evantra.service;

import com.evantra.evantra.model.Event;
import com.evantra.evantra.model.EventParticipant;
import com.evantra.evantra.model.Payment;
import com.evantra.evantra.model.User;
import com.evantra.evantra.repository.EventRepository;
import com.evantra.evantra.repository.EventParticipantRepository;
import com.evantra.evantra.repository.PaymentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
public class EventRegistrationService {

    @Autowired private EventParticipantRepository eventParticipantRepository;
    @Autowired private EventRepository eventRepository;
    @Autowired private PaymentRepository paymentRepository;
    @Autowired private EmailService emailService;

    @Transactional // Ensures all database operations succeed or none do
    public EventParticipant finalizeRegistration(User user, Event event, String razorpayOrderId, String razorpayPaymentId) {
        if (eventParticipantRepository.existsByEventAndUser(event, user)) {
            throw new IllegalStateException("User is already registered for this event.");
        }

        Integer availableSpots = event.getRemainingCapacity() != null
                ? event.getRemainingCapacity()
                : event.getCapacity();
        if (availableSpots == null || availableSpots <= 0) {
            throw new IllegalStateException("Event is full.");
        }

        // 1. Create the Payment record
        Payment payment = new Payment();
        payment.setAmount(event.getAmount());
        payment.setStatus("SUCCESS");
        

        // 2. Create the EventParticipant record
        EventParticipant participant = new EventParticipant();
        participant.setEvent(event);
        participant.setUser(user);
        participant.setCheckedIn(false);

        // Keep pass generation consistent with participant registration flow.
        String passId = generatePassId(event.getEventId(), user.getUserId());
        participant.setPassId(passId);

        EventParticipant savedParticipant = eventParticipantRepository.save(participant);
        savedParticipant.setQrCodeUrl("/api/participants/" + savedParticipant.getParticipantId() + "/qr");
        eventParticipantRepository.save(savedParticipant);

        // Link payment to participant and save
        paymentRepository.save(payment);

        // 5. Decrement remaining spots without changing total capacity
        Event eventToUpdate = savedParticipant.getEvent();
        int currentRemaining = eventToUpdate.getRemainingCapacity() != null
                ? eventToUpdate.getRemainingCapacity()
                : eventToUpdate.getCapacity();
        eventToUpdate.setRemainingCapacity(Math.max(0, currentRemaining - 1));
        eventRepository.save(eventToUpdate);

        // 4. Send the email asynchronously
        new Thread(() -> emailService.sendEventRegistrationEmail(user, event, passId)).start();

        return savedParticipant;
    }

    private String generatePassId(UUID eventId, UUID userId) {
        String eventShort = eventId.toString().substring(0, 6).toUpperCase();
        String userShort = userId.toString().substring(0, 6).toUpperCase();
        String random = UUID.randomUUID().toString().substring(0, 6).toUpperCase();

        return "EVT-" + eventShort + "-USR-" + userShort + "-" + random;
    }
}
