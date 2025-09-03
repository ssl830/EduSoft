package org.example.edusoft.content.mapper.notification;

import org.apache.ibatis.annotations.*;
import org.example.edusoft.content.entity.notification.Notification;
import java.util.List;
import java.util.Map;

/**
 * 通知数据访问接口
 */
@Mapper
public interface NotificationMapper {
    
    @Insert("INSERT INTO notification (user_id, title, message, type, read_flag, created_at, related_id, related_type, " +
            "priority, status, updated_at, sender_name, sender_id, course_name, course_id, class_name, class_id) " +
            "VALUES (#{userId}, #{title}, #{message}, #{type}, #{readFlag}, #{createdAt}, #{relatedId}, #{relatedType}, " +
            "#{priority}, #{status}, #{updatedAt}, #{senderName}, #{senderId}, #{courseName}, #{courseId}, #{className}, #{classId})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(Notification notification);

    @Insert("<script>" +
            "INSERT INTO notification (user_id, title, message, type, read_flag, created_at, related_id, related_type, " +
            "priority, status, updated_at, sender_name, sender_id, course_name, course_id, class_name, class_id) VALUES " +
            "<foreach collection='notifications' item='notification' separator=','>" +
            "(#{notification.userId}, #{notification.title}, #{notification.message}, #{notification.type}, " +
            "#{notification.readFlag}, #{notification.createdAt}, #{notification.relatedId}, #{notification.relatedType}, " +
            "#{notification.priority}, #{notification.status}, #{notification.updatedAt}, #{notification.senderName}, " +
            "#{notification.senderId}, #{notification.courseName}, #{notification.courseId}, #{notification.className}, #{notification.classId})" +
            "</foreach>" +
            "</script>")
    int batchInsert(@Param("notifications") List<Notification> notifications);

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
        @Result(property = "relatedType", column = "related_type"),
        @Result(property = "priority", column = "priority"),
        @Result(property = "status", column = "status"),
        @Result(property = "updatedAt", column = "updated_at"),
        @Result(property = "senderName", column = "sender_name"),
        @Result(property = "senderId", column = "sender_id"),
        @Result(property = "courseName", column = "course_name"),
        @Result(property = "courseId", column = "course_id"),
        @Result(property = "className", column = "class_name"),
        @Result(property = "classId", column = "class_id")
    })
    List<Notification> findByUserId(String userId);

    @Select("SELECT * FROM notification WHERE user_id = #{userId} ORDER BY created_at DESC LIMIT #{offset}, #{size}")
    @Results({
        @Result(property = "id", column = "id"),
        @Result(property = "userId", column = "user_id"),
        @Result(property = "title", column = "title"),
        @Result(property = "message", column = "message"),
        @Result(property = "type", column = "type"),
        @Result(property = "readFlag", column = "read_flag"),
        @Result(property = "createdAt", column = "created_at"),
        @Result(property = "relatedId", column = "related_id"),
        @Result(property = "relatedType", column = "related_type"),
        @Result(property = "priority", column = "priority"),
        @Result(property = "status", column = "status"),
        @Result(property = "updatedAt", column = "updated_at"),
        @Result(property = "senderName", column = "sender_name"),
        @Result(property = "senderId", column = "sender_id"),
        @Result(property = "courseName", column = "course_name"),
        @Result(property = "courseId", column = "course_id"),
        @Result(property = "className", column = "class_name"),
        @Result(property = "classId", column = "class_id")
    })
    List<Notification> findByUserIdPaged(@Param("userId") String userId, @Param("offset") int offset, @Param("size") int size);

    @Select("SELECT COUNT(*) FROM notification WHERE user_id = #{userId} AND read_flag = false")
    int countUnreadByUserId(String userId);

    @Select("SELECT COUNT(*) FROM notification WHERE user_id = #{userId}")
    int countByUserId(String userId);

    @Update("UPDATE notification SET read_flag = true, updated_at = NOW() WHERE id = #{id}")
    int markAsRead(Long id);

    @Update("UPDATE notification SET read_flag = true, updated_at = NOW() WHERE user_id = #{userId}")
    int markAllAsRead(String userId);

    @Delete("DELETE FROM notification WHERE id = #{id}")
    int deleteById(Long id);

    @Update("UPDATE notification SET status = 'deleted', updated_at = NOW() WHERE id = #{id}")
    int softDeleteById(Long id);

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
        @Result(property = "relatedType", column = "related_type"),
        @Result(property = "priority", column = "priority"),
        @Result(property = "status", column = "status"),
        @Result(property = "updatedAt", column = "updated_at"),
        @Result(property = "senderName", column = "sender_name"),
        @Result(property = "senderId", column = "sender_id"),
        @Result(property = "courseName", column = "course_name"),
        @Result(property = "courseId", column = "course_id"),
        @Result(property = "className", column = "class_name"),
        @Result(property = "classId", column = "class_id")
    })
    Notification findById(Long id);

    @Select("SELECT * FROM notification WHERE user_id = #{userId} AND type = #{type} ORDER BY created_at DESC")
    @Results({
        @Result(property = "id", column = "id"),
        @Result(property = "userId", column = "user_id"),
        @Result(property = "title", column = "title"),
        @Result(property = "message", column = "message"),
        @Result(property = "type", column = "type"),
        @Result(property = "readFlag", column = "read_flag"),
        @Result(property = "createdAt", column = "created_at"),
        @Result(property = "relatedId", column = "related_id"),
        @Result(property = "relatedType", column = "related_type"),
        @Result(property = "priority", column = "priority"),
        @Result(property = "status", column = "status"),
        @Result(property = "updatedAt", column = "updated_at"),
        @Result(property = "senderName", column = "sender_name"),
        @Result(property = "senderId", column = "sender_id"),
        @Result(property = "courseName", column = "course_name"),
        @Result(property = "courseId", column = "course_id"),
        @Result(property = "className", column = "class_name"),
        @Result(property = "classId", column = "class_id")
    })
    List<Notification> findByUserIdAndType(@Param("userId") String userId, @Param("type") String type);

    @Select("SELECT * FROM notification WHERE user_id = #{userId} AND course_id = #{courseId} ORDER BY created_at DESC")
    @Results({
        @Result(property = "id", column = "id"),
        @Result(property = "userId", column = "user_id"),
        @Result(property = "title", column = "title"),
        @Result(property = "message", column = "message"),
        @Result(property = "type", column = "type"),
        @Result(property = "readFlag", column = "read_flag"),
        @Result(property = "createdAt", column = "created_at"),
        @Result(property = "relatedId", column = "related_id"),
        @Result(property = "relatedType", column = "related_type"),
        @Result(property = "priority", column = "priority"),
        @Result(property = "status", column = "status"),
        @Result(property = "updatedAt", column = "updated_at"),
        @Result(property = "senderName", column = "sender_name"),
        @Result(property = "senderId", column = "sender_id"),
        @Result(property = "courseName", column = "course_name"),
        @Result(property = "courseId", column = "course_id"),
        @Result(property = "className", column = "class_name"),
        @Result(property = "classId", column = "class_id")
    })
    List<Notification> findByUserIdAndCourse(@Param("userId") String userId, @Param("courseId") Long courseId);

    @Select("SELECT * FROM notification WHERE user_id = #{userId} AND class_id = #{classId} ORDER BY created_at DESC")
    @Results({
        @Result(property = "id", column = "id"),
        @Result(property = "userId", column = "user_id"),
        @Result(property = "title", column = "title"),
        @Result(property = "message", column = "message"),
        @Result(property = "type", column = "type"),
        @Result(property = "readFlag", column = "read_flag"),
        @Result(property = "createdAt", column = "created_at"),
        @Result(property = "relatedId", column = "related_id"),
        @Result(property = "relatedType", column = "related_type"),
        @Result(property = "priority", column = "priority"),
        @Result(property = "status", column = "status"),
        @Result(property = "updatedAt", column = "updated_at"),
        @Result(property = "senderName", column = "sender_name"),
        @Result(property = "senderId", column = "sender_id"),
        @Result(property = "courseName", column = "course_name"),
        @Result(property = "courseId", column = "course_id"),
        @Result(property = "className", column = "class_name"),
        @Result(property = "classId", column = "class_id")
    })
    List<Notification> findByUserIdAndClass(@Param("userId") String userId, @Param("classId") Long classId);

    @Select("SELECT * FROM notification WHERE user_id = #{userId} AND (title LIKE CONCAT('%', #{keyword}, '%') OR message LIKE CONCAT('%', #{keyword}, '%')) ORDER BY created_at DESC")
    @Results({
        @Result(property = "id", column = "id"),
        @Result(property = "userId", column = "user_id"),
        @Result(property = "title", column = "title"),
        @Result(property = "message", column = "message"),
        @Result(property = "type", column = "type"),
        @Result(property = "readFlag", column = "read_flag"),
        @Result(property = "createdAt", column = "created_at"),
        @Result(property = "relatedId", column = "related_id"),
        @Result(property = "relatedType", column = "related_type"),
        @Result(property = "priority", column = "priority"),
        @Result(property = "status", column = "status"),
        @Result(property = "updatedAt", column = "updated_at"),
        @Result(property = "senderName", column = "sender_name"),
        @Result(property = "senderId", column = "sender_id"),
        @Result(property = "courseName", column = "course_name"),
        @Result(property = "courseId", column = "course_id"),
        @Result(property = "className", column = "class_name"),
        @Result(property = "classId", column = "class_id")
    })
    List<Notification> searchByUserIdAndKeyword(@Param("userId") String userId, @Param("keyword") String keyword);

    @Select("SELECT type, COUNT(*) as count FROM notification WHERE user_id = #{userId} GROUP BY type")
    List<Map<String, Object>> getTypeStatsByUserId(String userId);

    @Delete("DELETE FROM notification WHERE created_at < DATE_SUB(NOW(), INTERVAL #{days} DAY)")
    int deleteExpiredNotifications(@Param("days") int days);
} 