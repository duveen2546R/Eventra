package com.evantra.evantra.controller;

import com.evantra.evantra.repository.*;
import com.evantra.evantra.model.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/participants")
public class EventParticipantController {

    @Autowired
    private EventParticipantRepository participantRepository;

    @GetMapping
    public List<EventParticipant> getAllParticipants() {
        return participantRepository.findAll();
    }

    @PostMapping
    public EventParticipant createParticipant(@RequestBody EventParticipant participant) {
        return participantRepository.save(participant);
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<EventParticipant> getParticipantById(@PathVariable UUID id) {
        return participantRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
}