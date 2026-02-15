package com.evantra.evantra.service;

import com.evantra.evantra.dto.CheckInResponse;
import com.evantra.evantra.dto.RegisterParticipantResponse;
import com.evantra.evantra.model.Event;
import com.evantra.evantra.model.EventParticipant;
import com.evantra.evantra.model.User;
import com.evantra.evantra.repository.EventParticipantRepository;
import com.evantra.evantra.repository.EventRepository;
import com.evantra.evantra.repository.UserRepository;
import com.google.zxing.WriterException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.Optional;
import java.util.UUID;

@Service
public class EventParticipantService {

    @Autowired
    private EventParticipantRepository eventParticipantRepository;

    @Autowired
    private EventRepository eventRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private QrCodeService qrCodeService;


    // ---------------- REGISTER PARTICIPANT ----------------
    public RegisterParticipantResponse registerParticipant(UUID eventId, UUID userId) throws Exception {

        // Check already registered
        if (eventParticipantRepository.existsByEvent_EventIdAndUser_UserId(eventId, userId)) {
            throw new Exception("User already registered for this event.");
        }

        Optional<Event> eventOpt = eventRepository.findById(eventId);
        if (eventOpt.isEmpty()) {
            throw new Exception("Event not found.");
        }

        Optional<User> userOpt = userRepository.findById(userId);
        if (userOpt.isEmpty()) {
            throw new Exception("User not found.");
        }

        Event event = eventOpt.get();
        User user = userOpt.get();

        // Generate passId
        String passId = generatePassId(eventId, userId);

        // Generate QR Base64 using passId
        String qrCodeBase64;
        try {
            qrCodeBase64 = qrCodeService.generateQrBase64(passId);
        } catch (WriterException | IOException e) {
            throw new Exception("QR code generation failed.");
        }

        // Create participant record
        EventParticipant participant = new EventParticipant();
        participant.setEvent(event);
        participant.setUser(user);
        participant.setCheckedIn(false);
        participant.setPassId(passId);
        participant.setQrCodeUrl(qrCodeBase64);
        System.out.println("Generated Pass ID: " + passId);
        System.out.println("Generated QR Code URL: " + qrCodeBase64);

        eventParticipantRepository.save(participant);

        RegisterParticipantResponse response = new RegisterParticipantResponse();
        response.setMessage("Registered successfully.");
        response.setPassId(passId);
        response.setQrCodeUrl(qrCodeBase64);
        response.setCheckedIn(false);

        return response;
    }


    // ---------------- CHECK-IN PARTICIPANT ----------------
    public CheckInResponse checkIn(UUID eventId, String passId) throws Exception {

        EventParticipant participant =
                eventParticipantRepository.findByEvent_EventIdAndPassId(eventId, passId);

        if (participant == null) {
            throw new Exception("Invalid pass. Participant not found.");
        }

        if (participant.isCheckedIn()) {
            throw new Exception("Already checked in. Pass already used.");
        }

        participant.setCheckedIn(true);
        eventParticipantRepository.save(participant);

        CheckInResponse response = new CheckInResponse();
        response.setMessage("Check-in successful.");
        response.setPassId(participant.getPassId());
        response.setCheckedIn(true);

        return response;
    }


    // ---------------- GET PARTICIPANT DETAILS ----------------
    public EventParticipant getParticipant(UUID eventId, UUID userId) throws Exception {

        EventParticipant participant =
                eventParticipantRepository.findByEvent_EventIdAndUser_UserId(eventId, userId);

        if (participant == null) {
            throw new Exception("Participant not found.");
        }

        return participant;
    }


    // ---------------- PASS GENERATOR ----------------
    private String generatePassId(UUID eventId, UUID userId) {

        String eventShort = eventId.toString().substring(0, 6).toUpperCase();
        String userShort = userId.toString().substring(0, 6).toUpperCase();
        String random = UUID.randomUUID().toString().substring(0, 6).toUpperCase();

        return "EVT-" + eventShort + "-USR-" + userShort + "-" + random;
    }
}