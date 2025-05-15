package org.example.edusoft.Mapper;

import org.apache.ibatis.annotations.*;
import org.example.edusoft.entity.Progress;
import java.util.List;

@Mapper
public interface ProgressMapper {
    @Select("SELECT * FROM Progress WHERE id = #{id}")
    Progress findById(Long id);

    @Select("SELECT * FROM Progress WHERE student_id = #{studentId}")
    List<Progress> findByStudentId(Long studentId);

    @Select("SELECT * FROM Progress WHERE course_id = #{courseId}")
    List<Progress> findByCourseId(Long courseId);

    @Select("SELECT * FROM Progress WHERE student_id = #{studentId} AND course_id = #{courseId}")
    List<Progress> findByStudentAndCourse(Long studentId, Long courseId);

    @Insert("INSERT INTO Progress (student_id, course_id, section_id, completed, completed_at) " +
            "VALUES (#{studentId}, #{courseId}, #{sectionId}, #{completed}, #{completedAt})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(Progress progress);

    @Update("UPDATE Progress SET completed = #{completed}, completed_at = #{completedAt} " +
            "WHERE id = #{id}")
    int update(Progress progress);

    @Delete("DELETE FROM Progress WHERE id = #{id}")
    int deleteById(Long id);
} 