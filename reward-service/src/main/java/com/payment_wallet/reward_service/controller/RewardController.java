package com.payment_wallet.reward_service.controller;

import com.payment_wallet.reward_service.entity.Reward;
import com.payment_wallet.reward_service.service.RewardService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/rewards")
public class RewardController {

    private final RewardService rewardService;

    public RewardController(RewardService rewardService) {
        this.rewardService = rewardService;
    }

    @GetMapping
    public ResponseEntity<List<Reward>> getAllRewards() {
        return ResponseEntity.ok(rewardService.getAllRewards());
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<Reward>> getRewardsByUserId(@PathVariable Long userId) {
        return ResponseEntity.ok(rewardService.getRewardByUserId(userId));
    }
}
