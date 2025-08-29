package org.example.edusoft.content.mapper;

import org.example.edusoft.content.entity.TeachingResource;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface TeachingResourceMapper {
    
    @Select("SELECT * FROM teaching_resource WHERE id = #{id}")
    TeachingResource findById(@Param("id") Long id);
    
    @Select("SELECT * FROM teaching_resource WHERE author_id = #{authorId} AND status = 'published'")
    List<TeachingResource> findByAuthorId(@Param("authorId") Long authorId);
    
    @Select("SELECT * FROM teaching_resource WHERE course_id = #{courseId} AND status = 'published'")
    List<TeachingResource> findByCourseId(@Param("courseId") Long courseId);
    
    @Select("SELECT * FROM teaching_resource WHERE chapter_id = #{chapterId} AND status = 'published'")
    List<TeachingResource> findByChapterId(@Param("chapterId") Long chapterId);
    
    @Select("SELECT * FROM teaching_resource WHERE resource_type = #{resourceType} AND status = 'published'")
    List<TeachingResource> findByResourceType(@Param("resourceType") String resourceType);
    
    @Select("SELECT * FROM teaching_resource WHERE status = 'published' ORDER BY created_at DESC")
    List<TeachingResource> findAllPublished();
    
    @Insert("INSERT INTO teaching_resource (title, description, content, course_id, chapter_id, chapter_name, " +
            "resource_type, file_url, object_name, duration, author_id, author_name, tags, view_count, " +
            "download_count, status, created_at, updated_at) VALUES (#{title}, #{description}, #{content}, " +
            "#{courseId}, #{chapterId}, #{chapterName}, #{resourceType}, #{fileUrl}, #{objectName}, " +
            "#{duration}, #{authorId}, #{authorName}, #{tags}, 0, 0, #{status}, NOW(), NOW())")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    void insert(TeachingResource resource);
    
    @Update("UPDATE teaching_resource SET title = #{title}, description = #{description}, content = #{content}, " +
            "course_id = #{courseId}, chapter_id = #{chapterId}, chapter_name = #{chapterName}, " +
            "resource_type = #{resourceType}, file_url = #{fileUrl}, object_name = #{objectName}, " +
            "duration = #{duration}, tags = #{tags}, status = #{status}, updated_at = NOW() WHERE id = #{id}")
    void update(TeachingResource resource);
    
    @Update("UPDATE teaching_resource SET view_count = view_count + 1 WHERE id = #{id}")
    void incrementViewCount(@Param("id") Long id);
    
    @Update("UPDATE teaching_resource SET download_count = download_count + 1 WHERE id = #{id}")
    void incrementDownloadCount(@Param("id") Long id);
    
    @Update("UPDATE teaching_resource SET status = 'archived', updated_at = NOW() WHERE id = #{id}")
    void archiveById(@Param("id") Long id);
}
