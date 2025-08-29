package org.example.edusoft.mapper.discussion;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.*;
import org.example.edusoft.entity.discussion.Discussion;
import java.util.List;

@Mapper
public interface DiscussionMapper extends BaseMapper<Discussion> {
    
    @Select("SELECT * FROM discussion WHERE course_id = #{courseId} ORDER BY is_pinned DESC, created_at DESC")
    List<Discussion> findByCourseId(@Param("courseId") Long courseId);
    
    @Select("SELECT * FROM discussion WHERE class_id = #{classId} ORDER BY is_pinned DESC, created_at DESC")
    List<Discussion> findByClassId(@Param("classId") Long classId);
    
    @Select("SELECT * FROM discussion WHERE creator_id = #{creatorId} ORDER BY created_at DESC")
    List<Discussion> findByCreatorId(@Param("creatorId") Long creatorId);
    
    @Update("UPDATE discussion SET view_count = view_count + 1 WHERE id = #{id}")
    int incrementViewCount(@Param("id") Long id);
    
    @Update("UPDATE discussion SET reply_count = reply_count + 1 WHERE id = #{id}")
    int incrementReplyCount(@Param("id") Long id);
    
    @Update("UPDATE discussion SET reply_count = reply_count - 1 WHERE id = #{id}")
    int decrementReplyCount(@Param("id") Long id);
    
    @Update("UPDATE discussion SET is_pinned = #{isPinned} WHERE id = #{id}")
    int updatePinnedStatus(@Param("id") Long id, @Param("isPinned") Boolean isPinned);
    
    @Update("UPDATE discussion SET is_closed = #{isClosed} WHERE id = #{id}")
    int updateClosedStatus(@Param("id") Long id, @Param("isClosed") Boolean isClosed);
    
    @Select("SELECT * FROM discussion WHERE course_id = #{courseId} AND class_id = #{classId} " +
            "ORDER BY is_pinned DESC, created_at DESC")
    List<Discussion> findByCourseAndClass(@Param("courseId") Long courseId, @Param("classId") Long classId);
    
    @Select("SELECT COUNT(*) FROM discussion WHERE course_id = #{courseId}")
    int countByCourseId(@Param("courseId") Long courseId);
    
    @Select("SELECT COUNT(*) FROM discussion WHERE class_id = #{classId}")
    int countByClassId(@Param("classId") Long classId);
} 