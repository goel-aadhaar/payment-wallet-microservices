package com.payment_wallet.wallet_service.service;

import com.payment_wallet.wallet_service.dto.*;
import com.payment_wallet.wallet_service.entity.Wallet;
import com.payment_wallet.wallet_service.entity.WalletHold;
import com.payment_wallet.wallet_service.entity.WalletTransaction;
import com.payment_wallet.wallet_service.exception.InsufficientFundsException;
import com.payment_wallet.wallet_service.exception.NotFoundException;
import com.payment_wallet.wallet_service.repository.TransactionRepository;
import com.payment_wallet.wallet_service.repository.WalletHoldRepository;
import com.payment_wallet.wallet_service.repository.WalletRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class WalletService {

    private static final Logger log = LoggerFactory.getLogger(WalletService.class);

    private final WalletRepository walletRepository;

    private final WalletHoldRepository walletHoldRepository;

    private final TransactionRepository transactionRepository;

    public WalletService(WalletRepository walletRepository, WalletHoldRepository walletHoldRepository, TransactionRepository transactionRepository) {
        this.walletRepository = walletRepository;
        this.walletHoldRepository = walletHoldRepository;
        this.transactionRepository = transactionRepository;
    }

    @Transactional
    public WalletResponse createWallet(CreateWalletRequest request) {
        Wallet wallet = new Wallet(request.getUserId(), request.getCurrency());
        Wallet saved = walletRepository.save(wallet);
        log.info("Wallet created for userId: {}", saved.getUserId());
        return toResponse(saved);
    }

    @Transactional
    public WalletResponse credit(CreditRequest request) {
        log.info("CREDIT request: userId={}, amount={}, currency={}",
                request.getUserId(), request.getAmount(), request.getCurrency());

        Wallet wallet = walletRepository.findByUserIdAndCurrency(request.getUserId(), request.getCurrency())
                .orElseThrow(() -> new NotFoundException("Wallet not found for user: " + request.getUserId()));

        wallet.setBalance(wallet.getBalance() + request.getAmount());
        wallet.setAvailableBalance(wallet.getAvailableBalance() + request.getAmount());

        Wallet saved = walletRepository.save(wallet);

        transactionRepository.save(WalletTransaction.builder()
                .walletId(wallet.getId())
                .type("CREDIT")
                .amount(request.getAmount())
                .status("SUCCESS")
                .build());

        return toResponse(saved);
    }

    @Transactional
    public WalletResponse debit(DebitRequest request) {
        log.info("DEBIT request: userId={}, amount={}, currency={}",
                request.getUserId(), request.getAmount(), request.getCurrency());

        Wallet wallet = walletRepository.findByUserIdAndCurrency(request.getUserId(), request.getCurrency())
                .orElseThrow(() -> new NotFoundException("Wallet not found for user: " + request.getUserId()));

        if (wallet.getAvailableBalance() < request.getAmount()) {
            throw new InsufficientFundsException("Not enough balance");
        }

        wallet.setBalance(wallet.getBalance() - request.getAmount());
        wallet.setAvailableBalance(wallet.getAvailableBalance() - request.getAmount());

        Wallet saved = walletRepository.save(wallet);

        transactionRepository.save(WalletTransaction.builder()
                .walletId(wallet.getId())
                .type("DEBIT")
                .amount(request.getAmount())
                .status("SUCCESS")
                .build());

        return toResponse(saved);
    }

    public WalletResponse getWallet(Long userId) {
        Wallet wallet = walletRepository.findByUserId(userId)
                .orElseThrow(() -> new NotFoundException("Wallet not found for user: " + userId));
        return toResponse(wallet);
    }

    @Transactional
    public HoldResponse placeHold(HoldRequest request) {
        Wallet wallet = walletRepository.findByUserIdAndCurrency(request.getUserId(), request.getCurrency())
                .orElseThrow(() -> new NotFoundException("Wallet not found for user: " + request.getUserId()));

        if (wallet.getAvailableBalance() < request.getAmount()) {
            throw new InsufficientFundsException("Not enough balance");
        }

        wallet.setAvailableBalance(wallet.getAvailableBalance() - request.getAmount());

        WalletHold hold = new WalletHold();
        hold.setWallet(wallet);
        hold.setAmount(request.getAmount());
        hold.setHoldReference("HOLD-" + System.currentTimeMillis());
        hold.setStatus("ACTIVE");

        walletRepository.save(wallet);
        walletHoldRepository.save(hold);

        return new HoldResponse(hold.getHoldReference(), hold.getAmount(), hold.getStatus());
    }

    @Transactional
    public WalletResponse captureHold(CaptureRequest request) {
        WalletHold hold = walletHoldRepository.findByHoldReference(request.getHoldReference())
                .orElseThrow(() -> new NotFoundException("Hold not found"));

        if (!"ACTIVE".equals(hold.getStatus())) {
            throw new IllegalStateException("Hold is not active");
        }

        Wallet wallet = hold.getWallet();
        wallet.setBalance(wallet.getBalance() - hold.getAmount());

        hold.setStatus("CAPTURED");

        walletRepository.save(wallet);
        walletHoldRepository.save(hold);

        transactionRepository.save(WalletTransaction.builder()
                .walletId(wallet.getId())
                .type("DEBIT")
                .amount(hold.getAmount())
                .status("SUCCESS")
                .build());

        return toResponse(wallet);
    }

    @Transactional
    public HoldResponse releaseHold(String holdReference) {
        WalletHold hold = walletHoldRepository.findByHoldReference(holdReference)
                .orElseThrow(() -> new NotFoundException("Hold not found"));

        if (!"ACTIVE".equals(hold.getStatus())) {
            throw new IllegalStateException("Hold is not active");
        }

        Wallet wallet = hold.getWallet();
        wallet.setAvailableBalance(wallet.getAvailableBalance() + hold.getAmount());

        hold.setStatus("RELEASED");

        walletRepository.save(wallet);
        walletHoldRepository.save(hold);

        return new HoldResponse(hold.getHoldReference(), hold.getAmount(), hold.getStatus());
    }

    private WalletResponse toResponse(Wallet wallet) {
        return WalletResponse.builder()
                .id(wallet.getId())
                .userId(wallet.getUserId())
                .currency(wallet.getCurrency())
                .balance(wallet.getBalance())
                .availableBalance(wallet.getAvailableBalance())
                .build();
    }
}
