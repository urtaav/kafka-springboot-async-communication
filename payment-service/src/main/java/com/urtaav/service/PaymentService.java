package com.urtaav.service;

import com.urtaav.event.PaymentEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@Slf4j
public class PaymentService {

    private final KafkaTemplate<String, PaymentEvent> kafkaTemplate;

    public PaymentService(KafkaTemplate<String, PaymentEvent> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    @KafkaListener(topics = "order-placed", groupId = "payment-service")
    public void processPayment(String orderId) {
        log.info("Processing payment for order: {}", orderId);

        final PaymentEvent event = PaymentEvent.builder()
                .orderId(orderId)
                .paymentStatus("PAID")
                .amount(999.99)
                .paymentDate(LocalDateTime.now())
                .build();

        kafkaTemplate.send("order-paid", event);
        log.info("Payment processed and event sent: {}", orderId);
    }
}
