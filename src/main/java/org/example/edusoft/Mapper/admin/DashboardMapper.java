package org.example.edusoft.mapper.admin;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

/**
 * DashboardMapper
 *
 * 提供管理员大屏所需的各类统计数据聚合查询。
 * 所有查询均按照日期区间进行统计，便于按日 / 周等维度在服务层组合。
 */
@Mapper
public interface DashboardMapper {

    /* ========================= 教师侧 ========================= */

    /** 上传课件 / 视频数量 */
    @Select("""
            SELECT COUNT(*) FROM file_node f
            JOIN user u ON f.uploader_id = u.id
            WHERE u.role = 'teacher' AND DATE(f.created_at) BETWEEN #{start} AND #{end}
            """)
    int countTeacherUploadResource(@Param("start") LocalDate start,
                                   @Param("end") LocalDate end);

    /** 创建作业数量 */
    @Select("""
            SELECT COUNT(*) FROM homework h
            JOIN user u ON h.created_by = u.id
            WHERE u.role = 'teacher' AND DATE(h.created_at) BETWEEN #{start} AND #{end}
            """)
    int countTeacherCreateHomework(@Param("start") LocalDate start,
                                   @Param("end") LocalDate end);

    /** 创建练习数量 */
    @Select("""
            SELECT COUNT(*) FROM practice p
            JOIN user u ON p.created_by = u.id
            WHERE u.role = 'teacher' AND DATE(p.created_at) BETWEEN #{start} AND #{end}
            """)
    int countTeacherCreatePractice(@Param("start") LocalDate start,
                                   @Param("end") LocalDate end);

    /** 生成 / 创建题目量 */
    @Select("""
            SELECT COUNT(*) FROM question q
            JOIN user u ON q.creator_id = u.id
            WHERE u.role = 'teacher' AND DATE(q.created_at) BETWEEN #{start} AND #{end}
            """)
    int countTeacherCreateQuestion(@Param("start") LocalDate start,
                                   @Param("end") LocalDate end);

    /** 批改练习次数（以已判题提交数量估算） */
    @Select("""
            SELECT COUNT(*) FROM submission s
            WHERE s.is_judged = 1 AND DATE(s.submitted_at) BETWEEN #{start} AND #{end}
            """)
    int countTeacherCorrectPractice(@Param("start") LocalDate start,
                                    @Param("end") LocalDate end);

    /* ========================= 学生侧 ========================= */

    /** 下载资源 / 观看视频次数（以 learning_progress 更新计） */
    @Select("""
            SELECT COUNT(*) FROM learning_progress lp
            JOIN user u ON lp.student_id = u.id
            WHERE u.role = 'student' AND DATE(lp.updated_at) BETWEEN #{start} AND #{end}
            """)
    int countStudentDownloadResource(@Param("start") LocalDate start,
                                     @Param("end") LocalDate end);

    /** 提交作业数量 */
    @Select("""
            SELECT COUNT(*) FROM homeworksubmission hs
            JOIN user u ON hs.student_id = u.id
            WHERE u.role = 'student' AND DATE(hs.submitted_at) BETWEEN #{start} AND #{end}
            """)
    int countStudentSubmitHomework(@Param("start") LocalDate start,
                                   @Param("end") LocalDate end);

    /** 提交练习数量 */
    @Select("""
            SELECT COUNT(*) FROM submission s
            JOIN user u ON s.student_id = u.id
            WHERE u.role = 'student' AND DATE(s.submitted_at) BETWEEN #{start} AND #{end}
            """)
    int countStudentSubmitPractice(@Param("start") LocalDate start,
                                   @Param("end") LocalDate end);

    /** 在线学习助手提问量（以 ai_service 日志表或后续埋点，如果暂无则返回 0） */
    @Select("SELECT 0")
    int countStudentAssistantQuestions(@Param("start") LocalDate start,
                                       @Param("end") LocalDate end);

    /* ========================= 教学效率指数 ========================= */

    /**
     * 获取特定时间段内，某个AI接口的平均调用耗时。
     *
     * @param endpoint AI接口路径，如 /rag/generate
     * @param start 开始日期
     * @param end 结束日期
     * @return 平均耗时 (ms)
     */
    @Select("""
            SELECT IFNULL(AVG(duration_ms), 0.0) FROM aiservicecalllog
            WHERE endpoint = #{endpoint} AND DATE(call_time) BETWEEN #{start} AND #{end}
            """)
    Double getAiServiceAverageDuration(@Param("endpoint") String endpoint,
                                       @Param("start") LocalDate start,
                                       @Param("end") LocalDate end);

