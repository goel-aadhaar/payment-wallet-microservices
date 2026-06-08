package com.payment_wallet.notification_service.entity;

import jakarta.persistence.*;

import java.time.LocalDateTime;
import io.swagger.v3.oas.annotations.media.Schema;

@Entity
@Table(name = "notifications")
public class Notification {

    @Schema(description = "Unique notification identifier", example = "200")
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Schema(description = "ID of the user the notification belongs to", example = "1")
    private Long userId;

    @Schema(description = "Notification message content", example = "You received 150.00 from account #2")
    @Column(length = 500)
    private String message;

    @Schema(description = "Category of the notification", example = "TRANSFER_RECEIVED")
    @Enumerated(EnumType.STRING)
    @Column(length = 40)
    private NotificationType type;

    @Schema(description = "Whether the user has read this notification", example = "false")
    @Column(name = "is_read", nullable = false)
    private boolean read;

    @Schema(description = "When the notification was marked read", example = "2026-05-03T12:05:00")
    private LocalDateTime readAt;

    @Schema(description = "Source transaction id, if any (used for de-duplication)", example = "500")
    private Long transactionId;

    @Schema(description = "When the notification was created", example = "2026-05-03T12:00:01")
    private LocalDateTime sentAt;

    public Notification() {}

    public Notification(Long id, Long userId, String message, NotificationType type,
                        boolean read, LocalDateTime readAt, Long transactionId, LocalDateTime sentAt) {
        this.id = id;
        this.userId = userId;
        this.message = message;
        this.type = type;
        this.read = read;
        this.readAt = readAt;
        this.transactionId = transactionId;
        this.sentAt = sentAt;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }

    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }

    public NotificationType getType() { return type; }
    public void setType(NotificationType type) { this.type = type; }

    public boolean isRead() { return read; }
    public void setRead(boolean read) { this.read = read; }

    public LocalDateTime getReadAt() { return readAt; }
    public void setReadAt(LocalDateTime readAt) { this.readAt = readAt; }

    public Long getTransactionId() { return transactionId; }
    public void setTransactionId(Long transactionId) { this.transactionId = transactionId; }

    public LocalDateTime getSentAt() { return sentAt; }
    public void setSentAt(LocalDateTime sentAt) { this.sentAt = sentAt; }

    public static NotificationBuilder builder() {
        return new NotificationBuilder();
    }

    public static class NotificationBuilder {
        private Long id;
        private Long userId;
        private String message;
        private NotificationType type;
        private boolean read;
        private LocalDateTime readAt;
        private Long transactionId;
        private LocalDateTime sentAt;

        public NotificationBuilder id(Long id) { this.id = id; return this; }
        public NotificationBuilder userId(Long userId) { this.userId = userId; return this; }
        public NotificationBuilder message(String message) { this.message = message; return this; }
        public NotificationBuilder type(NotificationType type) { this.type = type; return this; }
        public NotificationBuilder read(boolean read) { this.read = read; return this; }
        public NotificationBuilder readAt(LocalDateTime readAt) { this.readAt = readAt; return this; }
        public NotificationBuilder transactionId(Long transactionId) { this.transactionId = transactionId; return this; }
        public NotificationBuilder sentAt(LocalDateTime sentAt) { this.sentAt = sentAt; return this; }

        public Notification build() {
            return new Notification(id, userId, message, type, read, readAt, transactionId, sentAt);
        }
    }
}
