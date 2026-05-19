package com.payment_wallet.wallet_service.dto;

import io.swagger.v3.oas.annotations.media.Schema;

public class CaptureRequest {
    @Schema(description = "Reference ID of the hold to capture or release", example = "HOLD-8f3b-4c2a-9e1d")
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
