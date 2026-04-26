package com.payment_wallet.wallet_service.exception;

public class InsufficientFundsException extends RuntimeException {

    public InsufficientFundsException(String msg) {
        super(msg);
    }
}
