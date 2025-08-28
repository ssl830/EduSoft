package org.example.edusoft.controller.notification;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.validation.ValidationException;
import org.example.edusoft.common.Result;
import org.example.edusoft.entity.notification.TaskReminder;
import org.example.edusoft.service.notification.TaskReminderService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.NoSuchElementException;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(TaskReminderController.class)
public class TaskReminderControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private TaskReminderService taskReminderService;

    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper();
    }

    // ---

    ## createTaskReminder() 测试

    ### 正常情况测试 (Positive Test)
    **目标:** 验证成功创建任务提醒并返回创建后的对象。
    ```java
    @Test
    @DisplayName("should create a task reminder and return success result")
    void createTaskReminder_Success() throws Exception {
        TaskReminder reminder = new TaskReminder();
        reminder.setUserId(1L);
        reminder.setTitle("测试任务");

        TaskReminder createdReminder = new TaskReminder();
        createdReminder.setId(1L);
        createdReminder.setUserId(1L);
        createdReminder.setTitle("测试任务");

        when(taskReminderService.createTaskReminder(any(TaskReminder.class))).thenReturn(createdReminder);

        mockMvc.perform(post("/api/task-reminders")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(reminder)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.id").value(1L))
                .andExpect(jsonPath("$.data.title").value("测试任务"));
    }
    ```

    ### 异常情况测试 (Negative Test)
    **目标:** 验证请求体无效时（如缺少必填字段），控制器能正确处理并返回 `400 Bad Request` 错误。
    ```java
    @Test
    @DisplayName("should return 400 Bad Request for invalid request body")
    void createTaskReminder_InvalidInput() throws Exception {
        TaskReminder reminder = new TaskReminder(); // 缺少 userId 和 title，将触发验证错误

        mockMvc.perform(post("/api/task-reminders")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(reminder)))
                .andExpect(status().isBadRequest());
    }
    ```

    ---

    ## updateTaskReminder() 测试

    ### 正常情况测试 (Positive Test)
    **目标:** 验证成功更新任务提醒并返回更新后的对象。
    ```java
    @Test
    @DisplayName("should update a task reminder and return success result")
    void updateTaskReminder_Success() throws Exception {
        Long id = 1L;
        TaskReminder reminder = new TaskReminder();
        reminder.setTitle("更新后的任务");

        TaskReminder updatedReminder = new TaskReminder();
        updatedReminder.setId(id);
        updatedReminder.setTitle("更新后的任务");

        when(taskReminderService.updateTaskReminder(any(TaskReminder.class))).thenReturn(updatedReminder);

        mockMvc.perform(put("/api/task-reminders/{id}", id)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(reminder)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.id").value(id))
                .andExpect(jsonPath("$.data.title").value("更新后的任务"));
    }
    ```

    ### 异常情况测试 (Negative Test)
    **目标:** 验证当任务不存在时，服务层抛出异常，控制器能正确处理。
    ```java
    @Test
    @DisplayName("should return error when updating a non-existent task reminder")
    void updateTaskReminder_NotFound() throws Exception {
        Long id = 99L;
        TaskReminder reminder = new TaskReminder();
        reminder.setTitle("更新后的任务");

        when(taskReminderService.updateTaskReminder(any(TaskReminder.class))).thenThrow(new NoSuchElementException("Task not found"));

        mockMvc.perform(put("/api/task-reminders/{id}", id)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(reminder)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(500)); // 或者其他合适的错误码，取决于全局异常处理
    }
    ```

    ---

    ## deleteTaskReminder() 测试

    ### 正常情况测试 (Positive Test)
    **目标:** 验证成功删除任务提醒并返回成功布尔值。
    ```java
    @Test
    @DisplayName("should delete a task reminder and return true")
    void deleteTaskReminder_Success() throws Exception {
        Long id = 1L;
        doNothing().when(taskReminderService).deleteTaskReminder(id);

        mockMvc.perform(delete("/api/task-reminders/{id}", id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data").value(true));
    }
    ```

    ### 异常情况测试 (Negative Test)
    **目标:** 验证当任务不存在时，服务层抛出异常，控制器能正确处理。
    ```java
    @Test
    @DisplayName("should return error when deleting a non-existent task reminder")
    void deleteTaskReminder_NotFound() throws Exception {
        Long id = 99L;
        doThrow(new NoSuchElementException("Task not found")).when(taskReminderService).deleteTaskReminder(id);

        mockMvc.perform(delete("/api/task-reminders/{id}", id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(500));
    }
    ```

    ---

    ## getTaskReminder() 测试

    ### 正常情况测试 (Positive Test)
    **目标:** 验证根据ID成功获取任务提醒。
    ```java
    @Test
    @DisplayName("should get a task reminder by id")
    void getTaskReminder_Success() throws Exception {
        Long id = 1L;
        TaskReminder reminder = new TaskReminder();
        reminder.setId(id);
        reminder.setTitle("待办事项");

        when(taskReminderService.getTaskReminderById(id)).thenReturn(reminder);

        mockMvc.perform(get("/api/task-reminders/{id}", id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.id").value(id));
    }
    ```

    ### 异常情况测试 (Negative Test)
    **目标:** 验证当任务不存在时，服务层抛出异常，控制器能正确处理。
    ```java
    @Test
    @DisplayName("should return error when a task reminder is not found")
    void getTaskReminder_NotFound() throws Exception {
        Long id = 99L;
        when(taskReminderService.getTaskReminderById(id)).thenThrow(new NoSuchElementException("Task not found"));

        mockMvc.perform(get("/api/task-reminders/{id}", id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(500));
    }
    
    @Test
    @DisplayName("should get all task reminders for a user")
    void getUserTaskReminders_Success() throws Exception {
        Long userId = 1L;
        List<TaskReminder> reminders = List.of(new TaskReminder(), new TaskReminder());
        when(taskReminderService.getTaskRemindersByUserId(userId)).thenReturn(reminders);

        mockMvc.perform(get("/api/task-reminders/user/{userId}", userId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.length()").value(2));
    }

    @Test
    @DisplayName("should get uncompleted task reminders for a user")
    void getUncompletedTaskReminders_Success() throws Exception {
        Long userId = 1L;
        List<TaskReminder> reminders = List.of(new TaskReminder());
        when(taskReminderService.getUncompletedTaskReminders(userId)).thenReturn(reminders);

        mockMvc.perform(get("/api/task-reminders/user/{userId}/uncompleted", userId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.length()").value(1));
    }
    
    @Test
    @DisplayName("should return error when a user id is invalid")
    void getUserTaskReminders_InvalidId() throws Exception {
        mockMvc.perform(get("/api/task-reminders/user/{userId}", "invalid_id"))
                .andExpect(status().isBadRequest());
    }
    
    @Test
    @DisplayName("should mark a task as completed and return true")
    void markTaskAsCompleted_Success() throws Exception {
        Long id = 1L;
        doNothing().when(taskReminderService).markTaskAsCompleted(id);

        mockMvc.perform(put("/api/task-reminders/{id}/complete", id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data").value(true));
    }

    @Test
    @DisplayName("should mark a task as uncompleted and return true")
    void markTaskAsUncompleted_Success() throws Exception {
        Long id = 1L;
        doNothing().when(taskReminderService).markTaskAsUncompleted(id);

        mockMvc.perform(put("/api/task-reminders/{id}/uncomplete", id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data").value(true));
    }
    
    @Test
    @DisplayName("should return error when marking a non-existent task as completed")
    void markTaskAsCompleted_NotFound() throws Exception {
        Long id = 99L;
        doThrow(new NoSuchElementException("Task not found")).when(taskReminderService).markTaskAsCompleted(id);

        mockMvc.perform(put("/api/task-reminders/{id}/complete", id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(500));
    }
}