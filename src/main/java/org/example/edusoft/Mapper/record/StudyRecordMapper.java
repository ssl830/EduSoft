package org.example.edusoft.mapper.record;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Param;
import java.util.List;
import org.example.edusoft.entity.record.StudyRecord; // Make sure this path matches your actual StudyRecord location

@Mapper
public interface StudyRecordMapper {
    @Select("""
                SELECT 
                    lp.id,
                    lp.resource_id,
                    lp.student_id,
                    lp.progress,
                    lp.last_position,
                    lp.watch_count,
                    lp.last_watch_time,
                    lp.created_at,
                    lp.updated_at,
                    tr.title as resource_title,
                    c.name as course_name,
                    cs.title as section_title                FROM learning_progress lp
                LEFT JOIN teaching_resource tr ON lp.resource_id = tr.id
                LEFT JOIN coursesection cs ON tr.chapter_id = cs.id
                LEFT JOIN course c ON cs.course_id = c.id
                WHERE lp.student_id = #{studentId}
                ORDER BY lp.last_watch_time DESC
            """)
    List<StudyRecord> findStudyRecords(@Param("studentId") Long studentId);

    @Select("""
                SELECT 
                    lp.id,
                    lp.resource_id,
                    lp.student_id,
                    lp.progress,
                    lp.last_position,
                    lp.watch_count,
                    lp.last_watch_time,
                    lp.created_at,
                    lp.updated_at,
                    tr.title as resource_title,
                    c.name as course_name,
                    cs.title as section_title
                FROM learning_progress lp
                LEFT JOIN teaching_resource tr ON lp.resource_id = tr.id
                LEFT JOIN coursesection cs ON tr.chapter_id = cs.id
                LEFT JOIN course c ON cs.course_id = c.id
                WHERE lp.student_id = #{studentId} AND c.id = #{courseId}
                ORDER BY lp.last_watch_time DESC
            """)
    List<StudyRecord> findByStudentIdAndCourseId(@Param("studentId") Long studentId, @Param("courseId") Long courseId);

    @Select("""
                SELECT COUNT(*) as rank
                FROM (
                    SELECT student_id, SUM(score) as total_score
                    FROM submission
                    WHERE practice_id = #{practiceId}
                    GROUP BY student_id
                    HAVING SUM(score) > (
                        SELECT SUM(score)
                        FROM submission
                        WHERE practice_id = #{practiceId}
                        AND student_id = #{studentId}
                    )
                ) t
            """)
    int getPracticeRank(@Param("practiceId") Long practiceId, @Param("studentId") Long studentId);
}
