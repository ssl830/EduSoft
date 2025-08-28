package org.example.edusoft.mapper.practice;

import org.apache.ibatis.annotations.*;
import org.example.edusoft.entity.practice.Submission;
import java.util.List;

@Mapper
public interface SubmissionMapper {
    @Insert({
        "INSERT INTO submission(practice_id, student_id, score, is_judged)",
        "VALUES(#{practiceId}, #{studentId}, #{score}, #{isJudged})"
    })
    @Options(useGeneratedKeys = true, keyProperty = "id")
    void insert(Submission submission);

    @Update({
        "UPDATE submission SET score=#{score}, is_judged=#{isJudged}",
        "WHERE id=#{id}"
    })
    void update(Submission submission);

    @Delete("DELETE FROM submission WHERE practice_id = #{id}")
    void removeSubmissionsByPracticeId(Long practiceId);

    @Select("SELECT * FROM submission WHERE id = #{id}")
    Submission selectById(Long id);

    @Select("SELECT * FROM submission WHERE practice_id = #{practiceId}")
    List<Submission> findByPracticeId(Long practiceId);

    @Select("SELECT * FROM submission WHERE practice_id = #{practiceId} AND is_judged = 0")
    List<Submission> findByPracticeIdWithUnjudgedAnswers(Long practiceId);

    @Select("SELECT id FROM submission WHERE practice_id = #{practiceId}")
    List<Long> findSubmissionIdsByPracticeId(Long practiceId);
}