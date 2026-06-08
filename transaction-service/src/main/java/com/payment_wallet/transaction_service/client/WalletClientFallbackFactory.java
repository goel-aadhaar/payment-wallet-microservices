package com.payment_wallet.transaction_service.client;

import com.payment_wallet.transaction_service.dto.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.openfeign.FallbackFactory;
import org.springframework.stereotype.Component;

/**
 * Fallback invoked when a wallet-service call fails or the circuit breaker is open.
 * Each method throws so the transaction flow's existing try/catch marks the transaction FAILED
 * (and releases any hold) instead of hanging or surfacing a raw stack trace.
 */
@Component
public class WalletClientFallbackFactory implements FallbackFactory<WalletClient> {

    private static final Logger log = LoggerFactory.getLogger(WalletClientFallbackFactory.class);

    @Override
    public WalletClient create(Throwable cause) {
        return new WalletClient() {
            @Override public WalletResponse debit(DebitRequest request)       { throw fail("debit"); }
            @Override public WalletResponse credit(CreditRequest request)     { throw fail("credit"); }
            @Override public HoldResponse placeHold(HoldRequest request)      { throw fail("placeHold"); }
            @Override public WalletResponse capture(CaptureRequest request)   { throw fail("capture"); }
            @Override public HoldResponse release(CaptureRequest request)     { throw fail("release"); }
            @Override public WalletResponse getWallet(Long userId)            { throw fail("getWallet"); }

            private RuntimeException fail(String op) {
                log.error("wallet-service call '{}' failed or circuit is open: {}", op, cause.toString());
                return new IllegalStateException("wallet-service unavailable: " + cause.getMessage(), cause);
            }
        };
    }
}
