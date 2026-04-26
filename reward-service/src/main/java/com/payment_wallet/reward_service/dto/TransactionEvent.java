package com.payment_wallet.reward_service.dto;


import java.time.LocalDateTime;

/**
 * DTO representing a transaction event received from Kafka.
 * This is NOT a JPA entity — it's a lightweight data carrier for Kafka deserialization.
 */
public class TransactionEvent {

    private Long id;
    private Long senderId;
    private Long receiverId;
    private Double amount;
    private LocalDateTime timestamp;
    private String status;
    public TransactionEvent() {}

    public TransactionEvent(Long id, Long senderId, Long receiverId, Double amount, LocalDateTime timestamp, String status) {
        this.id = id;
        this.senderId = senderId;
        this.receiverId = receiverId;
        this.amount = amount;
        this.timestamp = timestamp;
        this.status = status;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getSenderId() {
        return senderId;
    }

    public void setSenderId(Long senderId) {
        this.senderId = senderId;
    }

    public Long getReceiverId() {
        return receiverId;
    }

    public void setReceiverId(Long receiverId) {
        this.receiverId = receiverId;
    }

    public Double getAmount() {
        return amount;
    }

    public void setAmount(Double amount) {
        this.amount = amount;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public static TransactionEventBuilder builder() {
        return new TransactionEventBuilder();
    }

    public static class TransactionEventBuilder {
        private Long id;
        private Long senderId;
        private Long receiverId;
        private Double amount;
        private LocalDateTime timestamp;
        private String status;
        public TransactionEventBuilder id(Long id) {
            this.id = id;
            return this;
        }
        public TransactionEventBuilder senderId(Long senderId) {
            this.senderId = senderId;
            return this;
        }
        public TransactionEventBuilder receiverId(Long receiverId) {
            this.receiverId = receiverId;
            return this;
        }
        public TransactionEventBuilder amount(Double amount) {
            this.amount = amount;
            return this;
        }
        public TransactionEventBuilder timestamp(LocalDateTime timestamp) {
            this.timestamp = timestamp;
            return this;
        }
        public TransactionEventBuilder status(String status) {
            this.status = status;
            return this;
        }
        public TransactionEvent build() {
            return new TransactionEvent(id, senderId, receiverId, amount, timestamp, status);
        }
    }
}
