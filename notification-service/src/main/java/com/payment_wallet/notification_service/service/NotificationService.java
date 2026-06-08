package com.payment_wallet.notification_service.service;

import com.payment_wallet.notification_service.entity.Notification;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface NotificationService {

    Notification sendNotification(Notification notification);

    Page<Notification> getNotifications(Long userId, boolean unreadOnly, Pageable pageable);

    long getUnreadCount(Long userId);

    Notification markAsRead(Long id);

    int markAllAsRead(Long userId);

    void delete(Long id);
}
