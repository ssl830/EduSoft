package org.example.edusoft.learning.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Param;
import java.util.List;
import org.example.edusoft.learning.entity.StudyRecord;

@Mapper
public interface RecordMapper {
    // 学习记录相关
    @Select({
        "SELECT ",
        "lp.id, lp.resource_id, lp.student_id, lp.progress, lp.last_position, lp.watch_count, ",
        "lp.last_watch_time, lp.created_at, lp.updated_at, ",
        "tr.title as resource_title, c.name as course_name, cs.title as section_title ",
        "FROM learning_progress lp ",
        "LEFT JOIN teaching_resource tr ON lp.resource_id = tr.id ",
        "LEFT JOIN coursesection cs ON tr.chapter_id = cs.id ",
        "LEFT JOIN course c ON cs.course_id = c.id ",
        "WHERE lp.student_id = #{studentId} ",
        "ORDER BY lp.last_watch_time DESC"
    })
    List<StudyRecord> findStudyRecords(@Param("studentId") Long studentId);

    @Select({
        "SELECT ",
        "lp.id, lp.resource_id, lp.student_id, lp.progress, lp.last_position, lp.watch_count, ",
        "lp.last_watch_time, lp.created_at, lp.updated_at, ",
        "tr.title as resource_title, c.name as course_name, cs.title as section_title ",
        "FROM learning_progress lp ",
        "LEFT JOIN teaching_resource tr ON lp.resource_id = tr.id ",
        "LEFT JOIN coursesection cs ON tr.chapter_id = cs.id ",
        "LEFT JOIN course c ON cs.course_id = c.id ",
        "WHERE lp.student_id = #{studentId} AND c.id = #{courseId} ",
        "ORDER BY lp.last_watch_time DESC"
    })
    List<StudyRecord> findByStudentIdAndCourseId(@Param("studentId") Long studentId, @Param("courseId") Long courseId);

    // 排名相关
    @Select({
        "SELECT COUNT(*) as rank ",
        "FROM (",
        "   SELECT student_id, SUM(score) as total_score ",
        "   FROM submission ",
        "   WHERE practice_id = #{practiceId} ",
        "   GROUP BY student_id ",
        "   HAVING SUM(score) > (",
        "       SELECT SUM(score) ",
        "       FROM submission ",
        "       WHERE practice_id = #{practiceId} ",
        "       AND student_id = #{studentId}",
        "   )",
        ") t"
    })
    int getPracticeRank(@Param("practiceId") Long practiceId, @Param("studentId") Long studentId);

    // 学习记录（冗余，建议只保留一个实现）
    @Select({
        "SELECT ",
        "lp.id, lp.resource_id, lp.student_id, lp.progress, lp.last_position, lp.watch_count, ",
        "lp.last_watch_time, lp.created_at, lp.updated_at, ",
        "tr.title as resource_title, c.name as course_name, cs.title as section_title ",
        "FROM learning_progress lp ",
        "LEFT JOIN teaching_resource tr ON lp.resource_id = tr.id ",
        "LEFT JOIN coursesection cs ON tr.chapter_id = cs.id ",
        "LEFT JOIN course c ON cs.course_id = c.id ",
        "WHERE lp.student_id = #{studentId} ",
        "ORDER BY lp.last_watch_time DESC"
    })
    List<StudyRecord> getStudyRecordsByStudentId(@Param("studentId") Long studentId);

    @Select({
        "SELECT ",
        "lp.id, lp.resource_id, lp.student_id, lp.progress, lp.last_position, lp.watch_count, ",
        "lp.last_watch_time, lp.created_at, lp.updated_at, ",
        "tr.title as resource_title, c.name as course_name, cs.title as section_title ",
        "FROM learning_progress lp ",
        "LEFT JOIN teaching_resource tr ON lp.resource_id = tr.id ",
        "LEFT JOIN coursesection cs ON tr.chapter_id = cs.id ",
        "LEFT JOIN course c ON cs.course_id = c.id ",
        "WHERE lp.student_id = #{studentId} AND c.id = #{courseId} ",
        "ORDER BY lp.last_watch_time DESC"
    })
    List<StudyRecord> getStudyRecordsByStudentAndCourse(@Param("studentId") Long studentId, @Param("courseId") Long courseId);

    // 补充：练习记录相关
    @Select({
        "SELECT ",
        "s.id, s.practice_id, s.student_id, s.score, s.submitted_at, ",
        "p.title as practice_title, c.name as course_name ",
        "FROM submission s ",
        "LEFT JOIN practice p ON s.practice_id = p.id ",
        "LEFT JOIN course c ON p.course_id = c.id ",
        "WHERE s.student_id = #{studentId} ",
        "ORDER BY s.submitted_at DESC"
    })
    List<StudyRecord> getPracticeRecordsByStudentId(@Param("studentId") Long studentId);

    @Select({
        "SELECT ",
        "s.id, s.practice_id, s.student_id, s.score, s.submitted_at, ",
        "p.title as practice_title, c.name as course_name ",
        "FROM submission s ",
        "LEFT JOIN practice p ON s.practice_id = p.id ",
        "LEFT JOIN course c ON p.course_id = c.id ",
        "WHERE s.student_id = #{studentId} AND c.id = #{courseId} ",
        "ORDER BY s.submitted_at DESC"
    })
    List<StudyRecord> getPracticeRecordsByStudentAndCourse(@Param("studentId") Long studentId, @Param("courseId") Long courseId);
}
