package com.urtaav.event;

import lombok.*;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ShippingEvent {
    private String orderId;
    private String shippingStatus;
    private LocalDateTime shippedDate;
}
