package com.payment_wallet.wallet_service.dto;


public class HoldRequest {
    private Long userId;
    private Long amount;
    private String currency;
    public HoldRequest() {}

    public HoldRequest(Long userId, Long amount, String currency) {
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

    public static HoldRequestBuilder builder() {
        return new HoldRequestBuilder();
    }

    public static class HoldRequestBuilder {
        private Long userId;
        private Long amount;
        private String currency;
        public HoldRequestBuilder userId(Long userId) {
            this.userId = userId;
            return this;
        }
        public HoldRequestBuilder amount(Long amount) {
            this.amount = amount;
            return this;
        }
        public HoldRequestBuilder currency(String currency) {
            this.currency = currency;
            return this;
        }
        public HoldRequest build() {
            return new HoldRequest(userId, amount, currency);
        }
    }
}
