package org.example.edusoft.learning.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;
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
    
    @Update("UPDATE practice_question SET score = #{score} WHERE practice_id = #{practiceId} AND question_id = #{questionId}")
    int updateQuestionScore(@Param("practiceId") Long practiceId, @Param("questionId") Long questionId, @Param("score") Integer score);
}
