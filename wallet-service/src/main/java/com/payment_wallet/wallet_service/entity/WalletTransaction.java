package com.payment_wallet.wallet_service.entity;

import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "wallet_transactions")
public class WalletTransaction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "wallet_id", nullable = false)
    private Long walletId;

    @Column(nullable = false)
    private String type;

    @Column(nullable = false)
    private Long amount;

    @Column(nullable = false)
    private String status;

    @Column(nullable = false)
    private LocalDateTime timestamp;

    @PrePersist
    public void prePersist() {
        if (timestamp == null) {
            timestamp = LocalDateTime.now();
        }
    }
    public WalletTransaction() {}

    public WalletTransaction(Long id, Long walletId, String type, Long amount, String status, LocalDateTime timestamp) {
        this.id = id;
        this.walletId = walletId;
        this.type = type;
        this.amount = amount;
        this.status = status;
        this.timestamp = timestamp;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getWalletId() {
        return walletId;
    }

    public void setWalletId(Long walletId) {
        this.walletId = walletId;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Long getAmount() {
        return amount;
    }

    public void setAmount(Long amount) {
        this.amount = amount;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }

    public static WalletTransactionBuilder builder() {
        return new WalletTransactionBuilder();
    }

    public static class WalletTransactionBuilder {
        private Long id;
        private Long walletId;
        private String type;
        private Long amount;
        private String status;
        private LocalDateTime timestamp;
        public WalletTransactionBuilder id(Long id) {
            this.id = id;
            return this;
        }
        public WalletTransactionBuilder walletId(Long walletId) {
            this.walletId = walletId;
            return this;
        }
        public WalletTransactionBuilder type(String type) {
            this.type = type;
            return this;
        }
        public WalletTransactionBuilder amount(Long amount) {
            this.amount = amount;
            return this;
        }
        public WalletTransactionBuilder status(String status) {
            this.status = status;
            return this;
        }
        public WalletTransactionBuilder timestamp(LocalDateTime timestamp) {
            this.timestamp = timestamp;
            return this;
        }
        public WalletTransaction build() {
            return new WalletTransaction(id, walletId, type, amount, status, timestamp);
        }
    }
}
