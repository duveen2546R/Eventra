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
        // 1. Create the Payment record
        Payment payment = new Payment();
        payment.setAmount(event.getAmount());
        payment.setStatus("SUCCESS");
        

        // 2. Create the EventParticipant record
        EventParticipant participant = new EventParticipant();
        participant.setEvent(event);
        participant.setUser(user);

        // Save participant first to get its ID
        EventParticipant savedParticipant = eventParticipantRepository.save(participant);

        // Link payment to participant and save
        
        paymentRepository.save(payment);

        // 3. Generate QR code and send email
        String qrCodeData = savedParticipant.getParticipantId().toString();
        savedParticipant.setQrCodeUrl("/api/participants/" + qrCodeData + "/qr");
        eventParticipantRepository.save(savedParticipant); // Update with QR URL

        // 5. Decrement the event capacity
        Event eventToUpdate = savedParticipant.getEvent();
        eventToUpdate.setCapacity(eventToUpdate.getCapacity() - 1);
        eventRepository.save(eventToUpdate);

        // 4. Send the email asynchronously
        new Thread(() -> emailService.sendEventRegistrationEmail(user, event, qrCodeData)).start();

        return savedParticipant;
    }
}