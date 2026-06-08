package com.payment_wallet.common.error;

/** Thrown when a request is semantically invalid. Mapped to HTTP 400. */
public class BadRequestException extends RuntimeException {
    public BadRequestException(String message) {
        super(message);
    }
}
