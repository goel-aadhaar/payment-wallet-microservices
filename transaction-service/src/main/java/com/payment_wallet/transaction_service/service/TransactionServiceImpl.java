package com.payment_wallet.transaction_service.service;

import com.payment_wallet.transaction_service.client.WalletClient;
import com.payment_wallet.transaction_service.dto.*;
import com.payment_wallet.transaction_service.entity.Transaction;
import com.payment_wallet.transaction_service.kafka.KafkaEventProducer;
import com.payment_wallet.transaction_service.repository.TransactionRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class TransactionServiceImpl implements TransactionService {

    private static final Logger log = LoggerFactory.getLogger(TransactionServiceImpl.class);

    private final TransactionRepository transactionRepository;
    private final KafkaEventProducer kafkaEventProducer;
    private final WalletClient walletClient;

    public TransactionServiceImpl(TransactionRepository transactionRepository,
                                  KafkaEventProducer kafkaEventProducer,
                                  WalletClient walletClient) {
        this.transactionRepository = transactionRepository;
        this.kafkaEventProducer = kafkaEventProducer;
        this.walletClient = walletClient;
    }

    @Override
    public Transaction createTransaction(TransactionRequest request) {

        // Transaction saved as pending
        Transaction transaction = Transaction.builder()
                .senderId(request.getSenderId())
                .receiverId(request.getReceiverId())
                .amount(request.getAmount())
                .timestamp(LocalDateTime.now())
                .status("PENDING")
                .build();

        log.info("Creating transaction: {}", transaction);

        Transaction saved = transactionRepository.save(transaction);

        log.info("Transaction PENDING saved with id: {}", saved.getId());

        String holdReference = null;
        boolean captured = false;

        try {
            // Step 1: Place hold on sender's wallet
            HoldRequest holdRequest = HoldRequest.builder()
                    .userId(request.getSenderId())
                    .currency("INR")
                    .amount(request.getAmount().longValue())
                    .build();

            HoldResponse holdResponse = walletClient.placeHold(holdRequest);

            if (holdResponse == null || holdResponse.getHoldReference() == null) {
                throw new RuntimeException("Hold response missing holdReference");
            }

            holdReference = holdResponse.getHoldReference();
            log.info("Hold placed successfully. holdReference: {}", holdReference);

            // Step 2: Verify receiver wallet exists
            try {
                WalletResponse receiverWallet = walletClient.getWallet(request.getReceiverId());
                if (receiverWallet == null) {
                    log.warn("Receiver wallet not found for userId: {}", request.getReceiverId());
                    tryReleaseHold(holdReference);
                    saved.setStatus("FAILED");
                    saved = transactionRepository.save(saved);
                    return saved;
                }
            } catch (Exception ex) {
                log.warn("Failed to verify receiver wallet for userId: {}", request.getReceiverId(), ex);
                tryReleaseHold(holdReference);
                saved.setStatus("FAILED");
                saved = transactionRepository.save(saved);
                return saved;
            }

            // Step 3: Capture hold -> debit sender wallet
            CaptureRequest captureRequest = CaptureRequest.builder()
                    .holdReference(holdReference)
                    .build();

            WalletResponse captureResponse = walletClient.capture(captureRequest);

            if (captureResponse == null) {
                log.error("Capture failed for holdReference: {}", holdReference);
                tryReleaseHold(holdReference);
                saved.setStatus("FAILED");
                saved = transactionRepository.save(saved);
                return saved;
            }

            captured = true;
            log.info("Hold captured successfully for holdReference: {}", holdReference);

            // Step 4: Credit receiver wallet
            try {
                CreditRequest creditRequest = CreditRequest.builder()
                        .userId(request.getReceiverId())
                        .currency("INR")
                        .amount(request.getAmount().longValue())
                        .build();

                WalletResponse creditResponse = walletClient.credit(creditRequest);

                if (creditResponse == null) {
                    throw new RuntimeException("Credit response was null for receiver: " + request.getReceiverId());
                }

                log.info("Receiver wallet credited successfully for userId: {}", request.getReceiverId());
            } catch (Exception creditEx) {
                log.error("Failed to credit receiver wallet. Initiating refund to sender.", creditEx);

                // Refund: credit the sender back since capture already debited them
                try {
                    CreditRequest refundRequest = CreditRequest.builder()
                            .userId(request.getSenderId())
                            .currency("INR")
                            .amount(request.getAmount().longValue())
                            .build();

                    WalletResponse refundResponse = walletClient.credit(refundRequest);

                    if (refundResponse != null) {
                        log.info("Refund credited back to sender userId: {}", request.getSenderId());
                    } else {
                        log.error("CRITICAL: Refund to sender userId: {} returned null. Manual intervention required.", request.getSenderId());
                    }
                } catch (Exception refundEx) {
                    log.error("CRITICAL: Refund to sender userId: {} failed. Manual intervention required.", request.getSenderId(), refundEx);
                }

                saved.setStatus("FAILED");
                saved = transactionRepository.save(saved);
                return saved;
            }

            // Step 5: Mark transaction as SUCCESS
            saved.setStatus("SUCCESS");
            saved = transactionRepository.save(saved);
            log.info("Transaction {} completed successfully", saved.getId());

        } catch (Exception e) {
            log.error("Transaction failed with error: {}", e.getMessage(), e);

            if (holdReference != null && !captured) {
                tryReleaseHold(holdReference);
            }

            saved.setStatus("FAILED");
            saved = transactionRepository.save(saved);
            return saved;
        }

        // Send Kafka event for successful transaction
        try {
            String key = String.valueOf(saved.getId());
            kafkaEventProducer.sendTransactionEvent(key, saved);
            log.info("Kafka event sent for transaction: {}", saved.getId());
        } catch (Exception e) {
            log.error("Failed to send Kafka event for transaction: {}", saved.getId(), e);
        }

        return saved;
    }

    private void tryReleaseHold(String holdReference) {
        if (holdReference == null) return;
        try {
            walletClient.release(holdReference);
            log.info("Hold released successfully for holdReference: {}", holdReference);
        } catch (Exception e) {
            log.error("Failed to release hold for holdReference: {}. Manual intervention may be required.", holdReference, e);
        }
    }

    @Override
    public List<Transaction> getAllTransactions() {
        return transactionRepository.findAll();
    }
}
