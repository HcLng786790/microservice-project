package com.huuduc.notificationservice.model;

import com.huuduc.notificationservice.dto.OrderConfirmation;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
@Document
public class Notification {

    @Id
    private String id;
    private LocalDateTime notificationDate;
    private OrderConfirmation orderConfirmation;
}
