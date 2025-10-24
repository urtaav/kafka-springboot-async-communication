package com.urtaav.event;

import lombok.*;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PaymentEvent {
    private String orderId;
    private String paymentStatus;
    private Double amount;
    private LocalDateTime paymentDate;
}
