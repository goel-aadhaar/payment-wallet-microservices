package com.payment_wallet.transaction_service.service;

import com.payment_wallet.transaction_service.entity.Transaction;
import com.payment_wallet.transaction_service.kafka.KafkaEventProducer;
import com.payment_wallet.transaction_service.repository.TransactionRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import tools.jackson.databind.ObjectMapper;

import java.time.LocalDateTime;
import java.util.List;

@Service
@AllArgsConstructor
public class TransactionServiceImpl implements TransactionService {

    private final TransactionRepository transactionRepository;

    private final ObjectMapper objectMapper;

    private final KafkaEventProducer kafkaEventProducer;

    @Override
    public Transaction createTransaction(Transaction request) {

        Long senderId = request.getSenderId();

        Long receiverId = request.getReceiverId();

        Double amount = request.getAmount();

        Transaction transaction = Transaction
                .builder()
                .receiverId(receiverId)
                .senderId(senderId)
                .amount(amount)
                .timestamp(LocalDateTime.now())
                .status("SUCCESS")
                .build();

        System.out.println("Incoming transaction object: " + transaction);

        Transaction saved = transactionRepository.save(transaction);

        System.out.println("Saved transaction in DB: " + saved);

        try {
            String eventPayload = objectMapper.writeValueAsString(saved);
            String key = String.valueOf(saved.getId());
            kafkaEventProducer.sendTransactionEvent(key, eventPayload);
            System.out.println("Kafka message sent");
        } catch (Exception e) {
            System.err.println("Faied to send kafka event");
            e.printStackTrace();
        }

        return saved;
    }

    @Override
    public List<Transaction> getAllTransactions() {
        return transactionRepository.findAll();
    }
}
