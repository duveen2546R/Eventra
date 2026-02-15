package com.evantra.evantra.controller;

import com.evantra.evantra.model.EventParticipant;
import com.evantra.evantra.repository.EventParticipantRepository;
import com.evantra.evantra.repository.EventOrganizerRepository;
import com.evantra.evantra.repository.EventRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.OffsetDateTime;
import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/event-organizers")
public class EventOrganizerController {

    @Autowired
    private EventOrganizerRepository eventOrganizerRepository;

    @Autowired
    private EventParticipantRepository eventParticipantRepository;

    @Autowired
    private EventRepository eventRepository;


    @GetMapping("/hello")
    public String hello() {
        return "Organizer API Working";
    }


    // ---------------- GET ALL ORGANIZERS ----------------
    @GetMapping
    public ResponseEntity<?> getAllEventOrganizers() {
        return ResponseEntity.ok(eventOrganizerRepository.findAll());
    }


    // ---------------- GET ALL PARTICIPANTS OF EVENT ----------------
    @GetMapping("/participants/{eventId}")
    public ResponseEntity<?> getAllParticipantsForEvent(@PathVariable UUID eventId) {

        if (!eventRepository.existsById(eventId)) {
            return ResponseEntity.badRequest().body("Event not found.");
        }

        List<EventParticipant> participants =
                eventParticipantRepository.findAllByEvent_EventId(eventId);

        return ResponseEntity.ok(participants);
    }

    // ---------------- RECENT CHECK-INS FOR EVENT ----------------
    @GetMapping("/recent-checkins/{eventId}")
    public ResponseEntity<?> getRecentCheckInsForEvent(
            @PathVariable UUID eventId,
            @RequestParam(defaultValue = "10") int limit
    ) {
        if (!eventRepository.existsById(eventId)) {
            return ResponseEntity.badRequest().body("Event not found.");
        }

        int safeLimit = Math.max(1, Math.min(limit, 50));
        List<EventParticipant> recentParticipants = eventParticipantRepository.findRecentCheckedInByEventId(
                eventId,
                PageRequest.of(0, safeLimit)
        );

        List<Map<String, Object>> response = recentParticipants.stream().map(participant -> {
            Map<String, Object> row = new HashMap<>();
            String name = participant.getUser() != null && participant.getUser().getName() != null
                    ? participant.getUser().getName()
                    : "Unknown Participant";
            OffsetDateTime checkedInTime = participant.getCheckedInAt() != null
                    ? participant.getCheckedInAt()
                    : participant.getRegisteredAt();

            row.put("name", name);
            row.put("passId", participant.getPassId());
            row.put("checkedInTime", checkedInTime);
            return row;
        }).collect(Collectors.toList());

        return ResponseEntity.ok(response);
    }


    // ---------------- CHECK-IN USING PASS ID ----------------
    @PostMapping("/checkin/{eventId}/{passId}")
    public ResponseEntity<?> checkInParticipant(
            @PathVariable UUID eventId,
            @PathVariable String passId
    ) {
        EventParticipant participant =
                eventParticipantRepository.findByEvent_EventIdAndPassId(eventId, passId);

        if (participant == null) {
            return ResponseEntity.badRequest().body("Invalid Pass ID. Participant not found.");
        }

        if (participant.isCheckedIn()) {
            return ResponseEntity.badRequest().body("Already checked in. Pass already used.");
        }

        participant.setCheckedIn(true);
        participant.setCheckedInAt(OffsetDateTime.now());
        eventParticipantRepository.save(participant);

        Map<String, Object> response = new HashMap<>();
        response.put("message", "Check-in successful.");
        response.put("passId", participant.getPassId());
        response.put("checkedIn", participant.isCheckedIn());
        response.put("checkedInTime", participant.getCheckedInAt());
        response.put("user", participant.getUser());

        return ResponseEntity.ok(response);
    }


    // ---------------- UNDO CHECK-IN (OPTIONAL) ----------------
    @PostMapping("/undo-checkin/{eventId}/{passId}")
    public ResponseEntity<?> undoCheckIn(
            @PathVariable UUID eventId,
            @PathVariable String passId
    ) {
        EventParticipant participant =
                eventParticipantRepository.findByEvent_EventIdAndPassId(eventId, passId);

        if (participant == null) {
            return ResponseEntity.badRequest().body("Invalid Pass ID. Participant not found.");
        }

        if (!participant.isCheckedIn()) {
            return ResponseEntity.badRequest().body("Participant is not checked in yet.");
        }

        participant.setCheckedIn(false);
        participant.setCheckedInAt(null);
        eventParticipantRepository.save(participant);

        Map<String, Object> response = new HashMap<>();
        response.put("message", "Undo check-in successful.");
        response.put("passId", participant.getPassId());
        response.put("checkedIn", participant.isCheckedIn());
        response.put("checkedInTime", null);

        return ResponseEntity.ok(response);
    }
}
