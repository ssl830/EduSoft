package org.example.edusoft.content.mapper.resource;

import org.apache.ibatis.annotations.*;
import org.example.edusoft.content.entity.resource.TeachingResource;
import org.example.edusoft.content.entity.resource.LearningProgress;
import org.example.edusoft.content.dto.resource.ResourceProgressDTO;
import java.util.List;
import java.util.Map;

@Mapper
public interface TeachingResourceMapper {
    
    @Insert("INSERT INTO teaching_resources (title, description, type, url, file_size, duration, course_id, chapter_id, creator_id, created_at, updated_at) VALUES (#{title}, #{description}, #{type}, #{url}, #{fileSize}, #{duration}, #{courseId}, #{chapterId}, #{creatorId}, #{createdAt}, #{updatedAt})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(TeachingResource resource);
    
    @Select("SELECT * FROM teaching_resources WHERE id = #{id}")
    TeachingResource selectById(Long id);
    
    @Select("SELECT * FROM teaching_resources WHERE course_id = #{courseId} ORDER BY chapter_id, created_at DESC")
    List<TeachingResource> selectByCourseId(Long courseId);
    
    @Select("SELECT * FROM teaching_resources WHERE course_id = #{courseId} AND chapter_id = #{chapterId} ORDER BY created_at DESC")
    List<TeachingResource> selectByCourseAndChapter(@Param("courseId") Long courseId, @Param("chapterId") Long chapterId);
    
    @Delete("DELETE FROM teaching_resources WHERE id = #{id}")
    int deleteById(Long id);
    
    @Update("UPDATE teaching_resources SET duration = #{duration}, updated_at = NOW() WHERE id = #{id}")
    int updateDuration(@Param("id") Long id, @Param("duration") Integer duration);
    
    // LearningProgress相关方法
    @Insert("INSERT INTO learning_progress (resource_id, student_id, progress, position, last_accessed_at) VALUES (#{resourceId}, #{studentId}, #{progress}, #{position}, #{lastAccessedAt}) ON DUPLICATE KEY UPDATE progress = #{progress}, position = #{position}, last_accessed_at = #{lastAccessedAt}")
    int insertOrUpdateProgress(LearningProgress progress);
    
    @Select("SELECT * FROM learning_progress WHERE resource_id = #{resourceId} AND student_id = #{studentId}")
    LearningProgress selectProgress(@Param("resourceId") Long resourceId, @Param("studentId") Long studentId);
    
    @Select("SELECT tr.*, lp.progress, lp.position, lp.last_accessed_at FROM teaching_resources tr LEFT JOIN learning_progress lp ON tr.id = lp.resource_id AND lp.student_id = #{studentId} WHERE tr.course_id = #{courseId} AND tr.chapter_id = #{chapterId} ORDER BY tr.created_at DESC")
    List<ResourceProgressDTO> selectResourcesWithProgress(@Param("courseId") Long courseId, @Param("chapterId") Long chapterId, @Param("studentId") Long studentId);
} 