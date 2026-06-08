package com.payment_wallet.transaction_service.repository;

import com.payment_wallet.transaction_service.entity.Transaction;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, Long> {

    /** Transactions a user is party to (sender or receiver), optionally filtered by status. */
    @Query("select t from Transaction t where (t.senderId = :userId or t.receiverId = :userId) "
            + "and (:status is null or t.status = :status) order by t.timestamp desc")
    Page<Transaction> search(@Param("userId") Long userId, @Param("status") String status, Pageable pageable);
}
