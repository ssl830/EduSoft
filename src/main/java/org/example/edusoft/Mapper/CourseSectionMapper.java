package org.example.edusoft.Mapper;

import org.apache.ibatis.annotations.*;
import org.example.edusoft.entity.CourseSection;
import java.util.List;

@Mapper
public interface CourseSectionMapper {
    @Select("SELECT * FROM CourseSection WHERE id = #{id}")
    CourseSection findById(Long id);

    @Select("SELECT * FROM CourseSection WHERE course_id = #{courseId} ORDER BY sort_order")
    List<CourseSection> findByCourseId(Long courseId);

    @Insert("INSERT INTO CourseSection (course_id, title, sort_order) " +
            "VALUES (#{courseId}, #{title}, #{sortOrder})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(CourseSection section);

    @Update("UPDATE CourseSection SET title = #{title}, sort_order = #{sortOrder} " +
            "WHERE id = #{id}")
    int update(CourseSection section);

    @Delete("DELETE FROM CourseSection WHERE id = #{id}")
    int deleteById(Long id);
} 