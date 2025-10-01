package com.evantra.evantra.controller;

import com.evantra.evantra.model.*;
import com.evantra.evantra.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/events")
public class EventController {

    @Autowired private EventRepository eventRepository;
    @Autowired private UserRepository userRepository;
    @Autowired private EventParticipantRepository eventParticipantRepository;

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

    @PostMapping("/{eventId}/register")
    public ResponseEntity<?> registerForEvent(@PathVariable UUID eventId, @RequestBody Map<String, String> payload) {
        UUID userId = UUID.fromString(payload.get("userId"));
        
        Event event = eventRepository.findById(eventId).orElse(null);
        User user = userRepository.findById(userId).orElse(null);

        if (event == null || user == null) {
            return ResponseEntity.badRequest().body("Event or User not found");
        }
        
        if (eventParticipantRepository.existsByEventAndUser(event, user)) {
            return ResponseEntity.badRequest().body("User is already registered for this event.");
        }

        if (event.getAmount().compareTo(BigDecimal.ZERO) > 0) {
            System.out.println("Simulating successful payment for user " + user.getEmail());
        }

        EventParticipant participant = new EventParticipant();
        participant.setEvent(event);
        participant.setUser(user);
        
        EventParticipant savedParticipant = eventParticipantRepository.save(participant);

        String qrCodeData = savedParticipant.getParticipantId().toString();
        savedParticipant.setQrCodeUrl("/api/qr/" + qrCodeData);
        eventParticipantRepository.save(savedParticipant);

        return ResponseEntity.ok(savedParticipant);
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

    
}