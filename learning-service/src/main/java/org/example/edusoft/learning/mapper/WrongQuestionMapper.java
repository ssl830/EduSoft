package org.example.edusoft.learning.mapper;

import org.apache.ibatis.annotations.*;
import java.util.List;
import java.util.Map;

@Mapper
public interface WrongQuestionMapper {
    @Select("SELECT COUNT(*) FROM wrong_question WHERE student_id = #{studentId} AND question_id = #{questionId}")
    boolean existsWrongQuestion(@Param("studentId") Long studentId, @Param("questionId") Long questionId);

    @Insert("""
                INSERT INTO wrong_question
                (student_id, question_id, wrong_answer, correct_answer)
                VALUES
                (#{studentId}, #{questionId}, #{wrongAnswer}, #{correctAnswer})
            """)
    void insertWrongQuestion(
            @Param("studentId") Long studentId,
            @Param("questionId") Long questionId,
            @Param("wrongAnswer") String wrongAnswer,
            @Param("correctAnswer") String correctAnswer);

    @Update("""
                UPDATE wrong_question
                SET wrong_answer = #{wrongAnswer},
                    correct_answer = #{correctAnswer},
                    wrong_count = wrong_count + 1,
                    last_wrong_time = CURRENT_TIMESTAMP
                WHERE student_id = #{studentId} AND question_id = #{questionId}
            """)
    void updateWrongQuestion(
            @Param("studentId") Long studentId,
            @Param("questionId") Long questionId,
            @Param("wrongAnswer") String wrongAnswer,
            @Param("correctAnswer") String correctAnswer);

    @Select("""
            SELECT
                q.id,
                q.content,
                q.type,
                q.options,
                q.answer,
                q.course_id,
                q.section_id,
                wq.wrong_answer,
                wq.last_wrong_time,
                wq.wrong_count
            FROM wrong_question wq
            JOIN question q ON wq.question_id = q.id
            WHERE wq.student_id = #{studentId}
            ORDER BY wq.last_wrong_time DESC
            """)
    List<Map<String, Object>> findWrongQuestions(@Param("studentId") Long studentId);

    @Delete("DELETE FROM wrong_question WHERE student_id = #{studentId} AND question_id = #{questionId}")
    void deleteWrongQuestion(@Param("studentId") Long studentId, @Param("questionId") Long questionId);
}
