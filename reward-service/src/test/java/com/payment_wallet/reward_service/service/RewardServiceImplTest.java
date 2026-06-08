package com.payment_wallet.reward_service.service;

import com.payment_wallet.reward_service.dto.RewardSummary;
import com.payment_wallet.reward_service.entity.Reward;
import com.payment_wallet.reward_service.repository.RewardRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RewardServiceImplTest {

    @Mock RewardRepository rewardRepository;
    @InjectMocks RewardServiceImpl service;

    @Test
    void getSummary_silverTier() {
        when(rewardRepository.sumPointsByUserId(1L)).thenReturn(120.0);
        when(rewardRepository.countByUserId(1L)).thenReturn(3L);

        RewardSummary summary = service.getSummary(1L);

        assertThat(summary.totalPoints()).isEqualTo(120.0);
        assertThat(summary.rewardCount()).isEqualTo(3L);
        assertThat(summary.tier()).isEqualTo("SILVER");
    }

    @Test
    void getSummary_noRewards_isBronze() {
        when(rewardRepository.sumPointsByUserId(1L)).thenReturn(0.0);
        when(rewardRepository.countByUserId(1L)).thenReturn(0L);

        assertThat(service.getSummary(1L).tier()).isEqualTo("BRONZE");
    }

    @Test
    void tierFor_goldThreshold() {
        assertThat(RewardSummary.tierFor(500)).isEqualTo("GOLD");
        assertThat(RewardSummary.tierFor(499.99)).isEqualTo("SILVER");
    }

    @Test
    void sendReward_setsSentAt() {
        when(rewardRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        Reward reward = service.sendReward(Reward.builder().userId(1L).points(5.0).build());

        assertThat(reward.getSentAt()).isNotNull();
    }
}
