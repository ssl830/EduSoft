package org.example.edusoft.mapper.record;

import org.apache.ibatis.annotations.Many;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Result;
import org.apache.ibatis.annotations.Results;

import java.util.List;
import java.util.Map;
import org.example.edusoft.entity.record.*;

@Mapper
public interface PracticeRecordMapper {
    @Select("""
                SELECT
                    s.id,
                    s.practice_id,
                    s.student_id,
                    s.submitted_at,
                    s.score,
                    s.feedback,
                    p.title as practice_title,
                    c.name as course_name,
                    (
                      SELECT GROUP_CONCAT(DISTINCT cl.name)
                      FROM classuser cu
                      JOIN class cl ON cu.class_id = cl.id
                      WHERE cu.user_id = s.student_id AND cl.course_id = c.id
                    ) as class_name
                FROM submission s
                JOIN practice p ON s.practice_id = p.id
                JOIN course c ON p.course_id = c.id
                WHERE s.student_id = #{studentId}
                GROUP BY s.id
                ORDER BY s.submitted_at DESC
            """)
    @Results({
            @Result(property = "id", column = "id"),
            @Result(property = "practiceId", column = "practice_id"),
            @Result(property = "practiceTitle", column = "practice_title"),
            @Result(property = "courseName", column = "course_name"),
            @Result(property = "className", column = "class_name"),
            @Result(property = "submittedAt", column = "submitted_at"),
            @Result(property = "questions", column = "id", many = @Many(select = "findQuestionsBySubmissionId"))
    })
    List<PracticeRecord> findPracticeRecords(@Param("studentId") Long studentId);

    @Select("""
                SELECT
                    s.id,
                    s.practice_id,
                    s.student_id,
                    s.submitted_at,
                    s.score,
                    s.feedback,
                    p.title as practice_title,
                    c.name as course_name,
                    (
                      SELECT GROUP_CONCAT(DISTINCT cl.name)
                      FROM classuser cu
                      JOIN class cl ON cu.class_id = cl.id
                      WHERE cu.user_id = s.student_id AND cl.course_id = c.id
                    ) as class_name
                FROM submission s
                JOIN practice p ON s.practice_id = p.id
                JOIN course c ON p.course_id = c.id
                WHERE s.student_id = #{studentId} and p.course_id=#{courseId}
                GROUP BY s.id
                ORDER BY s.submitted_at DESC
            """)
    @Results({
            @Result(property = "id", column = "id"),
            @Result(property = "practiceId", column = "practice_id"),
            @Result(property = "practiceTitle", column = "practice_title"),
            @Result(property = "courseName", column = "course_name"),
            @Result(property = "className", column = "class_name"),
            @Result(property = "submittedAt", column = "submitted_at"),
            @Result(property = "questions", column = "id", many = @Many(select = "findQuestionsBySubmissionId"))
    })
    List<PracticeRecord> findByStudentIdAndCourseId(@Param("studentId") Long studentId,
            @Param("courseId") Long courseId);

    @Select("""
                SELECT DISTINCT
                    q.id,
                    q.course_id as courseId,
                    q.section_id as sectionId,
                    q.content,
                    q.type,
                    q.options,
                    q.analysis,
                    a.answer_text as studentAnswer,
                    q.answer as correctAnswer,
                    a.correct as isCorrect,
                    a.score
                FROM answer a
                JOIN question q ON a.question_id = q.id
                WHERE a.submission_id = #{submissionId}
                ORDER BY q.id
            """)
    @Results({
            @Result(property = "id", column = "id"),
            @Result(property = "content", column = "content"),
            @Result(property = "courseId", column = "courseId"),
            @Result(property = "sectionId", column = "sectionId"),
            @Result(property = "type", column = "type"),
            @Result(property = "options", column = "options"),
            @Result(property = "studentAnswer", column = "studentAnswer"),
            @Result(property = "correctAnswer", column = "correctAnswer"),
            @Result(property = "analysis", column = "analysis"),
            @Result(property = "isCorrect", column = "isCorrect"),
            @Result(property = "score", column = "score")
    })
    List<QuestionRecord> findQuestionsBySubmissionId(@Param("submissionId") Long submissionId);

