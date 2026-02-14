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
import java.security.Principal;
import java.time.OffsetDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/events")
public class EventController {

    // --- DEPENDENCY INJECTIONS ---
    @Autowired private RazorpayClient razorpayClient;
    @Autowired private RazorpayProperties razorpayProperties;
    @Autowired private EventRegistrationService eventRegistrationService;
    @Autowired private EventRepository eventRepository;
    @Autowired private UserRepository userRepository;
    @Autowired private EventParticipantRepository eventParticipantRepository;
    @Autowired private EventOrganizerRepository eventOrganizerRepository; // For the "My Events" feature
    @Autowired private PaymentRepository paymentRepository; // For creating payment records

    // ===================================================================
    // = PUBLIC & GENERAL EVENT ENDPOINTS
    // ===================================================================

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
    
    // ===================================================================
    // = AUTHENTICATED EVENT MANAGEMENT (CRUD)
    // ===================================================================
    @PostMapping
    public Event createEvent(@RequestBody Event event, Principal principal) {

        User user = userRepository.findByUsername(principal.getName())
                .orElseThrow(() -> new RuntimeException("User not found. Please ensure you are logged in."));

        Event savedEvent = eventRepository.save(event);

        EventOrganizer eventOrganizer = new EventOrganizer();

        // SET COMPOSITE KEY MANUALLY
        EventOrganizerId id = new EventOrganizerId();
        id.setEventId(savedEvent.getEventId());
        id.setUserId(user.getUserId());

        eventOrganizer.setId(id);
        eventOrganizer.setEvent(savedEvent);
        eventOrganizer.setUser(user);
        eventOrganizer.setRole("organizer");

        eventOrganizerRepository.save(eventOrganizer);

        return savedEvent;
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<Event> updateEvent(@PathVariable UUID id, @RequestBody Event eventDetails) {
        return eventRepository.findById(id)
                .map(event -> {
                    event.setTitle(eventDetails.getTitle());
                    event.setDescription(eventDetails.getDescription());
                    event.setLocation(eventDetails.getLocation());
                    event.setAmount(eventDetails.getAmount());
                    event.setCapacity(eventDetails.getCapacity());
                    eventRepository.save(event);
                    return ResponseEntity.ok(event);
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteEvent(@PathVariable UUID id) {
        return eventRepository.findById(id)
                .map(event -> {
                    // For production, you should handle cleanup of related participants and payments
                    eventRepository.delete(event);
                    return ResponseEntity.ok().build();
                })
                .orElse(ResponseEntity.notFound().build());
    }

    // ===================================================================
    // = MY EVENTS & STATISTICS
    // ===================================================================

    @GetMapping("/mine")
    public ResponseEntity<List<Event>> getMyEvents(@RequestParam UUID userId, @RequestParam String role) {
        if (!userRepository.existsById(userId)) {
            return ResponseEntity.notFound().build();
        }
        if ("organizer".equalsIgnoreCase(role)) {
            List<Event> organizedEvents = eventOrganizerRepository.findEventsByOrganizerUserId(userId);
            return ResponseEntity.ok(organizedEvents);
        } else if ("participant".equalsIgnoreCase(role)) {
            List<Event> participatedEvents = eventParticipantRepository.findEventsByParticipantUserId(userId);
            return ResponseEntity.ok(participatedEvents);
        }
        return ResponseEntity.badRequest().body(null);
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
    
    // ===================================================================
    // = REGISTRATION AND PAYMENT FLOW
    // ===================================================================
    
    @PostMapping("/{eventId}/register-free")
    public ResponseEntity<?> registerForFreeEvent(@PathVariable UUID eventId, @RequestBody Map<String, String> payload) {
        UUID userId = UUID.fromString(payload.get("userId"));
        Event event = eventRepository.findById(eventId).orElse(null);
        User user = userRepository.findById(userId).orElse(null);

        if (event == null || user == null) { return ResponseEntity.badRequest().body("Event or User not found"); }
        if (event.getAmount() != null && event.getAmount().compareTo(BigDecimal.ZERO) > 0) {
            return ResponseEntity.badRequest().body("This is a paid event. Please use the payment flow.");
        }
        if (eventParticipantRepository.existsByEventAndUser(event, user)) {
            return ResponseEntity.badRequest().body("User is already registered for this event.");
        }
        
        eventRegistrationService.finalizeRegistration(user, event, "FREE_REGISTRATION", "N/A");
        return ResponseEntity.ok("Successfully registered for the free event. Confirmation email sent.");
    }
    
    @PostMapping("/{eventId}/create-order")
    public ResponseEntity<?> createOrder(@PathVariable UUID eventId, @RequestBody Map<String, String> payload) {
        try {
            UUID userId = UUID.fromString(payload.get("userId"));
            Event event = eventRepository.findById(eventId).orElseThrow(() -> new RuntimeException("Event not found"));
            userRepository.findById(userId).orElseThrow(() -> new RuntimeException("User not found"));

            if (event.getAmount() == null || event.getAmount().compareTo(BigDecimal.ZERO) <= 0) {
                return ResponseEntity.badRequest().body("This is a free event. Use the /register-free endpoint.");
            }

            BigDecimal amountInPaisa = event.getAmount().multiply(new BigDecimal("100")).setScale(0, RoundingMode.HALF_UP);
            JSONObject orderRequest = new JSONObject();
            orderRequest.put("amount", amountInPaisa.intValue());
            orderRequest.put("currency", "INR");
            String receiptId = "rcpt_" + System.currentTimeMillis();
            orderRequest.put("receipt", receiptId);

            Order order = razorpayClient.orders.create(orderRequest);

            Map<String, String> response = new HashMap<>();
            response.put("order_id", order.get("id"));
            response.put("amount", order.get("amount").toString());
            response.put("key", razorpayProperties.getId());
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Error creating order: " + e.getMessage());
        }
    }
    
    @PostMapping("/verify-payment")
    public ResponseEntity<?> verifyPayment(@RequestBody PaymentVerificationRequest request) {
        try {
            JSONObject options = new JSONObject();
            options.put("razorpay_order_id", request.getRazorpay_order_id());
            options.put("razorpay_payment_id", request.getRazorpay_payment_id());
            options.put("razorpay_signature", request.getRazorpay_signature());
            
            boolean signatureIsValid = Utils.verifyPaymentSignature(options, razorpayProperties.getSecret());

            if (!signatureIsValid) {
                return ResponseEntity.status(400).body("Payment verification failed: Invalid signature.");
            }

            User user = userRepository.findById(request.getUserId()).orElseThrow(() -> new RuntimeException("User not found"));
            Event event = eventRepository.findById(request.getEventId()).orElseThrow(() -> new RuntimeException("Event not found"));

            // Create and save the payment record
            Payment payment = new Payment();
            payment.setRazorpayPaymentId(request.getRazorpay_payment_id());
            payment.setRazorpayOrderId(request.getRazorpay_order_id());
            payment.setRazorpaySignature(request.getRazorpay_signature());
            payment.setAmount(event.getAmount());
            payment.setCurrency("INR");
            payment.setStatus("SUCCESS");
            payment.setUser(user);
            payment.setEvent(event);
            payment.setPaidAt(OffsetDateTime.now());
            paymentRepository.save(payment);

            EventParticipant participant = eventRegistrationService.finalizeRegistration(
                    user, event, request.getRazorpay_order_id(), request.getRazorpay_payment_id());
            
            return ResponseEntity.ok(participant);
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Error verifying payment: " + e.getMessage());
        }
    }
}