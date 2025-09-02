package org.example.edusoft.content.mapper.discussion;

import org.apache.ibatis.annotations.*;
import org.example.edusoft.content.entity.discussion.Discussion;
import java.util.List;

@Mapper
public interface DiscussionMapper {
    
    @Insert("INSERT INTO discussion (title, content, course_id, class_id, creator_id, creator_num, view_count, reply_count, is_pinned, is_closed, created_at, updated_at) VALUES (#{title}, #{content}, #{courseId}, #{classId}, #{creatorId}, #{creatorNum}, #{viewCount}, #{replyCount}, #{isPinned}, #{isClosed}, NOW(), NOW())")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(Discussion discussion);
        
    @Update("UPDATE discussion SET title = #{title}, content = #{content}, course_id = #{courseId}, class_id = #{classId}, creator_id = #{creatorId}, creator_num = #{creatorNum}, view_count = #{viewCount}, reply_count = #{replyCount}, is_pinned = #{isPinned}, is_closed = #{isClosed}, updated_at = NOW() WHERE id = #{id}")
    int update(Discussion discussion);
        
    @Delete("DELETE FROM discussion WHERE id = #{id}")
    int deleteById(Long id);
        
    @Select("SELECT * FROM discussion WHERE id = #{id}")
    Discussion selectById(Long id);
        
    @Update("UPDATE discussion SET view_count = view_count + 1 WHERE id = #{id}")
    int incrementViewCount(Long id);
        
    @Select("SELECT * FROM discussion WHERE course_id = #{courseId} ORDER BY created_at DESC")
    List<Discussion> selectByCourseId(Long courseId);
        
    @Select("SELECT * FROM discussion WHERE class_id = #{classId} ORDER BY created_at DESC")
    List<Discussion> selectByClassId(Long classId);
        
    @Select("SELECT * FROM discussion WHERE creator_id = #{creatorId} ORDER BY created_at DESC")
    List<Discussion> selectByCreatorId(Long creatorId);
        
    @Select("SELECT * FROM discussion WHERE course_id = #{courseId} AND class_id = #{classId} ORDER BY created_at DESC")
    List<Discussion> selectByCourseAndClass(@Param("courseId") Long courseId, @Param("classId") Long classId);
        
    @Update("UPDATE discussion SET is_pinned = #{isPinned}, updated_at = NOW() WHERE id = #{id}")
    int updatePinnedStatus(@Param("id") Long id, @Param("isPinned") Boolean isPinned);
        
    @Update("UPDATE discussion SET is_closed = #{isClosed}, updated_at = NOW() WHERE id = #{id}")
    int updateClosedStatus(@Param("id") Long id, @Param("isClosed") Boolean isClosed);
        
    @Select("SELECT COUNT(*) FROM discussion WHERE course_id = #{courseId}")
    Integer countByCourseId(Long courseId);
        
    @Select("SELECT COUNT(*) FROM discussion WHERE class_id = #{classId}")
    Integer countByClassId(Long classId);
}
