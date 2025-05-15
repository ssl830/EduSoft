package org.example.edusoft.Mapper;

import org.apache.ibatis.annotations.*;
import org.example.edusoft.entity.Notification;
import java.util.List;

@Mapper
public interface NotificationMapper {
    @Select("SELECT * FROM Notification WHERE id = #{id}")
    Notification findById(Long id);

    @Select("SELECT * FROM Notification WHERE user_id = #{userId}")
    List<Notification> findByUserId(Long userId);

    @Select("SELECT * FROM Notification WHERE user_id = #{userId} AND read_flag = #{readFlag}")
    List<Notification> findByUserIdAndReadFlag(Long userId, Boolean readFlag);

    @Insert("INSERT INTO Notification (user_id, title, message) VALUES (#{userId}, #{title}, #{message})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(Notification notification);

    @Update("UPDATE Notification SET read_flag = #{readFlag} WHERE id = #{id}")
    int updateReadFlag(Long id, Boolean readFlag);

    @Delete("DELETE FROM Notification WHERE id = #{id}")
    int deleteById(Long id);
} 