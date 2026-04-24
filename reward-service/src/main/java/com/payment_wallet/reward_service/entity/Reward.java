package com.payment_wallet.reward_service.entity;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Entity
@Data
@Builder
@Table(name = "reward")
public class Reward {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long userId;

    private Double points;

    private LocalDateTime sentAt;

    @Column(unique = true)
    private Long transactionId;
}
