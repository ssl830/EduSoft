package org.example.edusoft.content.mapper;

import org.example.edusoft.content.entity.Discussion;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface DiscussionMapper {
    
    @Select("SELECT * FROM discussion WHERE id = #{id}")
    Discussion findById(@Param("id") Long id);
    
    @Select("SELECT * FROM discussion WHERE creator_id = #{creatorId} AND status = 'active' ORDER BY created_at DESC")
    List<Discussion> findByCreatorId(@Param("creatorId") Long creatorId);
    
    @Select("SELECT * FROM discussion WHERE course_id = #{courseId} AND status = 'active' ORDER BY created_at DESC")
    List<Discussion> findByCourseId(@Param("courseId") Long courseId);
    
    @Select("SELECT * FROM discussion WHERE class_id = #{classId} AND status = 'active' ORDER BY created_at DESC")
    List<Discussion> findByClassId(@Param("classId") Long classId);
    
    @Select("SELECT * FROM discussion WHERE category = #{category} AND status = 'active' ORDER BY created_at DESC")
    List<Discussion> findByCategory(@Param("category") String category);
    
    @Select("SELECT * FROM discussion WHERE type = #{type} AND status = 'active' ORDER BY created_at DESC")
    List<Discussion> findByType(@Param("type") String type);
    
    @Select("SELECT * FROM discussion WHERE status = 'active' ORDER BY created_at DESC")
    List<Discussion> findAllActive();
    
    @Select("SELECT * FROM discussion ORDER BY created_at DESC")
    List<Discussion> findAll();
    
    @Insert("INSERT INTO discussion (course_id, class_id, creator_id, creator_num, title, content, " +
            "category, is_pinned, is_closed, view_count, reply_count, like_count, status, created_at, " +
            "updated_at) VALUES (#{courseId}, #{classId}, #{creatorId}, #{creatorNum}, #{title}, " +
            "#{content}, #{category}, #{isPinned}, #{isClosed}, 0, 0, 0, 'active', NOW(), NOW())")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    void insert(Discussion discussion);
    
    @Update("UPDATE discussion SET title = #{title}, content = #{content}, category = #{category}, " +
            "is_pinned = #{isPinned}, is_closed = #{isClosed}, updated_at = NOW() WHERE id = #{id}")
    void update(Discussion discussion);
    
    @Update("UPDATE discussion SET view_count = view_count + 1 WHERE id = #{id}")
    void incrementViewCount(@Param("id") Long id);
    
    @Update("UPDATE discussion SET reply_count = reply_count + 1 WHERE id = #{id}")
    void incrementReplyCount(@Param("id") Long id);
    
    @Update("UPDATE discussion SET like_count = like_count + 1 WHERE id = #{id}")
    void incrementLikeCount(@Param("id") Long id);
    
    @Update("UPDATE discussion SET is_closed = true, updated_at = NOW() WHERE id = #{id}")
    void closeById(@Param("id") Long id);
    
    @Update("UPDATE discussion SET is_pinned = true, updated_at = NOW() WHERE id = #{id}")
    void pinById(@Param("id") Long id);
    
    @Update("UPDATE discussion SET is_pinned = false, updated_at = NOW() WHERE id = #{id}")
    void unpinById(@Param("id") Long id);
}
