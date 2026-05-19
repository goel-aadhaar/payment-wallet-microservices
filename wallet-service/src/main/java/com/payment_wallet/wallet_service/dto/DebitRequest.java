package com.payment_wallet.wallet_service.dto;

import io.swagger.v3.oas.annotations.media.Schema;

public class DebitRequest {
    @Schema(description = "ID of the user whose wallet to debit", example = "1")
    private Long userId;
    @Schema(description = "Amount to debit", example = "500")
    private Long amount;
    @Schema(description = "Currency of the amount", example = "INR")
    private String currency;
    public DebitRequest() {}

    public DebitRequest(Long userId, Long amount, String currency) {
        this.userId = userId;
        this.amount = amount;
        this.currency = currency;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Long getAmount() {
        return amount;
    }

    public void setAmount(Long amount) {
        this.amount = amount;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public static DebitRequestBuilder builder() {
        return new DebitRequestBuilder();
    }

    public static class DebitRequestBuilder {
        private Long userId;
        private Long amount;
        private String currency;
        public DebitRequestBuilder userId(Long userId) {
            this.userId = userId;
            return this;
        }
        public DebitRequestBuilder amount(Long amount) {
            this.amount = amount;
            return this;
        }
        public DebitRequestBuilder currency(String currency) {
            this.currency = currency;
            return this;
        }
        public DebitRequest build() {
            return new DebitRequest(userId, amount, currency);
        }
    }
}
