package com.payment_wallet.notification_service.entity;

import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "notifications")
public class Notification {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long userId;

    private String message;

    private LocalDateTime sentAt;
    public Notification() {}

    public Notification(Long id, Long userId, String message, LocalDateTime sentAt) {
        this.id = id;
        this.userId = userId;
        this.message = message;
        this.sentAt = sentAt;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public LocalDateTime getSentAt() {
        return sentAt;
    }

    public void setSentAt(LocalDateTime sentAt) {
        this.sentAt = sentAt;
    }

    public static NotificationBuilder builder() {
        return new NotificationBuilder();
    }

    public static class NotificationBuilder {
        private Long id;
        private Long userId;
        private String message;
        private LocalDateTime sentAt;
        public NotificationBuilder id(Long id) {
            this.id = id;
            return this;
        }
        public NotificationBuilder userId(Long userId) {
            this.userId = userId;
            return this;
        }
        public NotificationBuilder message(String message) {
            this.message = message;
            return this;
        }
        public NotificationBuilder sentAt(LocalDateTime sentAt) {
            this.sentAt = sentAt;
            return this;
        }
        public Notification build() {
            return new Notification(id, userId, message, sentAt);
        }
    }
}
