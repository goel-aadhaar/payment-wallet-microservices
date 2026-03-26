package com.payment_wallet.notification_service.controller;

import com.payment_wallet.notification_service.entity.Notification;
import com.payment_wallet.notification_service.service.NotificationService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/notify")
public class NotificationController {

    private NotificationService notificationService;

    @PostMapping()
    public Notification sendNotification(
            @RequestBody Notification notification
    ) {
        return notificationService.sendNotification(notification);
    }

    @GetMapping("/{userId}")
    public List<Notification> getNotificationByUser(
            @PathVariable String userId
    ) {
        return notificationService.getNotificationsByUserId(userId);
    }
}
