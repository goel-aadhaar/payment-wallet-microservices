package com.payment_wallet.wallet_service.dto;


public class CreditRequest {
    private Long userId;
    private Long amount;
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
