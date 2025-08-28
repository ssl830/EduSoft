package org.example.edusoft.mapper.notification;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.example.edusoft.entity.notification.TaskReminder;
import java.util.List;

@Mapper
public interface TaskReminderMapper extends BaseMapper<TaskReminder> {
    
    @Select("SELECT * FROM task_reminder WHERE user_id = #{userId} ORDER BY deadline ASC")
    List<TaskReminder> getTaskRemindersByUserId(Long userId);
    
    @Select("SELECT * FROM task_reminder WHERE user_id = #{userId} AND completed = false ORDER BY deadline ASC")
    List<TaskReminder> getUncompletedTaskReminders(Long userId);
    
    @Select("SELECT * FROM task_reminder WHERE user_id = #{userId} AND completed = true ORDER BY completed_time DESC")
    List<TaskReminder> getCompletedTaskReminders(Long userId);
} 