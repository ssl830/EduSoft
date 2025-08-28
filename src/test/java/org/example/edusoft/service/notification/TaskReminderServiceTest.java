package org.example.edusoft.service.notification;

import org.example.edusoft.common.exception.NotificationException;
import org.example.edusoft.entity.notification.TaskReminder;
import org.example.edusoft.mapper.notification.TaskReminderMapper;
import org.example.edusoft.service.notification.impl.TaskReminderServiceImpl;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TaskReminderServiceTest {

    @Mock
    private TaskReminderMapper taskReminderMapper;

    @InjectMocks
    private TaskReminderServiceImpl taskReminderService;

    @Test
    @DisplayName("createTaskReminder - 创建成功")
    void testCreateTaskReminder_success() {
        TaskReminder tr = new TaskReminder();
        tr.setTitle("任务1");
        tr.setDeadline(LocalDateTime.now().plusDays(1));
        tr.setPriority("HIGH");
        when(taskReminderMapper.insert(any(TaskReminder.class))).thenReturn(1);

        TaskReminder result = taskReminderService.createTaskReminder(tr);

        assertEquals("任务1", result.getTitle());
        assertFalse(result.getCompleted());
    }

    @Test
    @DisplayName("createTaskReminder - 缺少标题抛异常")
    void testCreateTaskReminder_noTitle() {
        TaskReminder tr = new TaskReminder();
        tr.setDeadline(LocalDateTime.now().plusDays(1));
        tr.setPriority("HIGH");
        assertThrows(NotificationException.class, () -> taskReminderService.createTaskReminder(tr));
    }

    @Test
    @DisplayName("createTaskReminder - 优先级错误抛异常")
    void testCreateTaskReminder_invalidPriority() {
        TaskReminder tr = new TaskReminder();
        tr.setTitle("任务2");
        tr.setDeadline(LocalDateTime.now().plusDays(1));
        tr.setPriority("INVALID");
        assertThrows(NotificationException.class, () -> taskReminderService.createTaskReminder(tr));
    }

    @Test
    @DisplayName("createTaskReminder - 截止时间已过抛异常")
    void testCreateTaskReminder_deadlinePassed() {
        TaskReminder tr = new TaskReminder();
        tr.setTitle("任务3");
        tr.setDeadline(LocalDateTime.now().minusDays(1));
        tr.setPriority("HIGH");
        assertThrows(NotificationException.class, () -> taskReminderService.createTaskReminder(tr));
    }

    @Test
    @DisplayName("updateTaskReminder - 更新成功")
    void testUpdateTaskReminder_success() {
        TaskReminder tr = new TaskReminder();
        tr.setId(1L);
        tr.setTitle("任务修改");
        tr.setPriority("MEDIUM");
        tr.setDeadline(LocalDateTime.now().plusDays(2));
        when(taskReminderMapper.selectById(1L)).thenReturn(new TaskReminder());
        when(taskReminderMapper.updateById(any(TaskReminder.class))).thenReturn(1);

        TaskReminder result = taskReminderService.updateTaskReminder(tr);

        assertEquals("任务修改", result.getTitle());
    }

    @Test
    @DisplayName("updateTaskReminder - ID为空抛异常")
    void testUpdateTaskReminder_noId() {
        TaskReminder tr = new TaskReminder();
        assertThrows(NotificationException.class, () -> taskReminderService.updateTaskReminder(tr));
    }

    @Test
    @DisplayName("updateTaskReminder - 任务不存在抛异常")
    void testUpdateTaskReminder_notFound() {
        TaskReminder tr = new TaskReminder();
        tr.setId(2L);
        when(taskReminderMapper.selectById(2L)).thenReturn(null);
        assertThrows(NotificationException.class, () -> taskReminderService.updateTaskReminder(tr));
    }

    @Test
    @DisplayName("updateTaskReminder - 优先级错误抛异常")
    void testUpdateTaskReminder_invalidPriority() {
        TaskReminder tr = new TaskReminder();
        tr.setId(3L);
        tr.setPriority("INVALID");
        when(taskReminderMapper.selectById(3L)).thenReturn(new TaskReminder());
        assertThrows(NotificationException.class, () -> taskReminderService.updateTaskReminder(tr));
    }

    @Test
    @DisplayName("updateTaskReminder - 截止时间已过抛异常")
    void testUpdateTaskReminder_deadlinePassed() {
        TaskReminder tr = new TaskReminder();
        tr.setId(4L);
        tr.setDeadline(LocalDateTime.now().minusDays(1));
        when(taskReminderMapper.selectById(4L)).thenReturn(new TaskReminder());
        assertThrows(NotificationException.class, () -> taskReminderService.updateTaskReminder(tr));
    }

    @Test
    @DisplayName("deleteTaskReminder - 删除成功")
    void testDeleteTaskReminder_success() {
        when(taskReminderMapper.selectById(1L)).thenReturn(new TaskReminder());
        when(taskReminderMapper.deleteById(1L)).thenReturn(1);

        assertDoesNotThrow(() -> taskReminderService.deleteTaskReminder(1L));
        verify(taskReminderMapper, times(1)).deleteById(1L);
    }

    @Test
    @DisplayName("deleteTaskReminder - ID为空抛异常")
    void testDeleteTaskReminder_nullId() {
        assertThrows(NotificationException.class, () -> taskReminderService.deleteTaskReminder(null));
    }

    @Test
    @DisplayName("deleteTaskReminder - 任务不存在抛异常")
    void testDeleteTaskReminder_notFound() {
        when(taskReminderMapper.selectById(2L)).thenReturn(null);
        assertThrows(NotificationException.class, () -> taskReminderService.deleteTaskReminder(2L));
    }

    @Test
    @DisplayName("getTaskReminderById - 获取成功")
    void testGetTaskReminderById_success() {
        TaskReminder tr = new TaskReminder();
        tr.setId(1L);
        when(taskReminderMapper.selectById(1L)).thenReturn(tr);

        TaskReminder result = taskReminderService.getTaskReminderById(1L);

        assertEquals(1L, result.getId());
    }

    @Test
    @DisplayName("getTaskReminderById - ID为空抛异常")
    void testGetTaskReminderById_nullId() {
        assertThrows(NotificationException.class, () -> taskReminderService.getTaskReminderById(null));
    }

    @Test
    @DisplayName("getTaskReminderById - 任务不存在抛异常")
    void testGetTaskReminderById_notFound() {
        when(taskReminderMapper.selectById(2L)).thenReturn(null);
        assertThrows(NotificationException.class, () -> taskReminderService.getTaskReminderById(2L));
    }

    @Test
    @DisplayName("getTaskRemindersByUserId - 非空列表")
    void testGetTaskRemindersByUserId_nonEmpty() {
        List<TaskReminder> list = Arrays.asList(new TaskReminder(), new TaskReminder());
        when(taskReminderMapper.getTaskRemindersByUserId(1L)).thenReturn(list);

        List<TaskReminder> result = taskReminderService.getTaskRemindersByUserId(1L);

        assertEquals(2, result.size());
    }

    @Test
    @DisplayName("getTaskRemindersByUserId - 空列表")
    void testGetTaskRemindersByUserId_empty() {
        when(taskReminderMapper.getTaskRemindersByUserId(2L)).thenReturn(Collections.emptyList());

        List<TaskReminder> result = taskReminderService.getTaskRemindersByUserId(2L);

        assertTrue(result.isEmpty());
    }

    @Test
    @DisplayName("getTaskRemindersByUserId - 用户ID为空抛异常")
    void testGetTaskRemindersByUserId_nullId() {
        assertThrows(NotificationException.class, () -> taskReminderService.getTaskRemindersByUserId(null));
    }

    @Test
    @DisplayName("getUncompletedTaskReminders - 非空列表")
    void testGetUncompletedTaskReminders_nonEmpty() {
        List<TaskReminder> list = Arrays.asList(new TaskReminder());
        when(taskReminderMapper.getUncompletedTaskReminders(1L)).thenReturn(list);

        List<TaskReminder> result = taskReminderService.getUncompletedTaskReminders(1L);

        assertEquals(1, result.size());
    }

    @Test
    @DisplayName("getUncompletedTaskReminders - 空列表")
    void testGetUncompletedTaskReminders_empty() {
        when(taskReminderMapper.getUncompletedTaskReminders(2L)).thenReturn(Collections.emptyList());

        List<TaskReminder> result = taskReminderService.getUncompletedTaskReminders(2L);

        assertTrue(result.isEmpty());
    }

    @Test
    @DisplayName("getUncompletedTaskReminders - 用户ID为空抛异常")
    void testGetUncompletedTaskReminders_nullId() {
        assertThrows(NotificationException.class, () -> taskReminderService.getUncompletedTaskReminders(null));
    }

    @Test
    @DisplayName("getCompletedTaskReminders - 非空列表")
    void testGetCompletedTaskReminders_nonEmpty() {
        List<TaskReminder> list = Arrays.asList(new TaskReminder());
        when(taskReminderMapper.getCompletedTaskReminders(1L)).thenReturn(list);
        List<TaskReminder> result = taskReminderService.getCompletedTaskReminders(1L);

        assertEquals(1, result.size());
    }

    @Test
    @DisplayName("getCompletedTaskReminders - 空列表")
    void testGetCompletedTaskReminders_empty() {
        when(taskReminderMapper.getCompletedTaskReminders(2L)).thenReturn(Collections.emptyList());

        List<TaskReminder> result = taskReminderService.getCompletedTaskReminders(2L);

        assertTrue(result.isEmpty());
    }

    @Test
    @DisplayName("getCompletedTaskReminders - 用户ID为空抛异常")
    void testGetCompletedTaskReminders_nullId() {
        assertThrows(NotificationException.class, () -> taskReminderService.getCompletedTaskReminders(null));
    }

    @Test
    @DisplayName("markTaskAsCompleted - 标记成功")
    void testMarkTaskAsCompleted_success() {
        TaskReminder tr = new TaskReminder();
        tr.setId(1L);
        tr.setCompleted(false);
        when(taskReminderMapper.selectById(1L)).thenReturn(tr);
        when(taskReminderMapper.updateById(any(TaskReminder.class))).thenReturn(1);

        assertDoesNotThrow(() -> taskReminderService.markTaskAsCompleted(1L));
        assertTrue(tr.getCompleted());
    }

    @Test
    @DisplayName("markTaskAsCompleted - 已完成抛异常")
    void testMarkTaskAsCompleted_alreadyCompleted() {
        TaskReminder tr = new TaskReminder();
        tr.setId(2L);
        tr.setCompleted(true);
        when(taskReminderMapper.selectById(2L)).thenReturn(tr);

        assertThrows(NotificationException.class, () -> taskReminderService.markTaskAsCompleted(2L));
    }

    @Test
    @DisplayName("markTaskAsCompleted - 任务不存在抛异常")
    void testMarkTaskAsCompleted_notFound() {
        when(taskReminderMapper.selectById(3L)).thenReturn(null);

        assertThrows(NotificationException.class, () -> taskReminderService.markTaskAsCompleted(3L));
    }

    @Test
    @DisplayName("markTaskAsCompleted - ID为空抛异常")
    void testMarkTaskAsCompleted_nullId() {
        assertThrows(NotificationException.class, () -> taskReminderService.markTaskAsCompleted(null));
    }

    @Test
    @DisplayName("markTaskAsUncompleted - 标记成功")
    void testMarkTaskAsUncompleted_success() {
        TaskReminder tr = new TaskReminder();
        tr.setId(1L);
        tr.setCompleted(true);
        when(taskReminderMapper.selectById(1L)).thenReturn(tr);
        when(taskReminderMapper.updateById(any(TaskReminder.class))).thenReturn(1);

        assertDoesNotThrow(() -> taskReminderService.markTaskAsUncompleted(1L));
        assertFalse(tr.getCompleted());
    }

    @Test
    @DisplayName("markTaskAsUncompleted - 未完成抛异常")
    void testMarkTaskAsUncompleted_notCompleted() {
        TaskReminder tr = new TaskReminder();
        tr.setId(2L);
        tr.setCompleted(false);
        when(taskReminderMapper.selectById(2L)).thenReturn(tr);

        assertThrows(NotificationException.class, () -> taskReminderService.markTaskAsUncompleted(2L));
    }

    @Test
    @DisplayName("markTaskAsUncompleted - 任务不存在抛异常")
    void testMarkTaskAsUncompleted_notFound() {
        when(taskReminderMapper.selectById(3L)).thenReturn(null);

        assertThrows(NotificationException.class, () -> taskReminderService.markTaskAsUncompleted(3L));
    }

    @Test
    @DisplayName("markTaskAsUncompleted - ID为空抛异常")
    void testMarkTaskAsUncompleted_nullId() {
        assertThrows(NotificationException.class, () -> taskReminderService.markTaskAsUncompleted(null));
    }
}
