package org.example.edusoft.content.mapper.notification;

import org.apache.ibatis.annotations.*;
import org.example.edusoft.content.entity.notification.Notification;
import java.util.List;

@Mapper
public interface NotificationMapper {
    
    @Select("SELECT * FROM notifications WHERE user_id = #{userId} ORDER BY created_at DESC")
    List<Notification> selectByUserId(Long userId);
    
    @Select("SELECT COUNT(*) FROM notifications WHERE user_id = #{userId} AND is_read = false")
    Integer countUnreadByUserId(Long userId);
    
    @Update("UPDATE notifications SET is_read = true, read_at = NOW() WHERE id = #{id}")
    int markAsRead(Long id);
    
    @Update("UPDATE notifications SET is_read = true, read_at = NOW() WHERE user_id = #{userId}")
    int markAllAsReadByUserId(Long userId);
    
    @Delete("DELETE FROM notifications WHERE id = #{id}")
    int deleteById(Long id);
} 