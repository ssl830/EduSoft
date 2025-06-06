package org.example.edusoft.mapper;

import org.apache.ibatis.annotations.*;
import org.example.edusoft.entity.Submission;
import java.util.List;

@Mapper
public interface SubmissionMapper {
    @Select("SELECT * FROM Submission WHERE id = #{id}")
    Submission findById(Long id);

    @Select("SELECT * FROM Submission WHERE practice_id = #{practiceId}")
    List<Submission> findByPracticeId(Long practiceId);

    @Select("SELECT * FROM Submission WHERE student_id = #{studentId}")
    List<Submission> findByStudentId(Long studentId);

    @Select("SELECT * FROM Submission WHERE practice_id = #{practiceId} AND student_id = #{studentId}")
    List<Submission> findByPracticeAndStudent(Long practiceId, Long studentId);

    @Insert("INSERT INTO Submission (practice_id, student_id, score, feedback) " +
            "VALUES (#{practiceId}, #{studentId}, #{score}, #{feedback})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(Submission submission);

    @Update("UPDATE Submission SET score = #{score}, feedback = #{feedback} WHERE id = #{id}")
    int update(Submission submission);

    @Delete("DELETE FROM Submission WHERE id = #{id}")
    int deleteById(Long id);
} 