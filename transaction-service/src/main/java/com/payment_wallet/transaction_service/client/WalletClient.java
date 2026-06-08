package com.payment_wallet.transaction_service.client;

import com.payment_wallet.transaction_service.dto.*;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

// Paths here must mirror wallet-service WalletController (base /api/v1/wallets).
@FeignClient(name = "wallet-service", url = "${wallet.service.url}",
        fallbackFactory = WalletClientFallbackFactory.class)
public interface WalletClient {

    @PostMapping("/debit")
    WalletResponse debit(@RequestBody DebitRequest request);

    @PostMapping("/credit")
    WalletResponse credit(@RequestBody CreditRequest request);

    @PostMapping("/hold")
    HoldResponse placeHold(@RequestBody HoldRequest request);

    @PostMapping("/hold/capture")
    WalletResponse capture(@RequestBody CaptureRequest request);

    @PostMapping("/hold/release")
    HoldResponse release(@RequestBody CaptureRequest request);

    @GetMapping("/user/{userId}")
    WalletResponse getWallet(@PathVariable Long userId);
}
