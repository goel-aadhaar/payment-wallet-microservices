package com.payment_wallet.transaction_service.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public class TransactionRequest {

    @NotNull(message = "Sender ID is required")
    private Long senderId;

    @NotNull(message = "Receiver ID is required")
    private Long receiverId;

    @NotNull(message = "Amount is required")
    @Positive(message = "Amount must be positive")
    private Double amount;
    public TransactionRequest() {}

    public TransactionRequest(Long senderId, Long receiverId, Double amount) {
        this.senderId = senderId;
        this.receiverId = receiverId;
        this.amount = amount;
    }

    public Long getSenderId() {
        return senderId;
    }

    public void setSenderId(Long senderId) {
        this.senderId = senderId;
    }

    public Long getReceiverId() {
        return receiverId;
    }

    public void setReceiverId(Long receiverId) {
        this.receiverId = receiverId;
    }

    public Double getAmount() {
        return amount;
    }

    public void setAmount(Double amount) {
        this.amount = amount;
    }

    public static TransactionRequestBuilder builder() {
        return new TransactionRequestBuilder();
    }

    public static class TransactionRequestBuilder {
        private Long senderId;
        private Long receiverId;
        private Double amount;
        public TransactionRequestBuilder senderId(Long senderId) {
            this.senderId = senderId;
            return this;
        }
        public TransactionRequestBuilder receiverId(Long receiverId) {
            this.receiverId = receiverId;
            return this;
        }
        public TransactionRequestBuilder amount(Double amount) {
            this.amount = amount;
            return this;
        }
        public TransactionRequest build() {
            return new TransactionRequest(senderId, receiverId, amount);
        }
    }
}
