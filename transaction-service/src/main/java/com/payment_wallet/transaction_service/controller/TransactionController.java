package com.payment_wallet.transaction_service.controller;

import com.payment_wallet.common.web.PageResponse;
import com.payment_wallet.transaction_service.dto.TransactionRequest;
import com.payment_wallet.transaction_service.entity.Transaction;
import com.payment_wallet.transaction_service.service.TransactionService;
import jakarta.validation.Valid;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import io.swagger.v3.oas.annotations.Operation;

import java.util.List;

@RestController
@RequestMapping("/api/v1/transactions")
public class TransactionController {

    private final TransactionService transactionService;

    public TransactionController(TransactionService transactionService) {
        this.transactionService = transactionService;
    }

    @Operation(summary = "Create Transaction", description = "Initiate a money transfer between two users")
    @PostMapping
    public ResponseEntity<Transaction> create(@Valid @RequestBody TransactionRequest request) {
        Transaction created = transactionService.createTransaction(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @Operation(summary = "Get All Transactions", description = "Retrieve a history of all transactions")
    @GetMapping
    public ResponseEntity<List<Transaction>> getAll() {
        return ResponseEntity.ok(transactionService.getAllTransactions());
    }

    @Operation(summary = "Search transactions", description = "Paginated transactions for a user, optionally filtered by status")
    @GetMapping("/search")
    public PageResponse<Transaction> search(
            @RequestParam Long userId,
            @RequestParam(required = false) String status,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        return PageResponse.from(transactionService.search(userId, status, PageRequest.of(page, size)));
    }

    @Operation(summary = "Get Transaction by ID")
    @GetMapping("/{id}")
    public ResponseEntity<Transaction> getById(@PathVariable Long id) {
        return ResponseEntity.ok(transactionService.getById(id));
    }
}
