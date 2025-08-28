package org.example.edusoft.controller.notification;

import org.example.edusoft.common.Result;
import org.example.edusoft.entity.notification.Notification;
import org.example.edusoft.service.notification.NotificationService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.*;

import static org.mockito.ArgumentMatchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(NotificationController.class)
class NotificationControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private NotificationService notificationService;

    // getUserNotifications success
    @Test
    void getUserNotifications_success() throws Exception {
        List<Notification> notifications = Arrays.asList(new Notification(), new Notification());
        Mockito.when(notificationService.getUserNotifications(10L)).thenReturn(notifications);

        mockMvc.perform(get("/api/notifications").requestAttr("userId", 10L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data[0]").exists());
    }

    // getUserNotifications fail
    @Test
    void getUserNotifications_fail() throws Exception {
        Mockito.when(notificationService.getUserNotifications(10L)).thenThrow(new RuntimeException("fail"));
        mockMvc.perform(get("/api/notifications").requestAttr("userId", 10L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(500))
                .andExpect(jsonPath("$.msg").value(org.hamcrest.Matchers.startsWith("获取通知列表失败：")));
    }

    // getUnreadCount success
    @Test
    void getUnreadCount_success() throws Exception {
        Mockito.when(notificationService.getUnreadCount(10L)).thenReturn(7);

        mockMvc.perform(get("/api/notifications/unread").requestAttr("userId", 10L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.count").value(7));
    }

    // getUnreadCount fail
    @Test
    void getUnreadCount_fail() throws Exception {
        Mockito.when(notificationService.getUnreadCount(10L)).thenThrow(new RuntimeException("fail"));
        mockMvc.perform(get("/api/notifications/unread").requestAttr("userId", 10L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(500))
                .andExpect(jsonPath("$.msg").value(org.hamcrest.Matchers.startsWith("获取未读通知数量失败：")));
    }

    // markAsRead success
    @Test
    void markAsRead_success() throws Exception {
        Mockito.doNothing().when(notificationService).markAsRead(123L);

        mockMvc.perform(put("/api/notifications/123/read"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.msg").value("通知已标记为已读"));
    }

    // markAsRead fail
    @Test
    void markAsRead_fail() throws Exception {
        Mockito.doThrow(new RuntimeException("fail")).when(notificationService).markAsRead(123L);

        mockMvc.perform(put("/api/notifications/123/read"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(500))
                .andExpect(jsonPath("$.msg").value(org.hamcrest.Matchers.startsWith("标记通知为已读失败：")));
    }

    // markAllAsRead success
    @Test
    void markAllAsRead_success() throws Exception {
        Mockito.doNothing().when(notificationService).markAllAsRead(10L);

        mockMvc.perform(put("/api/notifications/read-all").requestAttr("userId", 10L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.msg").value("所有通知已标记为已读"));
    }

    // markAllAsRead fail
    @Test
    void markAllAsRead_fail() throws Exception {
        Mockito.doThrow(new RuntimeException("fail")).when(notificationService).markAllAsRead(10L);

        mockMvc.perform(put("/api/notifications/read-all").requestAttr("userId", 10L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(500))
                .andExpect(jsonPath("$.msg").value(org.hamcrest.Matchers.startsWith("标记所有通知为已读失败：")));
    }

    // deleteNotification success
    @Test
    void deleteNotification_success() throws Exception {
        Mockito.doNothing().when(notificationService).deleteNotification(456L);

        mockMvc.perform(delete("/api/notifications/456"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.msg").value("通知已删除"));
    }

    // deleteNotification fail
    @Test
    void deleteNotification_fail() throws Exception {
        Mockito.doThrow(new RuntimeException("fail")).when(notificationService).deleteNotification(456L);

        mockMvc.perform(delete("/api/notifications/456"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(500))
                .andExpect(jsonPath("$.msg").value(org.hamcrest.Matchers.startsWith("删除通知失败：")));
    }
}