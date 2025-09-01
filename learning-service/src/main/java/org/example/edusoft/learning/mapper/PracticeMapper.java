package org.example.edusoft.learning.mapper;

import org.apache.ibatis.annotations.*;
import org.example.edusoft.learning.entity.Practice;
import org.example.edusoft.learning.dto.PracticeDTO;
import java.util.List;
import java.util.Map;

@Mapper
public interface PracticeMapper {

    @Select("SELECT id FROM practice WHERE end_time < #{now}")
    List<Long> findAllEndedPracticeIds(@Param("now") java.time.LocalDateTime now);
    
    @Insert("INSERT INTO practice (course_id, class_id, title, start_time, end_time, allow_multiple_submission, created_by) " +
            "VALUES (#{courseId}, #{classId}, #{title}, #{startTime}, #{endTime}, #{allowMultipleSubmission}, #{createdBy})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    void createPractice(Practice practice);

    @Update("UPDATE practice SET title = #{title}, start_time = #{startTime}, end_time = #{endTime}, " +
            "allow_multiple_submission = #{allowMultipleSubmission} WHERE id = #{id}")
    void updatePractice(Practice practice);

    @Select("SELECT * FROM practice WHERE class_id = #{classId} ORDER BY created_at DESC")
    List<Practice> getPracticeList(@Param("classId") Long classId);

    @Select("SELECT * FROM practice WHERE id = #{id}")
    Practice getPracticeById(Long id);

    @Delete("DELETE FROM practice WHERE id = #{id}")
    void deletePractice(Long id);

    @Select("SELECT COUNT(*) FROM practice WHERE course_id = #{courseId} AND class_id = #{classId}")
    int getPracticeCount(@Param("courseId") Long courseId, @Param("classId") Long classId);

    @Select("SELECT id, title, start_time, end_time, course_id, class_id FROM practice WHERE created_by = #{teacherId} ORDER BY created_at DESC")
    List<Map<String, Object>> getPracticesByTeacherId(@Param("teacherId") Long teacherId);
}
