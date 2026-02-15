package com.evantra.evantra.controller;

import com.evantra.evantra.model.Checkin;
import com.evantra.evantra.model.EventParticipant;
import com.evantra.evantra.model.User;
import com.evantra.evantra.repository.CheckinRepository;
import com.evantra.evantra.repository.EventParticipantRepository;
import com.evantra.evantra.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.OffsetDateTime;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/checkins")
public class CheckinController {

    @Autowired private CheckinRepository checkinRepository;
    @Autowired private EventParticipantRepository eventParticipantRepository;
    @Autowired private UserRepository userRepository;

    @PostMapping("/scan")
    public ResponseEntity<?> scanParticipant(@RequestBody Map<String, String> payload) {
        UUID participantId = UUID.fromString(payload.get("participantId"));
        UUID scannedByUserId = UUID.fromString(payload.get("scannedByUserId"));

        EventParticipant participant = eventParticipantRepository.findById(participantId).orElse(null);
        User scanner = userRepository.findById(scannedByUserId).orElse(null);

        if (participant == null) {
            return ResponseEntity.status(404).body("Participant not found.");
        }
        if (scanner == null) {
            return ResponseEntity.badRequest().body("Scanner user not found.");
        }

        if (participant.isCheckedIn()) {
            return ResponseEntity.status(409).body("This participant has already been checked in.");
        }

        participant.setCheckedIn(true);
        participant.setCheckedInAt(OffsetDateTime.now());
        eventParticipantRepository.save(participant);

        Checkin checkin = new Checkin();
        checkin.setRegistration(participant);
        checkin.setScannedBy(scanner);
        checkin.setStatus("SUCCESS");
        checkinRepository.save(checkin);

        return ResponseEntity.ok("Check-in successful for participant: " + participant.getUser().getName());
    }
}
