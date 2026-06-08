package com.payment_wallet.notification_service.service;

import com.payment_wallet.common.error.ResourceNotFoundException;
import com.payment_wallet.notification_service.entity.Notification;
import com.payment_wallet.notification_service.entity.NotificationType;
import com.payment_wallet.notification_service.repository.NotificationRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class NotificationServiceImplTest {

    @Mock NotificationRepository repository;
    @InjectMocks NotificationServiceImpl service;

    @Test
    void markAsRead_setsReadFlagAndTimestamp() {
        Notification n = Notification.builder().id(1L).userId(5L).read(false).build();
        when(repository.findById(1L)).thenReturn(Optional.of(n));

        Notification result = service.markAsRead(1L);

        assertThat(result.isRead()).isTrue();
        assertThat(result.getReadAt()).isNotNull();
        verify(repository).save(n);
    }

    @Test
    void markAsRead_missing_throwsNotFound() {
        when(repository.findById(99L)).thenReturn(Optional.empty());
        assertThatThrownBy(() -> service.markAsRead(99L)).isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    void getNotifications_unreadOnly_usesUnreadQuery() {
        Pageable pageable = PageRequest.of(0, 20);
        when(repository.findByUserIdAndReadOrderBySentAtDesc(5L, false, pageable)).thenReturn(Page.empty());

        service.getNotifications(5L, true, pageable);

        verify(repository).findByUserIdAndReadOrderBySentAtDesc(5L, false, pageable);
        verify(repository, never()).findByUserIdOrderBySentAtDesc(anyLong(), any());
    }

    @Test
    void delete_missing_throwsNotFound_andDoesNotDelete() {
        when(repository.existsById(7L)).thenReturn(false);
        assertThatThrownBy(() -> service.delete(7L)).isInstanceOf(ResourceNotFoundException.class);
        verify(repository, never()).deleteById(anyLong());
    }

    @Test
    void sendNotification_defaultsTypeAndTimestamp() {
        when(repository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        Notification saved = service.sendNotification(
                Notification.builder().userId(5L).message("hello").build());

        assertThat(saved.getType()).isEqualTo(NotificationType.GENERAL);
        assertThat(saved.getSentAt()).isNotNull();
    }
}
