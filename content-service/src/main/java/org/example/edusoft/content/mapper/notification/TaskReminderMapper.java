package org.example.edusoft.content.mapper.notification;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Update;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Options;
import org.example.edusoft.content.entity.notification.TaskReminder;
import java.util.List;

@Mapper
public interface TaskReminderMapper {
    
    @Select("SELECT * FROM task_reminder WHERE user_id = #{userId} ORDER BY deadline ASC")
    List<TaskReminder> getTaskRemindersByUserId(String userId);
    
    @Select("SELECT * FROM task_reminder WHERE user_id = #{userId} AND completed = false ORDER BY deadline ASC")
    List<TaskReminder> getUncompletedTaskReminders(String userId);
    
    @Select("SELECT * FROM task_reminder WHERE user_id = #{userId} AND completed = true ORDER BY completed_time DESC")
    List<TaskReminder> getCompletedTaskReminders(String userId);
    
    @Select("SELECT * FROM task_reminder WHERE id = #{id}")
    TaskReminder getTaskReminderById(Long id);
    
    @Insert("INSERT INTO task_reminder (user_id, title, content, create_time, deadline, priority, completed) " +
            "VALUES (#{userId}, #{title}, #{content}, #{createTime}, #{deadline}, #{priority}, #{completed})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(TaskReminder taskReminder);
    
    @Update("UPDATE task_reminder SET title = #{title}, content = #{content}, deadline = #{deadline}, " +
            "priority = #{priority}, completed = #{completed}, completed_time = #{completedTime} WHERE id = #{id}")
    int update(TaskReminder taskReminder);
    
    @Delete("DELETE FROM task_reminder WHERE id = #{id}")
    int deleteById(Long id);
    
    @Update("UPDATE task_reminder SET completed = true, completed_time = NOW() WHERE id = #{id}")
    int markAsCompleted(Long id);
    
    @Update("UPDATE task_reminder SET completed = false, completed_time = NULL WHERE id = #{id}")
    int markAsUncompleted(Long id);
} 