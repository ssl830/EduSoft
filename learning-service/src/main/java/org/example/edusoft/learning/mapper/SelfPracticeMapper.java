package org.example.edusoft.learning.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.example.edusoft.learning.entity.SelfPractice;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

@Mapper
public interface SelfPracticeMapper extends BaseMapper<SelfPractice> {
    @Select("""
        SELECT
            sp.id,
            sp.title,
            sp.created_at,
            ss.id AS submission_id,
            ss.score,
            ss.submitted_at,
            ss.is_judged
        FROM self_practice sp
        LEFT JOIN self_submission ss ON sp.id = ss.self_practice_id AND ss.student_id = #{stuId}
        WHERE sp.student_id = #{stuId}
        ORDER BY sp.created_at DESC
    """)
    List<Map<String, Object>> getHistory(@Param("stuId") Long stuId);

    @Select("""
        SELECT
            sp.id AS practice_id,
            sp.title,
            sp.created_at,
            ss.id AS submission_id,
            ss.score,
            ss.submitted_at,
            ss.is_judged,
            sa.id AS answer_id,
            sa.question_id,
            sa.answer_text,
            sa.is_judged AS answer_judged,
            sa.correct,
            sa.score AS answer_score,
            sa.sort_order
        FROM self_practice sp
        LEFT JOIN self_submission ss ON sp.id = ss.self_practice_id AND ss.student_id = #{stuId}
        LEFT JOIN self_answer sa ON ss.id = sa.submission_id
        WHERE sp.id = #{practiceId} AND sp.student_id = #{stuId}
        ORDER BY sa.sort_order ASC
    """)
    List<Map<String, Object>> getDetail(@Param("stuId") Long stuId, @Param("practiceId") Long practiceId);

    @Select("SELECT COUNT(*) FROM self_practice WHERE id = #{practiceId}")
    int checkPracticeExists(@Param("practiceId") Long practiceId);
}
