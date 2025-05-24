package org.example.edusoft.mapper.resource;

import org.apache.ibatis.annotations.*;
import org.example.edusoft.entity.resource.TeachingResource;
import java.util.List;

/**
 * 教学资源数据访问接口
 */
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

    /**
     * 根据课程ID查询所有教学资源
     * @param courseId 课程ID
     * @return 教学资源列表
     */
    @Select("SELECT * FROM teaching_resource WHERE course_id = #{courseId} ORDER BY chapter_id, created_at")
    List<TeachingResource> selectByCourseId(Long courseId);

    /**
     * 根据课程ID和章节ID查询教学资源
     * @param courseId 课程ID
     * @param chapterId 章节ID
     * @return 教学资源列表
     */
    @Select("SELECT * FROM teaching_resource WHERE course_id = #{courseId} AND chapter_id = #{chapterId} ORDER BY created_at")
    List<TeachingResource> selectByChapter(@Param("courseId") Long courseId, @Param("chapterId") Long chapterId);

    /**
     * 删除教学资源
     * @param id 资源ID
     * @return 影响的行数
     */
    @Delete("DELETE FROM teaching_resource WHERE id = #{id}")
    int deleteById(Long id);

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
} 