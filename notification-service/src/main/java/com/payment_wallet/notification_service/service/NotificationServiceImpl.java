package com.payment_wallet.notification_service.service;

import com.payment_wallet.common.error.ResourceNotFoundException;
import com.payment_wallet.notification_service.entity.Notification;
import com.payment_wallet.notification_service.entity.NotificationType;
import com.payment_wallet.notification_service.repository.NotificationRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
public class NotificationServiceImpl implements NotificationService {

    private final NotificationRepository notificationRepository;

    public NotificationServiceImpl(NotificationRepository notificationRepository) {
        this.notificationRepository = notificationRepository;
    }

    @Override
    public Notification sendNotification(Notification notification) {
        if (notification.getSentAt() == null) {
            notification.setSentAt(LocalDateTime.now());
        }
        if (notification.getType() == null) {
            notification.setType(NotificationType.GENERAL);
        }
        return notificationRepository.save(notification);
    }

    @Override
    public Page<Notification> getNotifications(Long userId, boolean unreadOnly, Pageable pageable) {
        return unreadOnly
                ? notificationRepository.findByUserIdAndReadOrderBySentAtDesc(userId, false, pageable)
                : notificationRepository.findByUserIdOrderBySentAtDesc(userId, pageable);
    }

    @Override
    public long getUnreadCount(Long userId) {
        return notificationRepository.countByUserIdAndReadFalse(userId);
    }

    @Override
    public Notification markAsRead(Long id) {
        Notification notification = notificationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Notification not found: " + id));
        if (!notification.isRead()) {
            notification.setRead(true);
            notification.setReadAt(LocalDateTime.now());
            notificationRepository.save(notification);
        }
        return notification;
    }

    @Override
    @Transactional
    public int markAllAsRead(Long userId) {
        return notificationRepository.markAllRead(userId, LocalDateTime.now());
    }

    @Override
    public void delete(Long id) {
        if (!notificationRepository.existsById(id)) {
            throw new ResourceNotFoundException("Notification not found: " + id);
        }
        notificationRepository.deleteById(id);
    }
}
