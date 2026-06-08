package com.payment_wallet.reward_service.dto;

/** Aggregated rewards view for a user: total points, how many rewards, and a loyalty tier. */
public record RewardSummary(Long userId, double totalPoints, long rewardCount, String tier) {

    public static String tierFor(double totalPoints) {
        if (totalPoints >= 500) return "GOLD";
        if (totalPoints >= 100) return "SILVER";
        return "BRONZE";
    }
}
