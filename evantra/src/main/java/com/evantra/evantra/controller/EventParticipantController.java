package com.evantra.evantra.controller;

import com.evantra.evantra.model.EventParticipant;
import com.evantra.evantra.repository.EventParticipantRepository;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.imageio.ImageIO;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/event-participants")
public class EventParticipantController {

    @Autowired
    private EventParticipantRepository eventParticipantRepository;

    @GetMapping("/hello")
    public String hello() {
        return "Hello";
    }

    @GetMapping("/{eventId}/{userId}")
    public ResponseEntity<?> getEventParticipant(
            @PathVariable UUID eventId, @PathVariable UUID userId) {
        EventParticipant participant = eventParticipantRepository.findByEvent_EventIdAndUser_UserId(eventId, userId);
        if (participant == null) {
            return ResponseEntity.notFound().build();
        }

        try {
            String qrCodeText = "event:" + eventId + ",user:" + userId;
            QRCodeWriter qrCodeWriter = new QRCodeWriter();
            BitMatrix bitMatrix = qrCodeWriter.encode(qrCodeText, BarcodeFormat.QR_CODE, 200, 200);

            ByteArrayOutputStream pngOutputStream = new ByteArrayOutputStream();
            MatrixToImageWriter.writeToStream(bitMatrix, "PNG", pngOutputStream);
            byte[] pngData = pngOutputStream.toByteArray();

            String qrCodeUrl = "data:image/png;base64," + Base64.getEncoder().encodeToString(pngData);

            Map<String, Object> response = new HashMap<>();
            response.put("event", participant.getEvent());
            response.put("participant", participant);
            response.put("qrCodeUrl", qrCodeUrl);

            return ResponseEntity.ok(response);
        } catch (WriterException | IOException e) {
            return ResponseEntity.status(500).body("Error generating QR code");
        }
    }

    @GetMapping("/is-registered/{eventId}/{userId}")
    public ResponseEntity<Boolean> isRegistered(
            @PathVariable UUID eventId, @PathVariable UUID userId) {
        boolean registered = eventParticipantRepository.existsByEvent_EventIdAndUser_UserId(eventId, userId);
        return ResponseEntity.ok(registered);
    }
}
