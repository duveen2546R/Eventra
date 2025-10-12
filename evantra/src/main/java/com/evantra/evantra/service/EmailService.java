package com.evantra.evantra.service;

import com.evantra.evantra.model.Event;
import com.evantra.evantra.model.User;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.ClassPathResource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.nio.charset.StandardCharsets;

@Service
public class EmailService {

    @Autowired
    private JavaMailSender mailSender;

    public void sendWelcomeEmail(User user) {
        try {
            MimeMessage mimeMessage = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true, StandardCharsets.UTF_8.name());

            // Read the HTML template
            String htmlTemplate = new String(new ClassPathResource("templates/welcome-email.html").getInputStream().readAllBytes());

            // Replace placeholders
            String htmlContent = htmlTemplate.replace("${userName}", user.getName());

            helper.setTo(user.getEmail());
            helper.setSubject("Welcome to Eventra!");
            helper.setText(htmlContent, true); // Set to true to send as HTML

            mailSender.send(mimeMessage);
        } catch (Exception e) {
            // In a real app, you would log this error
            System.err.println("Error sending welcome email: " + e.getMessage());
        }
    }

    public void sendEventRegistrationEmail(User user, Event event, String qrCodeData) {
        try {
            MimeMessage mimeMessage = mailSender.createMimeMessage();
            // Use multipart: true to allow attachments
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true, StandardCharsets.UTF_8.name());

            // --- Generate QR Code Image ---
            QRCodeWriter qrCodeWriter = new QRCodeWriter();
            BitMatrix bitMatrix = qrCodeWriter.encode(qrCodeData, BarcodeFormat.QR_CODE, 200, 200);

            ByteArrayOutputStream pngOutputStream = new ByteArrayOutputStream();
            MatrixToImageWriter.writeToStream(bitMatrix, "PNG", pngOutputStream);
            byte[] pngData = pngOutputStream.toByteArray();
            // -----------------------------

            String htmlTemplate = new String(new ClassPathResource("templates/event-registration-email.html").getInputStream().readAllBytes());

            // Replace placeholders
            String htmlContent = htmlTemplate
                .replace("${userName}", user.getName())
                .replace("${eventName}", event.getTitle())
                .replace("${eventLocation}", event.getLocation());

            helper.setTo(user.getEmail());
            helper.setSubject("Your Ticket for " + event.getTitle());
            helper.setText(htmlContent, true);

            // Attach the QR code as an inline resource with a Content-ID
            helper.addInline("qrCode", new ByteArrayResource(pngData), "image/png");

            mailSender.send(mimeMessage);
        } catch (Exception e) {
            System.err.println("Error sending event registration email: " + e.getMessage());
        }
    }
}