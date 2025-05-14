package org.example.edusoft.mapper;

import org.apache.ibatis.annotations.*;
import org.example.edusoft.entity.Resource;
import java.util.List;

@Mapper
public interface ResourceMapper {
    @Select("SELECT * FROM Resource WHERE id = #{id}")
    Resource findById(Long id);

    @Select("SELECT * FROM Resource WHERE course_id = #{courseId}")
    List<Resource> findByCourseId(Long courseId);

    @Select("SELECT * FROM Resource WHERE section_id = #{sectionId}")
    List<Resource> findBySectionId(Long sectionId);

    @Select("SELECT * FROM Resource WHERE uploader_id = #{uploaderId}")
    List<Resource> findByUploaderId(Long uploaderId);

    @Insert("INSERT INTO Resource (course_id, section_id, uploader_id, title, type, " +
            "file_url, visibility, version) VALUES (#{courseId}, #{sectionId}, " +
            "#{uploaderId}, #{title}, #{type}, #{fileUrl}, #{visibility}, #{version})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(Resource resource);

    @Update("UPDATE Resource SET title = #{title}, visibility = #{visibility}, " +
            "version = #{version} WHERE id = #{id}")
    int update(Resource resource);

    @Delete("DELETE FROM Resource WHERE id = #{id}")
    int deleteById(Long id);
} 