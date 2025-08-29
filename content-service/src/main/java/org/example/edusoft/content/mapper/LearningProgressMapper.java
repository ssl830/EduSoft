package org.example.edusoft.content.mapper;

import org.example.edusoft.content.entity.LearningProgress;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface LearningProgressMapper {
    
    @Select("SELECT * FROM learning_progress WHERE id = #{id}")
    LearningProgress findById(@Param("id") Long id);
    
    @Select("SELECT * FROM learning_progress WHERE resource_id = #{resourceId} AND student_id = #{studentId}")
    LearningProgress findByResourceAndStudent(@Param("resourceId") Long resourceId, @Param("studentId") Long studentId);
    
    @Select("SELECT * FROM learning_progress WHERE student_id = #{studentId}")
    List<LearningProgress> findByStudentId(@Param("studentId") Long studentId);
    
    @Select("SELECT * FROM learning_progress WHERE resource_id = #{resourceId}")
    List<LearningProgress> findByResourceId(@Param("resourceId") Long resourceId);
    
    @Insert("INSERT INTO learning_progress (resource_id, student_id, progress, last_position, watch_count, " +
            "last_watch_time, created_at, updated_at) VALUES (#{resourceId}, #{studentId}, #{progress}, " +
            "#{lastPosition}, #{watchCount}, #{lastWatchTime}, NOW(), NOW())")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    void insert(LearningProgress progress);
    
    @Update("UPDATE learning_progress SET progress = #{progress}, last_position = #{lastPosition}, " +
            "watch_count = #{watchCount}, last_watch_time = #{lastWatchTime}, updated_at = NOW() " +
            "WHERE resource_id = #{resourceId} AND student_id = #{studentId}")
    void update(LearningProgress progress);
    
    @Update("UPDATE learning_progress SET progress = #{progress}, last_position = #{lastPosition}, " +
            "updated_at = NOW() WHERE resource_id = #{resourceId} AND student_id = #{studentId}")
    void updateProgress(@Param("resourceId") Long resourceId, @Param("studentId") Long studentId, 
                       @Param("progress") Integer progress, @Param("lastPosition") Integer lastPosition);
    
    @Update("UPDATE learning_progress SET watch_count = watch_count + 1, last_watch_time = NOW(), " +
            "updated_at = NOW() WHERE resource_id = #{resourceId} AND student_id = #{studentId}")
    void incrementWatchCount(@Param("resourceId") Long resourceId, @Param("studentId") Long studentId);
}
