package com.payment_wallet.user_service.dto;


public class CreateWalletRequest {
    private Long userId;

    private String currency;
    public CreateWalletRequest() {}

    public CreateWalletRequest(Long userId, String currency) {
        this.userId = userId;
        this.currency = currency;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public static CreateWalletRequestBuilder builder() {
        return new CreateWalletRequestBuilder();
    }

    public static class CreateWalletRequestBuilder {
        private Long userId;
        private String currency;
        public CreateWalletRequestBuilder userId(Long userId) {
            this.userId = userId;
            return this;
        }
        public CreateWalletRequestBuilder currency(String currency) {
            this.currency = currency;
            return this;
        }
        public CreateWalletRequest build() {
            return new CreateWalletRequest(userId, currency);
        }
    }
}
