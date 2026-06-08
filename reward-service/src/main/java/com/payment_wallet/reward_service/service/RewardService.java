package com.payment_wallet.reward_service.service;

import com.payment_wallet.reward_service.dto.RewardSummary;
import com.payment_wallet.reward_service.entity.Reward;

import java.util.List;

public interface RewardService {
    Reward sendReward(Reward reward);

    List<Reward> getRewardByUserId(Long userId);

    List<Reward> getAllRewards();

    RewardSummary getSummary(Long userId);
}
