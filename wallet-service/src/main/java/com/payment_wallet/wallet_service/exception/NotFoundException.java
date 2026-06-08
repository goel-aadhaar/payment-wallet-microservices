package com.payment_wallet.wallet_service.exception;

import com.payment_wallet.common.error.ResourceNotFoundException;

/** Wallet-specific not-found; mapped to HTTP 404 by the shared GlobalExceptionHandler. */
public class NotFoundException extends ResourceNotFoundException {
    public NotFoundException(String msg) {
        super(msg);
    }
}
