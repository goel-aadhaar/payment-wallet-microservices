package com.payment_wallet.reward_service.controller;

import com.payment_wallet.reward_service.entity.Reward;
import com.payment_wallet.reward_service.service.RewardService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.Operation;

import java.util.List;

@RestController
@RequestMapping("/api/v1/rewards")
public class RewardController {

    private final RewardService rewardService;

    public RewardController(RewardService rewardService) {
        this.rewardService = rewardService;
    }

    @Operation(summary = "Get All Rewards", description = "Retrieve a list of all distributed rewards")
    @GetMapping
    public ResponseEntity<List<Reward>> getAllRewards() {
        return ResponseEntity.ok(rewardService.getAllRewards());
    }

    @Operation(summary = "Get Rewards by User", description = "Retrieve cashback or rewards earned by a specific user")
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<Reward>> getRewardsByUserId(@PathVariable Long userId) {
        return ResponseEntity.ok(rewardService.getRewardByUserId(userId));
    }
}
