package com.payment_wallet.wallet_service.exception;

import com.payment_wallet.common.error.ConflictException;

/** Raised when a wallet lacks funds for a debit/hold; mapped to HTTP 409 by the shared handler. */
public class InsufficientFundsException extends ConflictException {
    public InsufficientFundsException(String msg) {
        super(msg);
    }
}
