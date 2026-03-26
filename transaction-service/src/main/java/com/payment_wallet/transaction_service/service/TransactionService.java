package com.payment_wallet.transaction_service.service;

import com.payment_wallet.transaction_service.entity.Transaction;
import org.springframework.stereotype.Service;

import java.util.List;

public interface TransactionService {
    Transaction createTransaction(Transaction transaction);

    List<Transaction> getAllTransactions();
}