    @Select("""
                SELECT COUNT(DISTINCT student_id)
                FROM submission
                WHERE practice_id = #{practiceId}
            """)
    int getTotalStudentsInPractice(@Param("practiceId") Long practiceId);

    @Select("""
                SELECT DISTINCT
                    s.id,
                    s.practice_id,
                    s.student_id,
                    s.submitted_at,
                    s.score,
                    s.feedback,
                    p.title as practice_title,
                    c.name as course_name,
                    (SELECT GROUP_CONCAT(DISTINCT cl.name) 
                     FROM classuser cu 
                     JOIN class cl ON cu.class_id = cl.id 
                     WHERE cu.user_id = s.student_id) as class_name
                FROM submission s
                JOIN practice p ON s.practice_id = p.id
                JOIN course c ON p.course_id = c.id
                WHERE s.id = #{submissionId}
                AND s.student_id = #{studentId}
                LIMIT 1
            """)
    @Results({
            @Result(property = "id", column = "id"),
            @Result(property = "practiceId", column = "practice_id"),
            @Result(property = "practiceTitle", column = "practice_title"),
            @Result(property = "courseName", column = "course_name"),
            @Result(property = "className", column = "class_name"),
            @Result(property = "submittedAt", column = "submitted_at"),
            @Result(property = "score", column = "score"),
            @Result(property = "feedback", column = "feedback"),
            @Result(property = "questions", column = "id", many = @Many(select = "findQuestionsBySubmissionId"))
    })
    PracticeRecord findSubmissionDetail(
            @Param("submissionId") Long submissionId,
            @Param("studentId") Long studentId);

    @Select("""
                SELECT COUNT(*) + 1
                FROM submission s1
                WHERE s1.practice_id = #{practiceId}
                AND s1.score > (
                    SELECT score
                    FROM submission s2
                    WHERE s2.practice_id = #{practiceId}
                    AND s2.student_id = #{studentId}
                )
            """)
    int getPracticeRank(@Param("practiceId") Long practiceId,
            @Param("studentId") Long studentId);

    @Select("""
                SELECT
                    CASE
                        WHEN score >= 90 THEN '90-100'
                        WHEN score >= 80 THEN '80-89'
                        WHEN score >= 70 THEN '70-79'
                        WHEN score >= 60 THEN '60-69'
                        ELSE '0-59'
                    END as score_range,
                    COUNT(*) as count,
                    ROUND(COUNT(*) * 100.0 / SUM(COUNT(*)) OVER(), 2) as percentage
                FROM submission
                WHERE practice_id = #{practiceId}
                GROUP BY
                    CASE
                        WHEN score >= 90 THEN '90-100'
                        WHEN score >= 80 THEN '80-89'
                        WHEN score >= 70 THEN '70-79'
                        WHEN score >= 60 THEN '60-69'
                        ELSE '0-59'
                    END
                ORDER BY score_range DESC
            """)
    List<Map<String, Object>> getScoreDistribution(@Param("practiceId") Long practiceId);

    @Select("""
                SELECT
                    CASE
                        WHEN score >= 90 THEN '90-100'
                        WHEN score >= 80 THEN '80-89'
                        WHEN score >= 70 THEN '70-79'
                        WHEN score >= 60 THEN '60-69'
                        ELSE '0-59'
                    END as score_range,
                    COUNT(*) as count,
                    ROUND(COUNT(*) * 100.0 / SUM(COUNT(*)) OVER(), 2) as percentage
                FROM submission s
                JOIN classuser cu ON s.student_id = cu.user_id
                WHERE s.practice_id = #{practiceId}
                AND cu.class_id = (
                    SELECT cu2.class_id
                    FROM classuser cu2
                    WHERE cu2.user_id = #{studentId}
                    LIMIT 1
                )
                GROUP BY
                    CASE
                        WHEN score >= 90 THEN '90-100'
                        WHEN score >= 80 THEN '80-89'
                        WHEN score >= 70 THEN '70-79'
                        WHEN score >= 60 THEN '60-69'
                        ELSE '0-59'
                    END
                ORDER BY score_range DESC
            """)
    List<Map<String, Object>> getClassScoreDistribution(
            @Param("practiceId") Long practiceId,
            @Param("studentId") Long studentId);

