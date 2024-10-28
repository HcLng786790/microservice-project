package com.huuduc.notificationservice.kafka;

import com.huuduc.notificationservice.dto.OrderConfirmation;
import com.huuduc.notificationservice.email.EmailService;
import com.huuduc.notificationservice.model.Notification;
import com.huuduc.notificationservice.repository.NotificationRepository;
import jakarta.mail.MessagingException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@SuppressWarnings("unused")
@Service
@RequiredArgsConstructor
@Slf4j
public class NotificationConsumer {

    private final NotificationRepository notificationRepository;
    private final EmailService emailService;

    @KafkaListener(topics = "order-topic", groupId = "orderGroup")
    public void consumeOrderSuccessNotification(OrderConfirmation orderConfirmation) throws MessagingException {

        log.info("Consuming the message from order-topic:: {}", orderConfirmation);
        this.notificationRepository.save(
                Notification.builder()
                        .notificationDate(LocalDateTime.now())
                        .orderConfirmation(orderConfirmation)
                        .build()
        );

        var username = orderConfirmation.userResponse().username();
        this.emailService.sendOrderConfirmationEmail(
                username,
                orderConfirmation.totalPrice(),
                orderConfirmation.orderNumber(),
                orderConfirmation.purchaseResponseList()
        );
    }
}
