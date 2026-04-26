package com.payment_wallet.reward_service.service;

import com.payment_wallet.reward_service.entity.Reward;
import com.payment_wallet.reward_service.repository.RewardRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class RewardServiceImpl implements RewardService {

    private final RewardRepository rewardRepository;

    public RewardServiceImpl(RewardRepository rewardRepository) {
        this.rewardRepository = rewardRepository;
    }

    @Override
    public Reward sendReward(Reward reward) {
        reward.setSentAt(LocalDateTime.now());
        return rewardRepository.save(reward);
    }

    @Override
    public List<Reward> getRewardByUserId(Long userId) {
        return rewardRepository.findByUserId(userId);
    }

    @Override
    public List<Reward> getAllRewards() {
        return rewardRepository.findAll();
    }
}
