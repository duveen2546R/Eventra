package com.evantra.evantra.controller;

import com.evantra.evantra.model.EventParticipant;
import com.evantra.evantra.repository.EventParticipantRepository;
import com.evantra.evantra.service.QrCodeService;
import com.google.zxing.WriterException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.CacheControl;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("/api/participants")
public class ParticipantQrController {

    @Autowired
    private EventParticipantRepository eventParticipantRepository;

    @Autowired
    private QrCodeService qrCodeService;

    @GetMapping(value = "/{participantId}/qr", produces = MediaType.IMAGE_PNG_VALUE)
    public ResponseEntity<byte[]> getParticipantQr(@PathVariable UUID participantId) {
        Optional<EventParticipant> participantOpt = eventParticipantRepository.findById(participantId);
        if (participantOpt.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        EventParticipant participant = participantOpt.get();
        String qrPayload = participant.getPassId();
        if (qrPayload == null || qrPayload.isBlank()) {
            qrPayload = participantId.toString();
        }

        try {
            byte[] qrBytes = qrCodeService.generateQrPngBytes(qrPayload);
            return ResponseEntity.ok()
                    .contentType(MediaType.IMAGE_PNG)
                    .cacheControl(CacheControl.noStore())
                    .body(qrBytes);
        } catch (WriterException | IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}
