package com.urtaav.service;

import com.urtaav.event.OrderNotification;
import com.urtaav.event.ShippingEvent;
import jakarta.mail.internet.MimeMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class NotificationService {

    private final JavaMailSender mailSender;

    public NotificationService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }


    // Kafka listener that consumes plain String messages from the "order-placed" topic
    @KafkaListener(topics = "order-placed", groupId = "notification-service")
    public void getOrderNumber(String orderId) {
        log.info("Received order id: {}", orderId);
    }

    // Kafka listener that consumes structured OrderNotification messages
    @KafkaListener(topics = "order-placed-email", groupId = "notification-service")
    public void getOrderNotification(OrderNotification orderNotification) {
        log.info("Received order notification id: {}", orderNotification.getOrderId());

        // Here you could call an email service or push notification system
        log.info("Notification Sent: {}", orderNotification.toString());
    }

    @KafkaListener(topics = "order-shipped", groupId = "notification-service")
    public void sendEmailNotification(ShippingEvent event) {
        log.info("Received shipping event: {}", event.getOrderId());
        sendMail(event);
    }

    private void sendMail(ShippingEvent event) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            String htmlContent = String.format("""
    <html>
        <body style="font-family: Arial, sans-serif; background-color: #f9f9f9; padding: 20px;">
            <div style="max-width: 600px; margin: 0 auto; background-color: #ffffff; border-radius: 10px; box-shadow: 0 2px 8px rgba(0,0,0,0.1); overflow: hidden;">
                <div style="background-color: #007BFF; color: white; padding: 15px; text-align: center;">
                    <h2>Your Order Has Been Shipped! 🚚</h2>
                </div>
                <div style="padding: 20px; color: #333333;">
                    <p>Dear <b>Customer</b>,</p>
                    <p>We are happy to inform you that your order has been successfully shipped!</p>

                    <table style="width:100%%; border-collapse: collapse; margin-top: 20px;">
                        <tr>
                            <td style="padding: 8px; border-bottom: 1px solid #ddd;"><b>Order ID:</b></td>
                            <td style="padding: 8px; border-bottom: 1px solid #ddd;">%s</td>
                        </tr>
                        <tr>
                            <td style="padding: 8px;"><b>Status:</b></td>
                            <td style="padding: 8px;">%s</td>
                        </tr>
                    </table>

                    <p style="margin-top: 20px;">You can track your order with the tracking number above.</p>
                    <p>Thank you for shopping with us! 🙌</p>
                </div>
                <div style="background-color: #f1f1f1; text-align: center; padding: 10px; font-size: 12px; color: #777;">
                    © 2025 URTAAV Store | This email was sent automatically, please do not reply.
                </div>
            </div>
        </body>
    </html>
    """, event.getOrderId(), event.getShippingStatus());

            helper.setTo("customer@example.com");
            helper.setSubject("📦 Your Order Has Been Shipped!");
            helper.setText(htmlContent, true); // true = HTML content

            mailSender.send(message);
            log.info("✅ Email sent successfully for order: {}", event.getOrderId());

        } catch (Exception e) {
            log.error("❌ Failed to send email for order {}: {}", event.getOrderId(), e.getMessage());
        }
    }

}
