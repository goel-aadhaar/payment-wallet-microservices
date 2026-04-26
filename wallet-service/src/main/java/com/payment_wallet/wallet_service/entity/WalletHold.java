package com.payment_wallet.wallet_service.entity;

import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "wallet_holds")
public class WalletHold {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "wallet_id")
    private Wallet wallet;

    @Column(nullable = false, unique = true)
    private String holdReference;

    @Column(nullable = false)
    private Long amount;

    @Column(nullable = false)
    private String status = "ACTIVE";

    private LocalDateTime createdAt = LocalDateTime.now();

    private LocalDateTime expiresAt;
    public WalletHold() {}

    public WalletHold(Long id, Wallet wallet, String holdReference, Long amount, String status, LocalDateTime createdAt, LocalDateTime expiresAt) {
        this.id = id;
        this.wallet = wallet;
        this.holdReference = holdReference;
        this.amount = amount;
        this.status = status;
        this.createdAt = createdAt;
        this.expiresAt = expiresAt;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Wallet getWallet() {
        return wallet;
    }

    public void setWallet(Wallet wallet) {
        this.wallet = wallet;
    }

    public String getHoldReference() {
        return holdReference;
    }

    public void setHoldReference(String holdReference) {
        this.holdReference = holdReference;
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

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getExpiresAt() {
        return expiresAt;
    }

    public void setExpiresAt(LocalDateTime expiresAt) {
        this.expiresAt = expiresAt;
    }

    public static WalletHoldBuilder builder() {
        return new WalletHoldBuilder();
    }

    public static class WalletHoldBuilder {
        private Long id;
        private Wallet wallet;
        private String holdReference;
        private Long amount;
        private String status;
        private LocalDateTime createdAt;
        private LocalDateTime expiresAt;
        public WalletHoldBuilder id(Long id) {
            this.id = id;
            return this;
        }
        public WalletHoldBuilder wallet(Wallet wallet) {
            this.wallet = wallet;
            return this;
        }
        public WalletHoldBuilder holdReference(String holdReference) {
            this.holdReference = holdReference;
            return this;
        }
        public WalletHoldBuilder amount(Long amount) {
            this.amount = amount;
            return this;
        }
        public WalletHoldBuilder status(String status) {
            this.status = status;
            return this;
        }
        public WalletHoldBuilder createdAt(LocalDateTime createdAt) {
            this.createdAt = createdAt;
            return this;
        }
        public WalletHoldBuilder expiresAt(LocalDateTime expiresAt) {
            this.expiresAt = expiresAt;
            return this;
        }
        public WalletHold build() {
            return new WalletHold(id, wallet, holdReference, amount, status, createdAt, expiresAt);
        }
    }
}
