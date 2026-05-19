package com.payment_wallet.reward_service.entity;

import jakarta.persistence.*;

import java.time.LocalDateTime;
import io.swagger.v3.oas.annotations.media.Schema;

@Entity
@Table(name = "rewards")
public class Reward {
    @Schema(description = "Unique reward identifier", example = "300")
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Schema(description = "ID of the user receiving the reward", example = "1")
    private Long userId;

    @Schema(description = "Amount of reward points/cashback", example = "5.0")
    private Double points;

    @Schema(description = "Time the reward was granted", example = "2026-05-03T12:00:02")
    private LocalDateTime sentAt;

    @Schema(description = "ID of the transaction that triggered this reward", example = "500")
    @Column(unique = true)
    private Long transactionId;
    public Reward() {}

    public Reward(Long id, Long userId, Double points, LocalDateTime sentAt, Long transactionId) {
        this.id = id;
        this.userId = userId;
        this.points = points;
        this.sentAt = sentAt;
        this.transactionId = transactionId;
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

    public Double getPoints() {
        return points;
    }

    public void setPoints(Double points) {
        this.points = points;
    }

    public LocalDateTime getSentAt() {
        return sentAt;
    }

    public void setSentAt(LocalDateTime sentAt) {
        this.sentAt = sentAt;
    }

    public Long getTransactionId() {
        return transactionId;
    }

    public void setTransactionId(Long transactionId) {
        this.transactionId = transactionId;
    }

    public static RewardBuilder builder() {
        return new RewardBuilder();
    }

    public static class RewardBuilder {
        private Long id;
        private Long userId;
        private Double points;
        private LocalDateTime sentAt;
        private Long transactionId;
        public RewardBuilder id(Long id) {
            this.id = id;
            return this;
        }
        public RewardBuilder userId(Long userId) {
            this.userId = userId;
            return this;
        }
        public RewardBuilder points(Double points) {
            this.points = points;
            return this;
        }
        public RewardBuilder sentAt(LocalDateTime sentAt) {
            this.sentAt = sentAt;
            return this;
        }
        public RewardBuilder transactionId(Long transactionId) {
            this.transactionId = transactionId;
            return this;
        }
        public Reward build() {
            return new Reward(id, userId, points, sentAt, transactionId);
        }
    }
}
