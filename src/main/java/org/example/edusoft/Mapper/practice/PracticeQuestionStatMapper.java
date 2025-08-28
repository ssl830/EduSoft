package org.example.edusoft.mapper.practice;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import java.util.List;
import java.util.Map;

@Mapper
public interface PracticeQuestionStatMapper {
    /**
     * 查询某练习下所有题目的统计信息（题干、题型、分值、答题人数、正确人数、得分率）
     */
    @Select("SELECT pq.question_id, q.content, q.type, pq.score, pq.score_rate, " +
            "(SELECT COUNT(DISTINCT s.student_id) FROM submission s JOIN answer a ON a.submission_id = s.id WHERE s.practice_id = pq.practice_id AND a.question_id = pq.question_id) AS student_count, " +
            "(SELECT COUNT(DISTINCT s.student_id) FROM submission s JOIN answer a ON a.submission_id = s.id WHERE s.practice_id = pq.practice_id AND a.question_id = pq.question_id AND a.correct = TRUE) AS correct_count " +
            "FROM practicequestion pq " +
            "JOIN question q ON pq.question_id = q.id " +
            "WHERE pq.practice_id = #{practiceId}")
    List<Map<String, Object>> getPracticeQuestionStats(Long practiceId);
}
