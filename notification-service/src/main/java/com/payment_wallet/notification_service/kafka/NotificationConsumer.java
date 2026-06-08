package com.payment_wallet.notification_service.kafka;

import com.payment_wallet.notification_service.dto.TransactionEvent;
import com.payment_wallet.notification_service.entity.Notification;
import com.payment_wallet.notification_service.entity.NotificationType;
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
    public void consumeTransaction(TransactionEvent txn) {
        // Notify both parties; idempotent so Kafka redelivery never creates duplicates.
        notifyOnce(txn.getId(), txn.getReceiverId(), NotificationType.TRANSFER_RECEIVED,
                "You received " + txn.getAmount() + " from account #" + txn.getSenderId());
        notifyOnce(txn.getId(), txn.getSenderId(), NotificationType.TRANSFER_SENT,
                "You sent " + txn.getAmount() + " to account #" + txn.getReceiverId());
    }

    private void notifyOnce(Long transactionId, Long userId, NotificationType type, String message) {
        if (transactionId != null && notificationRepository.existsByTransactionIdAndUserId(transactionId, userId)) {
            log.debug("Notification already exists for txn {} / user {}", transactionId, userId);
            return;
        }
        notificationRepository.save(Notification.builder()
                .userId(userId)
                .type(type)
                .message(message)
                .transactionId(transactionId)
                .read(false)
                .sentAt(LocalDateTime.now())
                .build());
        log.info("Notification saved for userId {} (txn {}, type {})", userId, transactionId, type);
    }
}
