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
                wq.wrong_answer,
                wq.last_wrong_time,
                c.name as course_name,
                cs.title as section_title,
                p.title as practice_title
            FROM wrong_question wq
            JOIN question q ON wq.question_id = q.id
            LEFT JOIN course c ON q.course_id = c.id
            LEFT JOIN coursesection cs ON q.section_id = cs.id
            LEFT JOIN practicequestion pq ON q.id = pq.question_id
            LEFT JOIN practice p ON pq.practice_id = p.id
            WHERE wq.student_id = #{studentId}
            ORDER BY wq.last_wrong_time DESC
            """)
    List<Map<String, Object>> findWrongQuestions(@Param("studentId") Long studentId);

    @Delete("DELETE FROM wrong_question WHERE student_id = #{studentId} AND question_id = #{questionId}")
    void deleteWrongQuestion(@Param("studentId") Long studentId, @Param("questionId") Long questionId);
}
