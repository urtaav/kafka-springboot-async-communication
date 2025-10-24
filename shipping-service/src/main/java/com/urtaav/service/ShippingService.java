package com.urtaav.service;

import com.urtaav.event.PaymentEvent;
import com.urtaav.event.ShippingEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@Slf4j
public class ShippingService {

    private final KafkaTemplate<String, ShippingEvent> kafkaTemplate;

    public ShippingService(KafkaTemplate<String, ShippingEvent> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    @KafkaListener(topics = "order-paid", groupId = "shipping-service")
    public void processShipping(PaymentEvent event) {
        log.info("Preparing shipment for order: {}", event.getOrderId());

        final ShippingEvent shippingEvent = ShippingEvent.builder()
                .orderId(event.getOrderId())
                .shippingStatus("SHIPPED")
                .shippedDate(LocalDateTime.now())
                .build();

        kafkaTemplate.send("order-shipped", shippingEvent);
        log.info("Shipping event sent: {}", event.getOrderId());
    }
}
