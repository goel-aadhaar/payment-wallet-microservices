package com.payment_wallet.transaction_service.dto;


public class HoldResponse {
    private String holdReference;
    private Long amount;
    private String status;
    public HoldResponse() {}

    public HoldResponse(String holdReference, Long amount, String status) {
        this.holdReference = holdReference;
        this.amount = amount;
        this.status = status;
    }

    public String getHoldReference() {
        return holdReference;
    }

    public void setHoldReference(String holdReference) {
        this.holdReference = holdReference;
    }

    public Long getAmount() {
        return amount;
    }

    public void setAmount(Long amount) {
        this.amount = amount;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public static HoldResponseBuilder builder() {
        return new HoldResponseBuilder();
    }

    public static class HoldResponseBuilder {
        private String holdReference;
        private Long amount;
        private String status;
        public HoldResponseBuilder holdReference(String holdReference) {
            this.holdReference = holdReference;
            return this;
        }
        public HoldResponseBuilder amount(Long amount) {
            this.amount = amount;
            return this;
        }
        public HoldResponseBuilder status(String status) {
            this.status = status;
            return this;
        }
        public HoldResponse build() {
            return new HoldResponse(holdReference, amount, status);
        }
    }
}
