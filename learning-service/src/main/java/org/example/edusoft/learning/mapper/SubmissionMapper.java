package org.example.edusoft.learning.mapper;

import org.apache.ibatis.annotations.*;
import org.example.edusoft.learning.entity.PracticeSubmission;
import java.util.List;

@Mapper
public interface SubmissionMapper {
    @Insert({
        "INSERT INTO submission(practice_id, student_id, score, is_judged)",
        "VALUES(#{practiceId}, #{studentId}, #{score}, #{isJudged})"
    })
    @Options(useGeneratedKeys = true, keyProperty = "id")
    void insert(PracticeSubmission submission);

    @Update({
        "UPDATE submission SET score=#{score}, is_judged=#{isJudged}",
        "WHERE id=#{id}"
    })
    void update(PracticeSubmission submission);

    @Delete("DELETE FROM submission WHERE practice_id = #{id}")
    void removeSubmissionsByPracticeId(Long practiceId);

    @Select("SELECT * FROM submission WHERE id = #{id}")
    PracticeSubmission selectById(Long id);

    @Select("SELECT * FROM submission WHERE practice_id = #{practiceId}")
    List<PracticeSubmission> findByPracticeId(Long practiceId);

    @Select("SELECT * FROM submission WHERE practice_id = #{practiceId} AND is_judged = 0")
    List<PracticeSubmission> findByPracticeIdWithUnjudgedAnswers(Long practiceId);

    @Select("SELECT id FROM submission WHERE practice_id = #{practiceId}")
    List<Long> findSubmissionIdsByPracticeId(Long practiceId);
}
