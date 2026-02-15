package com.evantra.evantra.controller;

import com.evantra.evantra.dto.RegisterParticipantRequest;
import com.evantra.evantra.dto.RegisterParticipantResponse;
import com.evantra.evantra.model.EventParticipant;
import com.evantra.evantra.service.EventParticipantService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/event-participants")
public class EventParticipantController {

    @Autowired
    private EventParticipantService eventParticipantService;


    @GetMapping("/hello")
    public String hello() {
        return "Hello Participant API Working";
    }


    // ---------------- REGISTER ----------------
    @PostMapping("/register")
    public ResponseEntity<?> registerParticipant(@RequestBody RegisterParticipantRequest request) {

        try {
            RegisterParticipantResponse response =
                    eventParticipantService.registerParticipant(request.getEventId(), request.getUserId());

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }


    // ---------------- GET PARTICIPANT DETAILS ----------------
    @GetMapping("/{eventId}/{userId}")
    public ResponseEntity<?> getParticipant(
            @PathVariable UUID eventId,
            @PathVariable UUID userId
    ) {
        try {
            EventParticipant participant = eventParticipantService.getParticipant(eventId, userId);

            Map<String, Object> response = new HashMap<>();
            response.put("participant", participant);
            response.put("event", participant.getEvent());
            response.put("qrCodeUrl", participant.getQrCodeUrl());
            response.put("passId", participant.getPassId());
            response.put("checkedIn", participant.isCheckedIn());

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }


    // ---------------- CHECK REGISTERED ----------------
    @GetMapping("/is-registered/{eventId}/{userId}")
    public ResponseEntity<?> isRegistered(
            @PathVariable UUID eventId,
            @PathVariable UUID userId
    ) {
        try {
            EventParticipant participant = eventParticipantService.getParticipant(eventId, userId);
            return ResponseEntity.ok(participant != null);

        } catch (Exception e) {
            return ResponseEntity.ok(false);
        }
    }
}