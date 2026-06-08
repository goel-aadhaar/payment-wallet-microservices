package com.payment_wallet.notification_service.controller;

import com.payment_wallet.common.web.PageResponse;
import com.payment_wallet.notification_service.entity.Notification;
import com.payment_wallet.notification_service.service.NotificationService;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/notifications")
public class NotificationController {

    private final NotificationService notificationService;

    public NotificationController(NotificationService notificationService) {
        this.notificationService = notificationService;
    }

    @Operation(summary = "Send Notification", description = "Internal use: create a notification for a user")
    @PostMapping
    public ResponseEntity<Notification> sendNotification(@RequestBody Notification notification) {
        return ResponseEntity.ok(notificationService.sendNotification(notification));
    }

    @Operation(summary = "List notifications", description = "Paginated notifications for a user (newest first); set unreadOnly=true to filter")
    @GetMapping("/user/{userId}")
    public PageResponse<Notification> getNotificationsByUser(
            @PathVariable Long userId,
            @RequestParam(defaultValue = "false") boolean unreadOnly,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        return PageResponse.from(
                notificationService.getNotifications(userId, unreadOnly, PageRequest.of(page, size)));
    }

    @Operation(summary = "Unread count", description = "Number of unread notifications for a user")
    @GetMapping("/user/{userId}/unread-count")
    public Map<String, Long> unreadCount(@PathVariable Long userId) {
        return Map.of("count", notificationService.getUnreadCount(userId));
    }

    @Operation(summary = "Mark one read")
    @PatchMapping("/{id}/read")
    public Notification markRead(@PathVariable Long id) {
        return notificationService.markAsRead(id);
    }

    @Operation(summary = "Mark all read for a user")
    @PatchMapping("/user/{userId}/read-all")
    public Map<String, Integer> markAllRead(@PathVariable Long userId) {
        return Map.of("updated", notificationService.markAllAsRead(userId));
    }

    @Operation(summary = "Delete a notification")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        notificationService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
