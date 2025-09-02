package org.example.edusoft.content.mapper.notification;

import org.apache.ibatis.annotations.*;
import org.example.edusoft.content.entity.notification.TaskReminder;
import java.util.List;

@Mapper
public interface TaskReminderMapper {
    
    @Insert("INSERT INTO task_reminders (user_id, title, content, deadline, priority, is_completed, created_at, updated_at) " +
            "VALUES (#{userId}, #{title}, #{content}, #{deadline}, #{priority}, #{isCompleted}, #{createdAt}, #{updatedAt})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(TaskReminder taskReminder);
    
    @Update("UPDATE task_reminders SET user_id = #{userId}, title = #{title}, content = #{content}, " +
            "deadline = #{deadline}, priority = #{priority}, is_completed = #{isCompleted}, updated_at = #{updatedAt} " +
            "WHERE id = #{id}")
    int update(TaskReminder taskReminder);
    
    @Delete("DELETE FROM task_reminders WHERE id = #{id}")
    int deleteById(Long id);
    
    @Select("SELECT * FROM task_reminders WHERE id = #{id}")
    TaskReminder selectById(Long id);
    
    @Select("SELECT * FROM task_reminders WHERE user_id = #{userId}")
    List<TaskReminder> selectByUserId(Long userId);
    
    @Select("SELECT * FROM task_reminders WHERE user_id = #{userId} AND is_completed = false")
    List<TaskReminder> selectUncompletedByUserId(Long userId);
    
    @Select("SELECT * FROM task_reminders WHERE user_id = #{userId} AND is_completed = true")
    List<TaskReminder> selectCompletedByUserId(Long userId);
    
    @Update("UPDATE task_reminders SET is_completed = true, updated_at = NOW() WHERE id = #{id}")
    int markAsCompleted(Long id);
    
    @Update("UPDATE task_reminders SET is_completed = false, updated_at = NOW() WHERE id = #{id}")
    int markAsUncompleted(Long id);
}
