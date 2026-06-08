package com.payment_wallet.notification_service.repository;

import com.payment_wallet.notification_service.entity.Notification;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, Long> {

    Page<Notification> findByUserIdOrderBySentAtDesc(Long userId, Pageable pageable);

    Page<Notification> findByUserIdAndReadOrderBySentAtDesc(Long userId, boolean read, Pageable pageable);

    long countByUserIdAndReadFalse(Long userId);

    /** De-duplication guard: a given transaction yields at most one notification per user. */
    boolean existsByTransactionIdAndUserId(Long transactionId, Long userId);

    @Modifying
    @Query("update Notification n set n.read = true, n.readAt = :now where n.userId = :userId and n.read = false")
    int markAllRead(@Param("userId") Long userId, @Param("now") LocalDateTime now);
}
