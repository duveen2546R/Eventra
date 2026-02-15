package com.evantra.evantra.service;

import com.evantra.evantra.dto.CheckInResponse;
import com.evantra.evantra.dto.RegisterParticipantResponse;
import com.evantra.evantra.model.Event;
import com.evantra.evantra.model.EventParticipant;
import com.evantra.evantra.model.User;
import com.evantra.evantra.repository.EventParticipantRepository;
import com.evantra.evantra.repository.EventRepository;
import com.evantra.evantra.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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

    // ---------------- REGISTER PARTICIPANT ----------------
    @Transactional
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

        Integer availableSpots = event.getRemainingCapacity() != null
                ? event.getRemainingCapacity()
                : event.getCapacity();
        if (availableSpots == null || availableSpots <= 0) {
            throw new Exception("Event is full.");
        }

        // Generate passId
        String passId = generatePassId(eventId, userId);

        // Create participant record
        EventParticipant participant = new EventParticipant();
        participant.setEvent(event);
        participant.setUser(user);
        participant.setCheckedIn(false);
        participant.setPassId(passId);
        EventParticipant savedParticipant = eventParticipantRepository.save(participant);

        String qrCodeUrl = buildParticipantQrUrl(savedParticipant.getParticipantId());
        savedParticipant.setQrCodeUrl(qrCodeUrl);
        eventParticipantRepository.save(savedParticipant);

        int currentRemaining = event.getRemainingCapacity() != null
                ? event.getRemainingCapacity()
                : event.getCapacity();
        event.setRemainingCapacity(Math.max(0, currentRemaining - 1));
        eventRepository.save(event);

        System.out.println("Generated Pass ID: " + passId);
        System.out.println("Generated QR Code URL: " + qrCodeUrl);

        RegisterParticipantResponse response = new RegisterParticipantResponse();
        response.setMessage("Registered successfully.");
        response.setPassId(passId);
        response.setQrCodeUrl(qrCodeUrl);
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

        boolean participantUpdated = false;

        if (participant.getPassId() == null || participant.getPassId().isBlank()) {
            String fallbackPassId = participant.getParticipantId() != null
                    ? participant.getParticipantId().toString()
                    : generatePassId(eventId, userId);
            participant.setPassId(fallbackPassId);
            participantUpdated = true;
        }

        if (!hasRenderableQrCode(participant.getQrCodeUrl()) && participant.getParticipantId() != null) {
            participant.setQrCodeUrl(buildParticipantQrUrl(participant.getParticipantId()));
            participantUpdated = true;
        }

        if (participantUpdated) {
            participant = eventParticipantRepository.save(participant);
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

    private boolean hasRenderableQrCode(String qrCodeUrl) {
        if (qrCodeUrl == null || qrCodeUrl.isBlank()) {
            return false;
        }

        return qrCodeUrl.startsWith("data:image/")
                || qrCodeUrl.startsWith("http://")
                || qrCodeUrl.startsWith("https://")
                || qrCodeUrl.startsWith("/api/participants/")
                || qrCodeUrl.startsWith("/api/event-participants/qr/");
    }

    private String buildParticipantQrUrl(UUID participantId) {
        return "/api/participants/" + participantId + "/qr";
    }
}
