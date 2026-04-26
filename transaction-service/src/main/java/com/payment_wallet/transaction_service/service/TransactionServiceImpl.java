package com.payment_wallet.transaction_service.service;

import com.payment_wallet.transaction_service.dto.TransactionRequest;
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

    public TransactionServiceImpl(TransactionRepository transactionRepository, KafkaEventProducer kafkaEventProducer) {
        this.transactionRepository = transactionRepository;
        this.kafkaEventProducer = kafkaEventProducer;
    }

    @Override
    public Transaction createTransaction(TransactionRequest request) {
        Transaction transaction = Transaction.builder()
                .senderId(request.getSenderId())
                .receiverId(request.getReceiverId())
                .amount(request.getAmount())
                .timestamp(LocalDateTime.now())
                .status("SUCCESS")
                .build();

        log.info("Creating transaction: {}", transaction);

        Transaction saved = transactionRepository.save(transaction);

        log.info("Transaction saved with id: {}", saved.getId());

        try {
            String key = String.valueOf(saved.getId());
            kafkaEventProducer.sendTransactionEvent(key, saved);
            log.info("Kafka event sent for transaction: {}", saved.getId());
        } catch (Exception e) {
            log.error("Failed to send Kafka event for transaction: {}", saved.getId(), e);
        }

        return saved;
    }

    @Override
    public List<Transaction> getAllTransactions() {
        return transactionRepository.findAll();
    }
}
