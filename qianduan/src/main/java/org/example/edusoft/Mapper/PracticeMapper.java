package org.example.edusoft.mapper;

import org.apache.ibatis.annotations.*;
import org.example.edusoft.entity.Practice;
import java.util.List;

@Mapper
public interface PracticeMapper {
    @Select("SELECT * FROM Practice WHERE id = #{id}")
    Practice findById(Long id);

    @Select("SELECT * FROM Practice WHERE course_id = #{courseId}")
    List<Practice> findByCourseId(Long courseId);

    @Select("SELECT * FROM Practice WHERE created_by = #{createdBy}")
    List<Practice> findByCreatedBy(Long createdBy);

    @Insert("INSERT INTO Practice (course_id, title, start_time, end_time, " +
            "allow_multiple_submission, created_by) VALUES (#{courseId}, #{title}, " +
            "#{startTime}, #{endTime}, #{allowMultipleSubmission}, #{createdBy})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(Practice practice);

    @Update("UPDATE Practice SET title = #{title}, start_time = #{startTime}, " +
            "end_time = #{endTime}, allow_multiple_submission = #{allowMultipleSubmission} " +
            "WHERE id = #{id}")
    int update(Practice practice);

    @Delete("DELETE FROM Practice WHERE id = #{id}")
    int deleteById(Long id);
} 