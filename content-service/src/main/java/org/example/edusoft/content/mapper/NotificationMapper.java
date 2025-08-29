package org.example.edusoft.content.mapper;

import org.example.edusoft.content.entity.Notification;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface NotificationMapper {
    
    @Select("SELECT * FROM notification WHERE id = #{id}")
    Notification findById(@Param("id") Long id);
    
    @Select("SELECT * FROM notification WHERE user_id = #{userId} AND status = 'active' ORDER BY created_at DESC")
    List<Notification> findByUserId(@Param("userId") Long userId);
    
    @Select("SELECT * FROM notification WHERE user_id = #{userId} AND read_flag = false AND status = 'active'")
    List<Notification> findUnreadByUserId(@Param("userId") Long userId);
    
    @Select("SELECT * FROM notification WHERE type = #{type} AND status = 'active' ORDER BY created_at DESC")
    List<Notification> findByType(@Param("type") String type);
    
    @Select("SELECT * FROM notification WHERE related_type = #{relatedType} AND related_id = #{relatedId} AND status = 'active'")
    List<Notification> findByRelated(@Param("relatedType") String relatedType, @Param("relatedId") Long relatedId);
    
    @Insert("INSERT INTO notification (user_id, title, message, type, read_flag, related_id, related_type, " +
            "priority, status, created_at, updated_at) VALUES (#{userId}, #{title}, #{message}, #{type}, " +
            "false, #{relatedId}, #{relatedType}, #{priority}, 'active', NOW(), NOW())")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    void insert(Notification notification);
    
    @Update("UPDATE notification SET read_flag = true, updated_at = NOW() WHERE id = #{id}")
    void markAsRead(@Param("id") Long id);
    
    @Update("UPDATE notification SET read_flag = true, updated_at = NOW() WHERE user_id = #{userId}")
    void markAllAsRead(@Param("userId") Long userId);
    
    @Update("UPDATE notification SET status = 'expired', updated_at = NOW() WHERE id = #{id}")
    void expireById(@Param("id") Long id);
}
