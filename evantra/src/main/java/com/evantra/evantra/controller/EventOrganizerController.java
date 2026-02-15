package com.evantra.evantra.controller;

import com.evantra.evantra.model.EventParticipant;
import com.evantra.evantra.repository.EventParticipantRepository;
import com.evantra.evantra.repository.EventOrganizerRepository;
import com.evantra.evantra.repository.EventRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;

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
        eventParticipantRepository.save(participant);

        Map<String, Object> response = new HashMap<>();
        response.put("message", "Check-in successful.");
        response.put("passId", participant.getPassId());
        response.put("checkedIn", participant.isCheckedIn());
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
        eventParticipantRepository.save(participant);

        Map<String, Object> response = new HashMap<>();
        response.put("message", "Undo check-in successful.");
        response.put("passId", participant.getPassId());
        response.put("checkedIn", participant.isCheckedIn());

        return ResponseEntity.ok(response);
    }
}