package com.payment_wallet.wallet_service.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public class CreditRequest {
    @Schema(description = "ID of the user whose wallet to credit", example = "1")
    @NotNull(message = "userId is required")
    private Long userId;
    @Schema(description = "Amount to credit", example = "1000")
    @NotNull(message = "amount is required")
    @Positive(message = "amount must be positive")
    private Long amount;
    @Schema(description = "Currency of the amount", example = "INR")
    private String currency;
    public CreditRequest() {}

    public CreditRequest(Long userId, Long amount, String currency) {
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

    public static CreditRequestBuilder builder() {
        return new CreditRequestBuilder();
    }

    public static class CreditRequestBuilder {
        private Long userId;
        private Long amount;
        private String currency;
        public CreditRequestBuilder userId(Long userId) {
            this.userId = userId;
            return this;
        }
        public CreditRequestBuilder amount(Long amount) {
            this.amount = amount;
            return this;
        }
        public CreditRequestBuilder currency(String currency) {
            this.currency = currency;
            return this;
        }
        public CreditRequest build() {
            return new CreditRequest(userId, amount, currency);
        }
    }
}
