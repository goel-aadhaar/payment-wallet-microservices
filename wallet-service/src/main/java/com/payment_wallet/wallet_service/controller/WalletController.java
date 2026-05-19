package com.payment_wallet.wallet_service.controller;

import com.payment_wallet.wallet_service.dto.*;
import com.payment_wallet.wallet_service.service.WalletService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import io.swagger.v3.oas.annotations.Operation;

@RestController
@RequestMapping("/api/v1/wallets")
public class WalletController {

    private final WalletService walletService;

    public WalletController(WalletService walletService) {
        this.walletService = walletService;
    }

    @Operation(summary = "Create Wallet", description = "Initialize a new wallet for a user")
    @PostMapping
    public ResponseEntity<WalletResponse> createWallet(@RequestBody CreateWalletRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(walletService.createWallet(request));
    }

    @Operation(summary = "Credit Wallet", description = "Add funds to a user's wallet")
    @PostMapping("/credit")
    public ResponseEntity<WalletResponse> credit(@RequestBody CreditRequest request) {
        return ResponseEntity.ok(walletService.credit(request));
    }

    @Operation(summary = "Debit Wallet", description = "Deduct funds directly from a wallet")
    @PostMapping("/debit")
    public ResponseEntity<WalletResponse> debit(@RequestBody DebitRequest request) {
        return ResponseEntity.ok(walletService.debit(request));
    }

    @Operation(summary = "Get Wallet", description = "Retrieve wallet details and balance by User ID")
    @GetMapping("/user/{userId}")
    public ResponseEntity<WalletResponse> getWallet(@PathVariable Long userId) {
        return ResponseEntity.ok(walletService.getWallet(userId));
    }

    @Operation(summary = "Place Hold", description = "Temporarily hold funds for a transaction")
    @PostMapping("/hold")
    public ResponseEntity<HoldResponse> placeHold(@RequestBody HoldRequest request) {
        return ResponseEntity.ok(walletService.placeHold(request));
    }

    @Operation(summary = "Capture Hold", description = "Complete transaction by capturing held funds")
    @PostMapping("/hold/capture")
    public ResponseEntity<WalletResponse> captureHold(@RequestBody CaptureRequest request) {
        return ResponseEntity.ok(walletService.captureHold(request));
    }

    @Operation(summary = "Release Hold", description = "Cancel hold and return funds to available balance")
    @PostMapping("/hold/release")
    public ResponseEntity<HoldResponse> releaseHold(@RequestBody CaptureRequest request) {
        return ResponseEntity.ok(walletService.releaseHold(request.getHoldReference()));
    }
}
