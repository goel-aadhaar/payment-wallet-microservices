package com.payment_wallet.transaction_service.dto;


public class CaptureRequest {
    private String holdReference;
    public CaptureRequest() {}

    public CaptureRequest(String holdReference) {
        this.holdReference = holdReference;
    }

    public String getHoldReference() {
        return holdReference;
    }

    public void setHoldReference(String holdReference) {
        this.holdReference = holdReference;
    }

    public static CaptureRequestBuilder builder() {
        return new CaptureRequestBuilder();
    }

    public static class CaptureRequestBuilder {
        private String holdReference;
        public CaptureRequestBuilder holdReference(String holdReference) {
            this.holdReference = holdReference;
            return this;
        }
        public CaptureRequest build() {
            return new CaptureRequest(holdReference);
        }
    }
}
