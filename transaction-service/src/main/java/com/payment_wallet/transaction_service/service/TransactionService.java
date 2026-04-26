package com.payment_wallet.transaction_service.service;

import com.payment_wallet.transaction_service.dto.TransactionRequest;
import com.payment_wallet.transaction_service.entity.Transaction;

import java.util.List;

public interface TransactionService {
    Transaction createTransaction(TransactionRequest request);

    List<Transaction> getAllTransactions();
}
