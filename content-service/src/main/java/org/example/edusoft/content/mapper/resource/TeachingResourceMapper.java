package org.example.edusoft.content.mapper.resource;

import org.apache.ibatis.annotations.*;
import org.example.edusoft.content.entity.resource.TeachingResource;
import org.example.edusoft.content.entity.resource.LearningProgress;
import org.example.edusoft.content.dto.resource.ResourceProgressDTO;
import java.util.List;
import java.util.Map;

@Mapper
public interface TeachingResourceMapper {

    /**
     * 插入教学资源
     * @param resource 教学资源对象
     */
    @Insert({
            "INSERT INTO teaching_resource (title, description, course_id, chapter_id, chapter_name,",
            "resource_type, file_url, object_name, duration, created_by)",
            "VALUES (#{title}, #{description}, #{courseId}, #{chapterId}, #{chapterName},",
            "#{resourceType}, #{fileUrl}, #{objectName}, #{duration}, #{createdBy})"
    })
    @Options(useGeneratedKeys = true, keyProperty = "id")
    void insert(TeachingResource resource);

    /**
     * 根据ID查询教学资源
     * @param id 资源ID
     * @return 教学资源对象
     */
    @Select("SELECT * FROM teaching_resource WHERE id = #{id}")
    TeachingResource selectById(Long id);
    
    @Select("SELECT * FROM teaching_resource WHERE course_id = #{courseId} ORDER BY chapter_id, created_at DESC")
    List<TeachingResource> selectByCourseId(Long courseId);
    
    @Select("SELECT * FROM teaching_resource WHERE course_id = #{courseId} AND chapter_id = #{chapterId} ORDER BY created_at DESC")
    List<TeachingResource> selectByChapter(@Param("courseId") Long courseId, @Param("chapterId") Long chapterId);
    
    @Delete("DELETE FROM teaching_resource WHERE id = #{id}")
    int deleteById(Long id);
    
    @Update("UPDATE teaching_resource SET duration = #{duration}, updated_at = NOW() WHERE id = #{id}")
    int updateDuration(@Param("id") Long id, @Param("duration") Integer duration);
    
    // LearningProgress相关方法
    @Insert("INSERT INTO learning_progress (resource_id, student_id, progress, position, last_accessed_at) VALUES (#{resourceId}, #{studentId}, #{progress}, #{position}, #{lastAccessedAt}) ON DUPLICATE KEY UPDATE progress = #{progress}, position = #{position}, last_accessed_at = #{lastAccessedAt}")
    int insertOrUpdateProgress(LearningProgress progress);
    
    @Select("SELECT * FROM learning_progress WHERE resource_id = #{resourceId} AND student_id = #{studentId}")
    LearningProgress selectByResourceAndStudent(@Param("resourceId") Long resourceId, @Param("studentId") Long studentId);
    
    @Select("SELECT tr.*, lp.progress, lp.position, lp.last_accessed_at FROM teaching_resource tr LEFT JOIN learning_progress lp ON tr.id = lp.resource_id AND lp.student_id = #{studentId} WHERE tr.course_id = #{courseId} AND tr.chapter_id = #{chapterId} ORDER BY tr.created_at DESC")
    List<ResourceProgressDTO> selectResourcesWithProgress(@Param("courseId") Long courseId, @Param("chapterId") Long chapterId, @Param("studentId") Long studentId);

    /**
     * 更新教学资源信息
     * @param resource 教学资源对象
     * @return 影响的行数
     */
    @Update({
            "UPDATE teaching_resource SET",
            "title = #{title},",
            "description = #{description},",
            "chapter_id = #{chapterId},",
            "chapter_name = #{chapterName},",
            "file_url = #{fileUrl},",
            "duration = #{duration},",
            "updated_at = CURRENT_TIMESTAMP",
            "WHERE id = #{id}"
    })
    int update(TeachingResource resource);

    /**
     * 根据课程ID和可选的章节ID查询教学资源
     * @param courseId 课程ID
     * @param chapterId 章节ID（-1表示不筛选章节）
     * @return 教学资源列表
     */
    @Select({
            "<script>",
            "SELECT * FROM teaching_resource",
            "WHERE course_id = #{courseId}",
            "<if test='chapterId != -1'>",
            "AND chapter_id = #{chapterId}",
            "</if>",
            "ORDER BY chapter_id, created_at",
            "</script>"
    })
    List<TeachingResource> selectByCourseAndChapter(@Param("courseId") Long courseId, @Param("chapterId") Long chapterId);

    /**
     * 统计指定课程的资源总数
     * @param courseId 课程ID
     * @return 资源总数
     */
    @Select("SELECT COUNT(*) FROM teaching_resource WHERE course_id = #{courseId}")
    int countByCourseId(Long courseId);
}