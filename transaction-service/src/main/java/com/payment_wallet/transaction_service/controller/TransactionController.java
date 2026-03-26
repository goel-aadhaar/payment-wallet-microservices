package com.payment_wallet.transaction_service.controller;

import com.payment_wallet.transaction_service.entity.Transaction;
import com.payment_wallet.transaction_service.service.TransactionService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/transactions/")
@AllArgsConstructor
public class TransactionController {

    private final TransactionService transactionService;

    @PostMapping("/create")
    public ResponseEntity<?> create(@Valid @RequestBody Transaction transaction) {

        Transaction created = transactionService.createTransaction(transaction);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(created);
    }

    @GetMapping("/all")
    public List<Transaction> getAll() {
        return transactionService.getAllTransactions();
    }

}
