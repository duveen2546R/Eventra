package com.evantra.evantra.service;

import com.evantra.evantra.model.Event;
import com.evantra.evantra.model.User;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.ClassPathResource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;

@Service
public class EmailService {

    @Autowired
    private JavaMailSender mailSender;

    @Autowired
    private QrCodeService qrCodeService;

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

    public void sendEventRegistrationEmail(User user, Event event, String qrPayload) {
        try {
            MimeMessage mimeMessage = mailSender.createMimeMessage();
            // Use multipart: true to allow attachments
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true, StandardCharsets.UTF_8.name());

            // Use the same generator used by website QR endpoint.
            byte[] pngData = qrCodeService.generateQrPngBytes(qrPayload);

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
