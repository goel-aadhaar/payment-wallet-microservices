package com.payment_wallet.reward_service.kafka;

import com.payment_wallet.reward_service.dto.TransactionEvent;
import com.payment_wallet.reward_service.entity.Reward;
import com.payment_wallet.reward_service.repository.RewardRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class RewardConsumer {

    private static final Logger log = LoggerFactory.getLogger(RewardConsumer.class);

    private final RewardRepository rewardRepository;

    public RewardConsumer(RewardRepository rewardRepository) {
        this.rewardRepository = rewardRepository;
    }

    @KafkaListener(topics = "txn-initiated", groupId = "reward-group")
    public void consumeTransaction(TransactionEvent transaction) {
        try {
            if (rewardRepository.existsByTransactionId(transaction.getId())) {
                log.info("Reward already exists for transaction: {}", transaction.getId());
                return;
            }

            Reward reward = Reward.builder()
                    .userId(transaction.getSenderId())
                    .points(transaction.getAmount() * 0.01)
                    .sentAt(LocalDateTime.now())
                    .transactionId(transaction.getId())
                    .build();

            rewardRepository.save(reward);
            log.info("Reward saved: {} points for user {} (txn: {})",
                    reward.getPoints(), reward.getUserId(), reward.getTransactionId());
        } catch (Exception e) {
            log.error("Failed to process transaction {}: {}", transaction.getId(), e.getMessage(), e);
            throw e;
        }
    }
}