    /**
     * 获取特定时间段内，某个AI接口的调用次数。
     *
     * @param endpoint AI接口路径，如 /rag/generate
     * @param start 开始日期
     * @param end 结束日期
     * @return 调用次数
     */
    @Select("""
            SELECT COUNT(*) FROM aiservicecalllog
            WHERE endpoint = #{endpoint} AND DATE(call_time) BETWEEN #{start} AND #{end}
            """)
    int getAiServiceCallCount(@Param("endpoint") String endpoint,
                              @Param("start") LocalDate start,
                              @Param("end") LocalDate end);

    /* ========================= 学生学习效果 ========================= */

    /**
     * 获取指定时间段内，每个课程-章节的平均练习正确率。
     * 返回结果为 { course_id, section_id, average_score }
     */
    @Select("""
            SELECT
                q.course_id, q.section_id, AVG(a.score) as average_score
            FROM answer a
            JOIN submission s ON a.submission_id = s.id
            JOIN question q ON a.question_id = q.id
            WHERE DATE(s.submitted_at) BETWEEN #{start} AND #{end}
            GROUP BY q.course_id, q.section_id
            """)
    List<Map<String, Object>> getAverageCorrectnessBySection(@Param("start") LocalDate start,
                                                             @Param("end") LocalDate end);

    /**
     * 获取指定时间段内，高频错误知识点。
     * 返回结果为 { course_id, section_id, question_id, wrong_count, content }
     */
    @Select("""
            SELECT
                wq.question_id, COUNT(wq.id) as wrong_count, q.course_id, q.section_id, q.content
            FROM wrongquestion wq
            JOIN question q ON wq.question_id = q.id
            WHERE DATE(wq.last_wrong_time) BETWEEN #{start} AND #{end}
            GROUP BY wq.question_id, q.course_id, q.section_id, q.content
            ORDER BY wrong_count DESC
            LIMIT 10
            """)
    List<Map<String, Object>> getTopWrongKnowledgePoints(@Param("start") LocalDate start,
                                                         @Param("end") LocalDate end);

    /**
     * 获取所有课程及章节信息（用于前端展示，如果需要关联知识点名称）
     */
    @Select("""
            SELECT c.id as course_id, c.name as course_name, cs.id as section_id, cs.title as section_name
            FROM course c
            JOIN coursesection cs ON c.id = cs.course_id
            """)
    List<Map<String, Object>> getAllCoursesAndSections();

    /**
     * 获取所有班级ID和名称
     */
    @Select("SELECT id as class_id, name as class_name FROM class")
    List<Map<String, Object>> getAllClasses();

    /**
     * 获取指定时间段内，每个班级-课程-章节的平均分和通过率。
     * 返回结果为 { class_id, course_id, section_id, average_score, pass_rate }
     */
    @Select("""
            SELECT
                p.class_id,
                q.course_id,
                q.section_id,
                AVG(s.score) as average_score,
                SUM(CASE WHEN s.score >= 60 THEN 1 ELSE 0 END) * 1.0 / COUNT(s.id) as pass_rate
            FROM submission s
            JOIN practice p ON s.practice_id = p.id
            JOIN practicequestion pq ON pq.practice_id = p.id
            JOIN question q ON pq.question_id = q.id
            WHERE DATE(s.submitted_at) BETWEEN #{start} AND #{end}
            GROUP BY p.class_id, q.course_id, q.section_id
            """)
    List<Map<String, Object>> getClassCourseSectionStats(@Param("start") LocalDate start,
                                                        @Param("end") LocalDate end);

    @Select("""
        SELECT 
            q.course_id,
            q.section_id,
            AVG(a.score) as average_score,
            COUNT(CASE WHEN a.correct = 0 THEN 1 END) * 100.0 / COUNT(*) as error_rate,
            COUNT(DISTINCT s.student_id) as student_count
        FROM question q
        JOIN practicequestion pq ON q.id = pq.question_id
        JOIN practice p ON pq.practice_id = p.id
        JOIN submission s ON p.id = s.practice_id
        JOIN answer a ON s.id = a.submission_id AND q.id = a.question_id
        WHERE q.course_id = #{courseId} AND q.section_id = #{sectionId}
        GROUP BY q.course_id, q.section_id
    """)
    Map<String, Object> getCourseStatistics(Long courseId, Long sectionId);

    @Select("""
        SELECT 
            q.content as question_content,
            COUNT(CASE WHEN a.correct = 0 THEN 1 END) as wrong_count,
            COUNT(*) as total_attempts
        FROM question q
        JOIN practicequestion pq ON q.id = pq.question_id
        JOIN practice p ON pq.practice_id = p.id
        JOIN submission s ON p.id = s.practice_id
        JOIN answer a ON s.id = a.submission_id AND q.id = a.question_id
        WHERE q.course_id = #{courseId} AND q.section_id = #{sectionId}
        GROUP BY q.id, q.content
        ORDER BY wrong_count DESC
        LIMIT 5
    """)
    List<Map<String, Object>> getTopWrongQuestions(Long courseId, Long sectionId);
}