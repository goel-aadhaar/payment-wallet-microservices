package com.payment_wallet.transaction_service.service;

import com.payment_wallet.transaction_service.dto.TransactionRequest;
import com.payment_wallet.transaction_service.entity.Transaction;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface TransactionService {
    Transaction createTransaction(TransactionRequest request);

    List<Transaction> getAllTransactions();

    Transaction getById(Long id);

    Page<Transaction> search(Long userId, String status, Pageable pageable);
}
