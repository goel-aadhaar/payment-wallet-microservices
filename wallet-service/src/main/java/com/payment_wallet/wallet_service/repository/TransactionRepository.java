package com.payment_wallet.wallet_service.repository;

import com.payment_wallet.wallet_service.entity.WalletTransaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TransactionRepository extends JpaRepository<WalletTransaction, Long> {
}
