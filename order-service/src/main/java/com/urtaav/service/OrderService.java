package com.urtaav.service;

import com.urtaav.event.OrderNotification;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.UUID;
@Service
@Slf4j
public class OrderService {

    // Kafka template for sending simple String messages
    private final KafkaTemplate<String, String> kafkaTemplate;

    // Kafka template for sending custom object messages (OrderNotification)
    private final KafkaTemplate<String, OrderNotification> kafkaTemplateOrderEmail;
    private final long startTime = System.currentTimeMillis();

    // Constructor-based dependency injection
    public OrderService(KafkaTemplate<String, String> kafkaTemplate,
                        KafkaTemplate<String, OrderNotification> kafkaTemplateOrderEmail) {
        this.kafkaTemplate = kafkaTemplate;
        this.kafkaTemplateOrderEmail = kafkaTemplateOrderEmail;
    }

    // Scheduled task that runs every 2 seconds
    // Simulates a new order being placed and sends data to Kafka topics
    @Scheduled(fixedRate = 2000)
    public void processOrder() {
        if (System.currentTimeMillis() - startTime > 240_000) {
            log.info("Stopping order processing after 4 minutes");
            return;
        }

        final String orderId = UUID.randomUUID().toString();
        log.info("Processing order id: {}", orderId);

        // Send the order ID to the "order-placed" topic
        kafkaTemplate.send("order-placed", orderId);
        log.info("Message sent to Kafka topic: order-placed with id: {}", orderId);

        // Trigger email notification
        processOrderNotification(orderId);
    }

    // Builds and sends a detailed notification to a different Kafka topic
    private void processOrderNotification(final String orderId) {
        log.info("Sending Order notification for order id: {}", orderId);

        final OrderNotification orderNotification = OrderNotification.builder()
                .orderId(orderId)
                .orderStatus("PLACED")
                .userId("urtaav")
                .price(100.0)
                .productName("Laptop Dell Gamer")
                .quantity(1)
                .build();

        // Send the full order notification object to Kafka
        kafkaTemplateOrderEmail.send("order-placed-email", orderNotification);
        log.info("Message sent to Kafka topic: order-placed-email with orderId: {}", orderId);
    }
}
