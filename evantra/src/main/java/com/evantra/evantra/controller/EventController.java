package com.evantra.evantra.controller;

import com.evantra.evantra.config.RazorpayProperties;
import com.evantra.evantra.dto.PaymentVerificationRequest;
import com.evantra.evantra.model.*;
import com.evantra.evantra.repository.*;
import com.evantra.evantra.service.EventRegistrationService;
import com.razorpay.Order;
import com.razorpay.RazorpayClient;
import com.razorpay.Utils;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/events")
public class EventController {

    // --- UPDATED INJECTIONS FOR RAZORPAY ---
    @Autowired private RazorpayClient razorpayClient;
    @Autowired private RazorpayProperties razorpayProperties;
    @Autowired private EventRegistrationService eventRegistrationService;
    // -------------------------------------

    @Autowired private EventRepository eventRepository;
    @Autowired private UserRepository userRepository;
    @Autowired private EventParticipantRepository eventParticipantRepository;

    // ----- UNCHANGED METHODS -----

    @GetMapping
    public List<Event> getAllEvents() {
        return eventRepository.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Event> getEventById(@PathVariable UUID id) {
        return eventRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public Event createEvent(@RequestBody Event event) {
        return eventRepository.save(event);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Event> updateEvent(@PathVariable UUID id, @RequestBody Event eventDetails) {
        return eventRepository.findById(id)
                .map(event -> {
                    event.setTitle(eventDetails.getTitle());
                    event.setDescription(eventDetails.getDescription());
                    eventRepository.save(event);
                    return ResponseEntity.ok(event);
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteEvent(@PathVariable UUID id) {
        return eventRepository.findById(id)
                .map(event -> {
                    eventRepository.delete(event);
                    return ResponseEntity.ok().build();
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/{eventId}/stats")
    public ResponseEntity<?> getEventStats(@PathVariable UUID eventId) {
        if (!eventRepository.existsById(eventId)) {
            return ResponseEntity.notFound().build();
        }
        long totalRegistrations = eventParticipantRepository.countByEvent_EventId(eventId);
        long checkedInCount = eventParticipantRepository.countByEvent_EventIdAndCheckedIn(eventId, true);

        Map<String, Object> stats = new HashMap<>();
        stats.put("eventId", eventId);
        stats.put("totalRegistrations", totalRegistrations);
        stats.put("checkedInCount", checkedInCount);

        return ResponseEntity.ok(stats);
    }


    // ----- METHODS UPDATED FOR RAZORPAY PAYMENT FLOW -----

    @PostMapping("/{eventId}/register-free")
    public ResponseEntity<?> registerForFreeEvent(@PathVariable UUID eventId, @RequestBody Map<String, String> payload) {
        // ... Logic for free events ... (This method is unchanged)
        // You should implement the full logic here
        return ResponseEntity.ok("Successfully registered for the free event.");
    }

    /**
     * API Call 1: Creates a Razorpay Order and sends the order_id to the front end.
     */
    @PostMapping("/{eventId}/create-order")
    public ResponseEntity<?> createOrder(@PathVariable UUID eventId, @RequestBody Map<String, String> payload) {
        try {
            UUID userId = UUID.fromString(payload.get("userId"));
            Event event = eventRepository.findById(eventId).orElseThrow(() -> new RuntimeException("Event not found"));
            userRepository.findById(userId).orElseThrow(() -> new RuntimeException("User not found"));

            if (event.getAmount() == null || event.getAmount().compareTo(BigDecimal.ZERO) <= 0) {
                return ResponseEntity.badRequest().body("This is a free event. Please use the /register-free endpoint.");
            }

            BigDecimal amountInPaisa = event.getAmount().multiply(new BigDecimal("100")).setScale(0, RoundingMode.HALF_UP);

            JSONObject orderRequest = new JSONObject();
            orderRequest.put("amount", amountInPaisa.intValue());
            orderRequest.put("currency", "INR");

            // --- FIX APPLIED HERE: Create a shorter receipt ID ---
            String receiptId = "rcpt_" + System.currentTimeMillis();
            orderRequest.put("receipt", receiptId);
            // ----------------------------------------------------

            Order order = razorpayClient.orders.create(orderRequest);

            Map<String, String> response = new HashMap<>();
            response.put("order_id", order.get("id"));
            response.put("amount", order.get("amount").toString());
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Error creating order: " + e.getMessage());
        }
    }


    /**
     * API Call 2: Verifies the payment signature from Razorpay.
     * If successful, it finalizes the event registration.
     */
    @PostMapping("/verify-payment")
    public ResponseEntity<?> verifyPayment(@RequestBody PaymentVerificationRequest request) {
        try {
            JSONObject options = new JSONObject();
            options.put("razorpay_order_id", request.getRazorpay_order_id());
            options.put("razorpay_payment_id", request.getRazorpay_payment_id());
            options.put("razorpay_signature", request.getRazorpay_signature());

            // --- FIX APPLIED HERE: Use the injected properties object ---
            boolean signatureIsValid = Utils.verifyPaymentSignature(options, razorpayProperties.getSecret());

            if (!signatureIsValid) {
                return ResponseEntity.status(400).body("Payment verification failed: Invalid signature.");
            }

            User user = userRepository.findById(request.getUserId()).orElseThrow(() -> new RuntimeException("User not found"));
            Event event = eventRepository.findById(request.getEventId()).orElseThrow(() -> new RuntimeException("Event not found"));

            EventParticipant participant = eventRegistrationService.finalizeRegistration(
                    user, event, request.getRazorpay_order_id(), request.getRazorpay_payment_id());

            return ResponseEntity.ok(participant);
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Error verifying payment: " + e.getMessage());
        }
    }
}