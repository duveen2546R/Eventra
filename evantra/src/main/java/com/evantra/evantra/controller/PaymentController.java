package com.evantra.evantra.controller;

import com.evantra.evantra.repository.*;
import com.evantra.evantra.model.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.security.Principal;
import java.util.UUID;
import java.util.*;
import java.math.BigDecimal;

@RestController
@RequestMapping("/api/payments")
public class PaymentController {

    @Autowired
    private PaymentRepository paymentRepository;

    @Autowired
    private UserRepository userRepository;

    @GetMapping
    public ResponseEntity<Map<String, List<Payment>>> getAllPayments(@RequestParam("userId") UUID userId) {
        System.out.println("Fetching payments for userId: " + userId);
        User user = userRepository.findById(userId).orElseThrow(() -> new RuntimeException("User not found"));

        List<Payment> madePayments = paymentRepository.findByUser(user);
        List<Payment> receivedPayments = paymentRepository.findByEvent_EventOrganizers_User(user);

        Map<String, List<Payment>> response = new HashMap<>();
        response.put("madePayments", madePayments);
        response.put("receivedPayments", receivedPayments);

        System.out.println("Found " + madePayments.size() + " made payments and " + receivedPayments.size() + " received payments for user " + userId);
        return ResponseEntity.ok(response);
    }

    @PostMapping
    public Payment createPayment(@RequestBody Payment payment) {
        return paymentRepository.save(payment);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Payment> getPaymentById(@PathVariable UUID id) {
        return paymentRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/insights/{userId}")
    public ResponseEntity<Map<String, Object>> getPaymentInsights(@PathVariable UUID userId) {
        User user = userRepository.findById(userId).orElseThrow(() -> new RuntimeException("User not found"));

        List<Payment> receivedPayments = paymentRepository.findByEvent_EventOrganizers_User(user);
        List<Payment> spentPayments = paymentRepository.findByUser(user);

        Map<String, Object> insights = new HashMap<>();

        // 1. Aggregate by month
        insights.put("monthlyReceived", aggregatePaymentsByMonth(receivedPayments));
        insights.put("monthlySpent", aggregatePaymentsByMonth(spentPayments));

        // 2. Calculate total amounts
        insights.put("totalReceived", receivedPayments.stream().map(Payment::getAmount).reduce(BigDecimal.ZERO, BigDecimal::add));
        insights.put("totalSpent", spentPayments.stream().map(Payment::getAmount).reduce(BigDecimal.ZERO, BigDecimal::add));

        // 3. Aggregate by event
        insights.put("receivedByEvent", aggregatePaymentsByEvent(receivedPayments));
        insights.put("spentByEvent", aggregatePaymentsByEvent(spentPayments));

        return ResponseEntity.ok(insights);
    }

    private List<Map<String, Object>> aggregatePaymentsByMonth(List<Payment> payments) {
        Map<String, Double> monthlyAggregates = new HashMap<>();
        payments.forEach(payment -> {
            String month = payment.getPaidAt().getMonth().toString(); // Or format as YYYY-MM
            monthlyAggregates.merge(month, payment.getAmount().doubleValue(), Double::sum);
        });

        return monthlyAggregates.entrySet().stream()
                .map(entry -> {
                    Map<String, Object> monthData = new HashMap<>();
                    monthData.put("month", entry.getKey());
                    monthData.put("amount", entry.getValue());
                    return monthData;
                })
                .collect(java.util.stream.Collectors.toList());
    }

    private List<Map<String, Object>> aggregatePaymentsByEvent(List<Payment> payments) {
        Map<String, Double> eventAggregates = new HashMap<>();
        payments.forEach(payment -> {
            String eventTitle = payment.getEvent().getTitle();
            eventAggregates.merge(eventTitle, payment.getAmount().doubleValue(), Double::sum);
        });

        return eventAggregates.entrySet().stream()
                .map(entry -> {
                    Map<String, Object> eventData = new HashMap<>();
                    eventData.put("eventTitle", entry.getKey());
                    eventData.put("amount", entry.getValue());
                    return eventData;
                })
                .collect(java.util.stream.Collectors.toList());
    }
}