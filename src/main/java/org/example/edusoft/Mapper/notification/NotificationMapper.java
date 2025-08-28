package org.example.edusoft.mapper.notification;

import org.apache.ibatis.annotations.*;
import org.example.edusoft.entity.notification.Notification;
import java.util.List;

/**
 * 通知数据访问接口
 */
@Mapper
public interface NotificationMapper {
    
    @Insert("INSERT INTO notification (user_id, title, message, type, read_flag, created_at, related_id, related_type) " +
            "VALUES (#{userId}, #{title}, #{message}, #{type}, #{readFlag}, #{createdAt}, #{relatedId}, #{relatedType})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(Notification notification);

    @Select("SELECT * FROM notification WHERE user_id = #{userId} ORDER BY created_at DESC")
    @Results({
        @Result(property = "id", column = "id"),
        @Result(property = "userId", column = "user_id"),
        @Result(property = "title", column = "title"),
        @Result(property = "message", column = "message"),
        @Result(property = "type", column = "type"),
        @Result(property = "readFlag", column = "read_flag"),
        @Result(property = "createdAt", column = "created_at"),
        @Result(property = "relatedId", column = "related_id"),
        @Result(property = "relatedType", column = "related_type")
    })
    List<Notification> findByUserId(Long userId);

    @Select("SELECT COUNT(*) FROM notification WHERE user_id = #{userId} AND read_flag = false")
    int countUnreadByUserId(Long userId);

    @Update("UPDATE notification SET read_flag = true WHERE id = #{id}")
    int markAsRead(Long id);

    @Update("UPDATE notification SET read_flag = true WHERE user_id = #{userId}")
    int markAllAsRead(Long userId);

    @Delete("DELETE FROM notification WHERE id = #{id}")
    int deleteById(Long id);

    @Select("SELECT * FROM notification WHERE id = #{id}")
    @Results({
        @Result(property = "id", column = "id"),
        @Result(property = "userId", column = "user_id"),
        @Result(property = "title", column = "title"),
        @Result(property = "message", column = "message"),
        @Result(property = "type", column = "type"),
        @Result(property = "readFlag", column = "read_flag"),
        @Result(property = "createdAt", column = "created_at"),
        @Result(property = "relatedId", column = "related_id"),
        @Result(property = "relatedType", column = "related_type")
    })
    Notification findById(Long id);
} 