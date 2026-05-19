package com.payment_wallet.transaction_service.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Positive;

import java.time.LocalDateTime;
import io.swagger.v3.oas.annotations.media.Schema;

@Entity
@Table(name = "transaction")
public class Transaction {

    @Schema(description = "Unique transaction identifier", example = "500")
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Schema(description = "ID of the user sending the money", example = "1")
    @Column(name = "sender_id", nullable = false)
    private Long senderId;

    @Schema(description = "ID of the user receiving the money", example = "2")
    @Column(name = "receiver_id", nullable = false)
    private Long receiverId;

    @Schema(description = "Transfer amount in INR", example = "150.0")
    @Column(nullable = false)
    @Positive(message = "Amount must be positive")
    private Double amount;

    @Schema(description = "Time the transaction was initiated", example = "2026-05-03T12:00:00")
    @Column(nullable = false)
    private LocalDateTime timestamp;

    @Schema(description = "Current status of the transaction", example = "SUCCESS")
    @Column(nullable = false)
    private String status;

    @PrePersist
    public void prePersist() {
        if(timestamp == null) {
            timestamp = LocalDateTime.now();
        }
        if(status == null) {
            status = "PENDING";
        }
    }

    @Override
    public String toString() {
        return "Transaction{" +
                "id=" + id +
                ", senderId=" + senderId +
                ", receiverId=" + receiverId +
                ", amount=" + amount +
                ", timestamp=" + timestamp +
                ", status='" + status + '\'' +
                '}';
    }
    public Transaction() {}

    public Transaction(Long id, Long senderId, Long receiverId, Double amount, LocalDateTime timestamp, String status) {
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

    public static TransactionBuilder builder() {
        return new TransactionBuilder();
    }

    public static class TransactionBuilder {
        private Long id;
        private Long senderId;
        private Long receiverId;
        private Double amount;
        private LocalDateTime timestamp;
        private String status;
        public TransactionBuilder id(Long id) {
            this.id = id;
            return this;
        }
        public TransactionBuilder senderId(Long senderId) {
            this.senderId = senderId;
            return this;
        }
        public TransactionBuilder receiverId(Long receiverId) {
            this.receiverId = receiverId;
            return this;
        }
        public TransactionBuilder amount(Double amount) {
            this.amount = amount;
            return this;
        }
        public TransactionBuilder timestamp(LocalDateTime timestamp) {
            this.timestamp = timestamp;
            return this;
        }
        public TransactionBuilder status(String status) {
            this.status = status;
            return this;
        }
        public Transaction build() {
            return new Transaction(id, senderId, receiverId, amount, timestamp, status);
        }
    }
}
