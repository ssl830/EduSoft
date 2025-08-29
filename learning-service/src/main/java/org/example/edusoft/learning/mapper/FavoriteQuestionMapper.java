package org.example.edusoft.learning.mapper;

import org.apache.ibatis.annotations.*;
import java.util.List;
import java.util.Map;

@Mapper
public interface FavoriteQuestionMapper {
    @Select("SELECT COUNT(*) FROM favorite_question WHERE student_id = #{studentId} AND question_id = #{questionId}")
    boolean isQuestionFavorited(@Param("studentId") Long studentId, @Param("questionId") Long questionId);

    @Insert("INSERT INTO favorite_question (student_id, question_id) VALUES (#{studentId}, #{questionId})")
    void insertFavoriteQuestion(@Param("studentId") Long studentId, @Param("questionId") Long questionId);

    @Delete("DELETE FROM favorite_question WHERE student_id = #{studentId} AND question_id = #{questionId}")
    void deleteFavoriteQuestion(@Param("studentId") Long studentId, @Param("questionId") Long questionId);

    @Select("""
                SELECT
                    q.id,
                    q.content,
                    q.type,
                    q.options,
                    q.answer,
                    c.name as course_name,
                    cs.title as section_title,
                    p.title as practice_title
                FROM favorite_question fq
                JOIN question q ON fq.question_id = q.id
                LEFT JOIN course c ON q.course_id = c.id
                LEFT JOIN coursesection cs ON q.section_id = cs.id
                LEFT JOIN practice_question pq ON q.id = pq.question_id
                LEFT JOIN practice p ON pq.practice_id = p.id
                WHERE fq.student_id = #{studentId}
                ORDER BY q.created_at DESC
            """)
    List<Map<String, Object>> findFavoriteQuestions(@Param("studentId") Long studentId);
}
