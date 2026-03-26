package com.payment_wallet.notification_service.kafka;

import com.payment_wallet.notification_service.entity.Notification;
import com.payment_wallet.notification_service.entity.Transaction;
import com.payment_wallet.notification_service.repository.NotificationRepository;
import lombok.AllArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import tools.jackson.databind.ObjectMapper;

import java.time.LocalDateTime;

@Component
@AllArgsConstructor
public class NotificationConsumer {

    private final NotificationRepository notificationRepository;

    private final ObjectMapper mapper;

    @KafkaListener(topics = "txn-initiated", groupId = "notification-group")
    public void consumeTransaction(Transaction transaction) {
        Transaction txn = mapper.readValue(message, Transaction.class);
        Long receiverUserId = txn.getReceiverId();
        Long senderUserId = txn.getSenderId();

        String notify = "$ " + txn.getAmount() + "received from " + txn.getSenderId();

        Notification notification = Notification
                .builder()
                .message(notify)
                .sentAt(LocalDateTime.now())
                .build();

        notificationRepository.save(notification);
    }
}
