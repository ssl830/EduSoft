package org.example.edusoft.service.notification;

import org.example.edusoft.entity.notification.Notification;
import org.example.edusoft.mapper.notification.NotificationMapper;
import org.example.edusoft.service.notification.impl.NotificationServiceImpl;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class NotificationServiceTest {

    @Mock
    private NotificationMapper notificationMapper;

    @InjectMocks
    private NotificationServiceImpl notificationService;

    @Test
    @DisplayName("创建通知 - 正常插入")
    void testCreateNotification_success() {
        Notification notification = new Notification();
        when(notificationMapper.insert(notification)).thenReturn(1);

        notificationService.createNotification(notification);

        verify(notificationMapper, times(1)).insert(notification);
    }

    @Test
    @DisplayName("创建通知 - 插入空对象")
    void testCreateNotification_null() {
        when(notificationMapper.insert(null)).thenReturn(1);

        notificationService.createNotification(null);

        verify(notificationMapper, times(1)).insert(null);
    }

    @Test
    @DisplayName("获取用户通知 - 非空列表")
    void testGetUserNotifications_nonEmpty() {
        List<Notification> notifications = Arrays.asList(new Notification(), new Notification());
        when(notificationMapper.findByUserId(1L)).thenReturn(notifications);

        List<Notification> result = notificationService.getUserNotifications(1L);

        assertEquals(2, result.size());
    }

    @Test
    @DisplayName("获取用户通知 - 空列表")
    void testGetUserNotifications_empty() {
        when(notificationMapper.findByUserId(2L)).thenReturn(Collections.emptyList());

        List<Notification> result = notificationService.getUserNotifications(2L);

        assertTrue(result.isEmpty());
    }

    @Test
    @DisplayName("获取未读数量 - 有未读")
    void testGetUnreadCount_nonZero() {
        when(notificationMapper.countUnreadByUserId(1L)).thenReturn(3);

        int count = notificationService.getUnreadCount(1L);

        assertEquals(3, count);
    }

    @Test
    @DisplayName("获取未读数量 - 无未读")
    void testGetUnreadCount_zero() {
        when(notificationMapper.countUnreadByUserId(2L)).thenReturn(0);

        int count = notificationService.getUnreadCount(2L);

        assertEquals(0, count);
    }

    @Test
    @DisplayName("标记为已读 - 正常调用")
    void testMarkAsRead_success() {
        when(notificationMapper.markAsRead(1L)).thenReturn(1);

        notificationService.markAsRead(1L);

        verify(notificationMapper, times(1)).markAsRead(1L);
    }

    @Test
    @DisplayName("标记为已读 - 空ID")
    void testMarkAsRead_nullId() {
        when(notificationMapper.markAsRead(null)).thenReturn(1);

        notificationService.markAsRead(null);

        verify(notificationMapper, times(1)).markAsRead(null);
    }

    @Test
    @DisplayName("全部标记为已读 - 正常调用")
    void testMarkAllAsRead_success() {
        when(notificationMapper.markAllAsRead(1L)).thenReturn(1);

        notificationService.markAllAsRead(1L);

        verify(notificationMapper, times(1)).markAllAsRead(1L);
    }

    @Test
    @DisplayName("全部标记为已读 - 空ID")
    void testMarkAllAsRead_nullId() {
        when(notificationMapper.markAllAsRead(null)).thenReturn(1);

        notificationService.markAllAsRead(null);

        verify(notificationMapper, times(1)).markAllAsRead(null);
    }

    @Test
    @DisplayName("删除通知 - 正常删除")
    void testDeleteNotification_success() {
        when(notificationMapper.deleteById(1L)).thenReturn(1);

        notificationService.deleteNotification(1L);

        verify(notificationMapper, times(1)).deleteById(1L);
    }

    @Test
    @DisplayName("删除通知 - 空ID")
    void testDeleteNotification_nullId() {
        when(notificationMapper.deleteById(null)).thenReturn(1);

        notificationService.deleteNotification(null);

        verify(notificationMapper, times(1)).deleteById(null);
    }

    @Test
    @DisplayName("根据ID获取通知 - 找到通知")
    void testGetNotificationById_found() {
        Notification notification = new Notification();
        notification.setId(1L);
        when(notificationMapper.findById(1L)).thenReturn(notification);

        Notification result = notificationService.getNotificationById(1L);

        assertEquals(notification, result);
        assertEquals(1L, result.getId());
    }

    @Test
    @DisplayName("根据ID获取通知 - 未找到通知")
    void testGetNotificationById_notFound() {
        when(notificationMapper.findById(2L)).thenReturn(null);

        Notification result = notificationService.getNotificationById(2L);

        assertNull(result);
    }
}