    @Select("""
                SELECT
                    AVG(score) as average_score,
                    MAX(score) as highest_score,
                    MIN(score) as lowest_score,
                    COUNT(*) as total_submissions
                FROM practice_submission
                WHERE practice_id = #{practiceId}
                AND class_id = (
                    SELECT class_id
                    FROM practice_submission
                    WHERE practice_id = #{practiceId}
                    AND student_id = #{studentId}
                    LIMIT 1
                )
            """)
    Map<String, Object> getPracticeStats(@Param("practiceId") Long practiceId,
            @Param("studentId") Long studentId);

    @Select("""
                SELECT DISTINCT
                    q.id,
                    q.content,
                    q.type,
                    q.options,
                    q.answer as correct_answer,
                    q.analysis,
                    a.answer_text as student_answer,
                    a.correct as is_correct,
                    a.score
                FROM answer a
                JOIN question q ON a.question_id = q.id
                WHERE a.submission_id = #{submissionId}
                ORDER BY q.id
            """)
    @Results({
            @Result(property = "id", column = "id"),
            @Result(property = "content", column = "content"),
            @Result(property = "type", column = "type"),
            @Result(property = "options", column = "options"),
            @Result(property = "correctAnswer", column = "correct_answer"),
            @Result(property = "analysis", column = "analysis"),
            @Result(property = "studentAnswer", column = "student_answer"),
            @Result(property = "isCorrect", column = "is_correct"),
            @Result(property = "score", column = "score")
    })
    List<QuestionRecord> findSubmissionQuestions(@Param("submissionId") Long submissionId);

    @Select("""
                SELECT COUNT(*) + 1
                FROM submission s1
                WHERE s1.practice_id = (
                    SELECT practice_id FROM submission WHERE id = #{submissionId}
                )
                AND s1.score > (
                    SELECT score FROM submission WHERE id = #{submissionId}
                )
            """)
    int getSubmissionRank(
            @Param("submissionId") Long submissionId,
            @Param("studentId") Long studentId);

    @Select("""
                SELECT
                    CASE
                        WHEN score >= 90 THEN '90-100'
                        WHEN score >= 80 THEN '80-89'
                        WHEN score >= 70 THEN '70-79'
                        WHEN score >= 60 THEN '60-69'
                        ELSE '0-59'
                    END as score_range,
                    COUNT(*) as count,
                    ROUND(COUNT(*) * 100.0 / SUM(COUNT(*)) OVER(), 2) as percentage
                FROM submission
                WHERE practice_id = (
                    SELECT practice_id FROM submission WHERE id = #{submissionId}
                )
                GROUP BY
                    CASE
                        WHEN score >= 90 THEN '90-100'
                        WHEN score >= 80 THEN '80-89'
                        WHEN score >= 70 THEN '70-79'
                        WHEN score >= 60 THEN '60-69'
                        ELSE '0-59'
                    END
                ORDER BY score_range DESC
            """)
    List<Map<String, Object>> getSubmissionScoreDistribution(@Param("submissionId") Long submissionId);

    @Select("""
        SELECT COUNT(*) as total_submissions, AVG(score) as average_score
        FROM submission
        WHERE practice_id = #{practiceId}
    """)
    Map<String, Object> getSubmissionStatsByPracticeId(@Param("practiceId") Long practiceId);
}