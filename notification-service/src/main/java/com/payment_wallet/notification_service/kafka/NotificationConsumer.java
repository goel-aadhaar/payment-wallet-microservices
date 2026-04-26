package com.payment_wallet.notification_service.kafka;

import com.payment_wallet.notification_service.dto.TransactionEvent;
import com.payment_wallet.notification_service.entity.Notification;
import com.payment_wallet.notification_service.repository.NotificationRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class NotificationConsumer {

    private static final Logger log = LoggerFactory.getLogger(NotificationConsumer.class);

    private final NotificationRepository notificationRepository;

    public NotificationConsumer(NotificationRepository notificationRepository) {
        this.notificationRepository = notificationRepository;
    }

    @KafkaListener(topics = "txn-initiated", groupId = "notification-group")
    public void consumeTransaction(TransactionEvent transaction) {

        String notify = "$ " + transaction.getAmount() + " received from user " + transaction.getSenderId();

        Notification notification = Notification.builder()
                .userId(transaction.getReceiverId())
                .message(notify)
                .sentAt(LocalDateTime.now())
                .build();

        notificationRepository.save(notification);

        log.info("Notification saved for userId: {}, message: {}", transaction.getReceiverId(), notify);
    }
}
