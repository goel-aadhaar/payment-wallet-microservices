package com.payment_wallet.reward_service.kafka;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.payment_wallet.reward_service.entity.Reward;
import com.payment_wallet.reward_service.repository.RewardRepository;
import com.payment_wallet.reward_service.entity.Transaction;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class RewardConsumer {

    private final RewardRepository rewardRepository;

    private final ObjectMapper mapper;


    public RewardConsumer(RewardRepository rewardRepository, ObjectMapper mapper) {
        this.rewardRepository = rewardRepository;
        this.mapper = new ObjectMapper();
        this.mapper.registerModule(new JavaTimeModule());
        this.mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
    }

    @KafkaListener(topics = "txn-initiated", groupId = "reward-group")
    public void consumerTransaction(Transaction transaction) {
        try {
            if(rewardRepository.existsByTransactionId(transaction.getId())) {
                System.out.println("Reward already exists for transaction: " + transaction.getId());
                return;
            }
            Reward reward = Reward
                    .builder()
                    .userId(transaction.getSenderId())
                    .points(transaction.getAmount() * 100)
                    .sentAt(LocalDateTime.now())
                    .transactionId(transaction.getId())
                    .build();
            rewardRepository.save(reward);
            System.out.println("Reward saved: " + reward);
        } catch (Exception e) {
            System.err.println("Failed to process transaction " + transaction.getId() + ": " + e.getMessage());
            throw e;
        }
    }
}
