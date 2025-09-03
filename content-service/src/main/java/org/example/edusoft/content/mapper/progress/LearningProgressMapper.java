package org.example.edusoft.content.mapper.progress;

import org.apache.ibatis.annotations.*;
import org.example.edusoft.content.entity.resource.LearningProgress;
import org.example.edusoft.content.dto.progress.LearningProgressDTO;
import org.example.edusoft.content.dto.progress.ProgressStatisticsDTO;
import java.util.List;

@Mapper
public interface LearningProgressMapper {

    /**
     * 插入或更新学习进度
     * @param progress 学习进度对象
     */
    @Insert({
            "<script>",
            "INSERT INTO learning_progress (resource_id, student_id, progress, last_position, watch_count, last_watch_time)",
            "VALUES (#{resourceId}, #{studentId}, #{progress}, #{lastPosition}, 1, #{lastWatchTime})",
            "ON DUPLICATE KEY UPDATE",
            "progress = #{progress},",
            "last_position = #{lastPosition},",
            "watch_count = watch_count + 1,",
            "last_watch_time = #{lastWatchTime}",
            "</script>"
    })
    void insertOrUpdate(LearningProgress progress);

    /**
     * 根据资源ID和学生ID查询学习进度
     * @param resourceId 资源ID
     * @param studentId 学生ID
     * @return 学习进度对象
     */
    @Select("SELECT * FROM learning_progress WHERE resource_id = #{resourceId} AND student_id = #{studentId}")
    LearningProgress selectByResourceAndStudent(@Param("resourceId") Long resourceId, @Param("studentId") Long studentId);
    
    @Insert("INSERT INTO learning_progress (resource_id, student_id, progress, position, last_accessed_at, created_at, updated_at) " +
            "VALUES (#{resourceId}, #{studentId}, #{progress}, #{position}, #{lastAccessedAt}, #{createdAt}, #{updatedAt}) " +
            "ON DUPLICATE KEY UPDATE progress = #{progress}, position = #{position}, last_accessed_at = #{lastAccessedAt}, updated_at = #{updatedAt}")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insertOrUpdateProgress(LearningProgress progress);
    
    @Select("SELECT * FROM learning_progress WHERE resource_id = #{resourceId} AND student_id = #{studentId}")
    LearningProgress selectProgress(@Param("resourceId") Long resourceId, @Param("studentId") Long studentId);
    
    @Select("SELECT lp.*, tr.title as resourceTitle, tr.course_id as courseId, tr.chapter_id as chapterId " +
            "FROM learning_progress lp " +
            "JOIN teaching_resources tr ON lp.resource_id = tr.id " +
            "WHERE lp.student_id = #{studentId}")
    List<LearningProgressDTO> selectStudentProgress(@Param("studentId") Long studentId);
    
    @Select("SELECT lp.*, tr.title as resourceTitle, tr.course_id as courseId, tr.chapter_id as chapterId " +
            "FROM learning_progress lp " +
            "JOIN teaching_resources tr ON lp.resource_id = tr.id " +
            "WHERE lp.resource_id = #{resourceId}")
    List<LearningProgressDTO> selectResourceProgress(@Param("resourceId") Long resourceId);
    
    @Select("SELECT tr.id as resourceId, tr.title as resourceTitle, tr.course_id as courseId, tr.chapter_id as chapterId, " +
            "COUNT(lp.student_id) as totalStudents, " +
            "COUNT(CASE WHEN lp.progress >= 0.8 THEN 1 END) as completedStudents, " +
            "AVG(lp.progress) as averageProgress, " +
            "AVG(lp.position) as averageWatchTime " +
            "FROM teaching_resources tr " +
            "LEFT JOIN learning_progress lp ON tr.id = lp.resource_id " +
            "WHERE tr.course_id = #{courseId} " +
            "GROUP BY tr.id")
    List<ProgressStatisticsDTO> selectCourseProgressStatistics(@Param("courseId") Long courseId);
    
    @Select("SELECT tr.id as resourceId, tr.title as resourceTitle, tr.course_id as courseId, tr.chapter_id as chapterId, " +
            "COUNT(lp.student_id) as totalStudents, " +
            "COUNT(CASE WHEN lp.progress >= 0.8 THEN 1 END) as completedStudents, " +
            "AVG(lp.progress) as averageProgress, " +
            "AVG(lp.position) as averageWatchTime " +
            "FROM teaching_resources tr " +
            "LEFT JOIN learning_progress lp ON tr.id = lp.resource_id " +
            "WHERE tr.chapter_id = #{chapterId} " +
            "GROUP BY tr.id")
    List<ProgressStatisticsDTO> selectChapterProgressStatistics(@Param("chapterId") Long chapterId);
    
    @Delete("DELETE FROM learning_progress WHERE resource_id = #{resourceId} AND student_id = #{studentId}")
    int deleteProgress(@Param("resourceId") Long resourceId, @Param("studentId") Long studentId);
    
    @Update("UPDATE learning_progress SET progress = #{progress}, position = #{position}, " +
            "last_accessed_at = #{lastAccessedAt}, updated_at = #{updatedAt} " +
            "WHERE resource_id = #{resourceId} AND student_id = #{studentId}")
    int updateProgress(LearningProgress progress);

    /**
     * 删除资源的所有学习进度记录
     * @param resourceId 资源ID
     * @return 影响的行数
     */
    @Delete("DELETE FROM learning_progress WHERE resource_id = #{resourceId}")
    int deleteByResourceId(@Param("resourceId") Long resourceId);
}
