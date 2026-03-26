package com.payment_wallet.notification_service.service;

import com.payment_wallet.notification_service.entity.Notification;
import com.payment_wallet.notification_service.repository.NotificationRepository;

import java.util.List;

public interface NotificationService {

    Notification sendNotification(Notification notification);

    List<Notification> getNotificationsByUserId(String userId);
}
