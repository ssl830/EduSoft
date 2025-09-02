package org.example.edusoft.learning.mapper;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;
import org.example.edusoft.learning.dto.PracticeDTO;
import org.example.edusoft.learning.entity.Practice;

@Mapper
public interface PracticeMapper {

    @Select("SELECT id FROM practice WHERE end_time < #{now}")
    List<Long> findAllEndedPracticeIds(@Param("now") java.time.LocalDateTime now);
    
    @Insert("INSERT INTO practice (course_id, class_id, title, start_time, end_time, allow_multiple_submission, created_by, created_at) " +
            "VALUES (#{courseId}, #{classId}, #{title}, #{startTime}, #{endTime}, #{allowMultipleSubmission}, #{createdBy}, #{createdAt})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int createPractice(Practice practice);

    @Update("UPDATE practice SET title = #{title}, start_time = #{startTime}, end_time = #{endTime}, " +
            "allow_multiple_submission = #{allowMultipleSubmission} WHERE id = #{id}")
    void updatePractice(Practice practice);

    @Select("SELECT * FROM practice WHERE class_id = #{classId} ORDER BY created_at DESC")
    List<Practice> getPracticeList(@Param("classId") Long classId);

    @Select("SELECT * FROM practice WHERE course_id = #{courseId} ORDER BY created_at DESC")
    List<Practice> getPracticeListByCourse(@Param("courseId") Long courseId);

    @Select("SELECT * FROM practice WHERE id = #{id}")
    Practice getPracticeById(Long id);

    @Delete("DELETE FROM practice WHERE id = #{id}")
    void deletePractice(Long id);

    @Select("SELECT COUNT(*) FROM practice WHERE course_id = #{courseId} AND class_id = #{classId}")
    int getPracticeCount(@Param("courseId") Long courseId, @Param("classId") Long classId);

    @Select("SELECT id, title, start_time, end_time, course_id, class_id FROM practice WHERE created_by = #{teacherId} ORDER BY created_at DESC")
    List<Map<String, Object>> getPracticesByTeacherId(@Param("teacherId") Long teacherId);
    
    @Update("UPDATE practice_question SET score = #{score} WHERE practice_id = #{practiceId} AND question_id = #{questionId}")
    int updateQuestionScore(@Param("practiceId") Long practiceId, @Param("questionId") Long questionId, @Param("score") Integer score);

    @Select("""
            SELECT 
                p.*,
                CASE WHEN s.id IS NOT NULL THEN TRUE ELSE FALSE END as isCompleted,
                (SELECT COUNT(*) FROM submission s2 WHERE s2.practice_id = p.id AND s2.student_id = #{studentId}) as submissionCount,
                (SELECT MAX(score) FROM submission s3 WHERE s3.practice_id = p.id AND s3.student_id = #{studentId}) as score
            FROM practice p
            LEFT JOIN submission s ON p.id = s.practice_id AND s.student_id = #{studentId}
            WHERE p.class_id = #{classId}
            ORDER BY p.created_at DESC
            """)
    List<PracticeDTO> getStudentPracticeList(Long studentId, Long classId);
}
