package com.payment_wallet.common.error;

/** Thrown on a conflicting state (e.g. duplicate, insufficient funds). Mapped to HTTP 409. */
public class ConflictException extends RuntimeException {
    public ConflictException(String message) {
        super(message);
    }
}
