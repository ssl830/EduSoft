package org.example.edusoft.mapper.practice;

import org.apache.ibatis.annotations.*;
import org.example.edusoft.entity.practice.Practice;
import org.example.edusoft.entity.practice.PracticeListDTO;
import java.util.List;
import java.util.Map;

@Mapper
public interface PracticeMapper {

    /**
     * 查找所有已截止的练习ID（end_time早于当前时间）
     */
    @Select("SELECT id FROM practice WHERE end_time < #{now}")
    List<Long> findAllEndedPracticeIds(@Param("now") java.time.LocalDateTime now);
    
    @Insert("INSERT INTO practice (course_id, class_id, title, start_time, end_time, allow_multiple_submission, created_by) " +
            "VALUES (#{courseId}, #{classId}, #{title}, #{startTime}, #{endTime}, #{allowMultipleSubmission}, #{createdBy})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    void createPractice(Practice practice);

    @Update("UPDATE practice SET title = #{title}, start_time = #{startTime}, end_time = #{endTime}, " +
            "allow_multiple_submission = #{allowMultipleSubmission} WHERE id = #{id}")
    void updatePractice(Practice practice);

    @Select("SELECT * FROM practice WHERE class_id = #{classId} ORDER BY created_at DESC")
    List<Practice> getPracticeList(@Param("classId") Long classId);

    @Select("SELECT * FROM practice WHERE id = #{id}")
    Practice getPracticeById(Long id);

    @Delete("DELETE FROM practice WHERE id = #{id}")
    void deletePractice(Long id);

    @Select("SELECT COUNT(*) FROM practice WHERE course_id = #{courseId} AND class_id = #{classId}")
    int getPracticeCount(@Param("courseId") Long courseId, @Param("classId") Long classId);

    @Select("SELECT user_id FROM classuser WHERE class_id = #{classId}")
    List<Long> getClassStudentIds(Long classId);


    // 检查题目是否已收藏
    @Select("SELECT COUNT(*) FROM favoritequestion WHERE student_id = #{studentId} AND question_id = #{questionId}")
    boolean isQuestionFavorited(@Param("studentId") Long studentId, @Param("questionId") Long questionId);

    // 添加收藏题目
    @Insert("INSERT INTO favoritequestion (student_id, question_id) VALUES (#{studentId}, #{questionId})")
    void insertFavoriteQuestion(@Param("studentId") Long studentId, @Param("questionId") Long questionId);

    // 删除收藏题目
    @Delete("DELETE FROM favoritequestion WHERE student_id = #{studentId} AND question_id = #{questionId}")
    void deleteFavoriteQuestion(@Param("studentId") Long studentId, @Param("questionId") Long questionId);

    // 获取收藏的题目列表
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
                FROM favoritequestion fq
                JOIN question q ON fq.question_id = q.id
                LEFT JOIN course c ON q.course_id = c.id
                LEFT JOIN coursesection cs ON q.section_id = cs.id
                LEFT JOIN practicequestion pq ON q.id = pq.question_id
                LEFT JOIN practice p ON pq.practice_id = p.id
                WHERE fq.student_id = #{studentId}
                ORDER BY q.created_at DESC
            """)
    List<Map<String, Object>> findFavoriteQuestions(@Param("studentId") Long studentId);

    // 检查错题是否存在
    @Select("SELECT COUNT(*) FROM wrongquestion WHERE student_id = #{studentId} AND question_id = #{questionId}")
    boolean existsWrongQuestion(@Param("studentId") Long studentId, @Param("questionId") Long questionId);

    // 添加错题
    @Insert("""
                INSERT INTO wrongquestion
                (student_id, question_id, wrong_answer, correct_answer)
                VALUES
                (#{studentId}, #{questionId}, #{wrongAnswer}, #{correctAnswer})
            """)
    void insertWrongQuestion(
            @Param("studentId") Long studentId,
            @Param("questionId") Long questionId,
            @Param("wrongAnswer") String wrongAnswer,
            @Param("correctAnswer") String correctAnswer);

    // 更新错题
    @Update("""
                UPDATE wrongquestion
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

    // 删除错题
    @Delete("DELETE FROM wrongquestion WHERE student_id = #{studentId} AND question_id = #{questionId}")
    void deleteWrongQuestion(@Param("studentId") Long studentId, @Param("questionId") Long questionId);

    // 获取错题列表
    @Select("""
                SELECT
                    wq.id,
                    wq.question_id,
                    wq.wrong_answer,
                    wq.correct_answer,
                    wq.wrong_count,
                    wq.last_wrong_time,
                    q.content,
                    q.type,
                    q.options,
                    c.name as course_name,
                    cs.title as section_title,
                    p.title as practice_title
                FROM wrongquestion wq
                JOIN question q ON wq.question_id = q.id
                LEFT JOIN course c ON q.course_id = c.id
                LEFT JOIN coursesection cs ON q.section_id = cs.id
                LEFT JOIN practicequestion pq ON q.id = pq.question_id
                LEFT JOIN practice p ON pq.practice_id = p.id
                WHERE wq.student_id = #{studentId}
                ORDER BY wq.last_wrong_time DESC
            """)
    List<Map<String, Object>> findWrongQuestions(@Param("studentId") Long studentId);

    // 获取某个课程的错题列表
    @Select("""
                SELECT
                    wq.id,
                    wq.wrong_answer,
                    wq.correct_answer,
                    wq.wrong_count,
                    wq.last_wrong_time,
                    q.content,
                    q.type,
                    q.options,
                    c.name as course_name,
                    cs.title as section_title,
                    p.title as practice_title
                FROM wrongquestion wq
                JOIN question q ON wq.question_id = q.id
                LEFT JOIN course c ON q.course_id = c.id
                LEFT JOIN coursesection cs ON q.section_id = cs.id
                LEFT JOIN practicequestion pq ON q.id = pq.question_id
                LEFT JOIN practice p ON pq.practice_id = p.id
                WHERE wq.student_id = #{studentId}
                AND q.course_id = #{courseId}
                ORDER BY wq.last_wrong_time DESC
            """)
    List<Map<String, Object>> findWrongQuestionsByCourse(
            @Param("studentId") Long studentId,
            @Param("courseId") Long courseId);

    @Select("""
                SELECT cu.class_id
                FROM classuser cu
                JOIN courseclass cc ON cu.class_id = cc.class_id
                WHERE cu.user_id = #{userId}
                AND cc.course_id = #{courseId}
            """)
    Long findClassIdByUserAndCourse(
            @Param("userId") Long userId,
            @Param("courseId") Long courseId);

    @Select("""
                SELECT
                    p.id,
                    p.title,
                    p.start_time,
                    p.end_time,
                    p.allow_multiple_submission,
                    p.created_at,
                    (
                        SELECT cs.id
                        FROM practicequestion pq
                        JOIN question q ON pq.question_id = q.id
                        JOIN coursesection cs ON q.section_id = cs.id
                        WHERE pq.practice_id = p.id
                        LIMIT 1
                    ) as section_id,
                    (
                        SELECT cs.title
                        FROM practicequestion pq
                        JOIN question q ON pq.question_id = q.id
                        JOIN coursesection cs ON q.section_id = cs.id
                        WHERE pq.practice_id = p.id
                        LIMIT 1
                    ) as section_title,
                    (
                        SELECT COUNT(*)
                        FROM practicequestion pq
                        WHERE pq.practice_id = p.id
                    ) as question_count,
                    (
                        SELECT COUNT(*)
                        FROM submission s
                        WHERE s.practice_id = p.id
                        AND s.student_id = #{studentId}
                    ) as submission_count,
                    (
                        SELECT s.score
                        FROM submission s
                        WHERE s.practice_id = p.id
                        AND s.student_id = #{studentId}
                        ORDER BY s.submitted_at DESC
                        LIMIT 1
                    ) as last_score
                FROM practice p
                WHERE p.course_id = #{courseId}
                AND p.class_id = #{classId}
                ORDER BY p.created_at DESC
            """)
    List<Map<String, Object>> findCoursePractices(
            @Param("courseId") Long courseId,
            @Param("classId") Long classId,
            @Param("studentId") Long studentId);

    @Select("""
            SELECT 
                p.*,
                CASE WHEN s.id IS NOT NULL THEN TRUE ELSE FALSE END as isCompleted,
                (SELECT COUNT(*) FROM submission s2 WHERE s2.practice_id = p.id AND s2.student_id = #{studentId}) as submissionCount,
                (SELECT MAX(score) FROM submission s3 WHERE s3.practice_id = p.id AND s3.student_id = #{studentId}) as score
            FROM practice p
            LEFT JOIN submission s ON p.id = s.practice_id AND s.student_id = #{studentId}
            WHERE p.class_id = #{classId}
            ORDER BY p.created_at DESC
            """)
    List<PracticeListDTO> getStudentPracticeList(Long studentId, Long classId);
    @Select("SELECT * FROM practice WHERE id = #{id}")
    Practice findById(Long id);

    @Select("SELECT * FROM practice WHERE class_id = #{classId}")
    List<Practice> findByClassId(Long classId);

    @Insert("INSERT INTO practice(course_id, class_id, title, start_time, end_time, allow_multiple_submission, created_by) " +
            "VALUES(#{courseId}, #{classId}, #{title}, #{startTime}, #{endTime}, #{allowMultipleSubmission}, #{createdBy})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    void insert(Practice practice);

    @Update("UPDATE practice SET title=#{title}, start_time=#{startTime}, end_time=#{endTime}, " +
            "allow_multiple_submission=#{allowMultipleSubmission} WHERE id=#{id}")
    void update(Practice practice);

    @Select("SELECT p.id, p.title, p.start_time, p.end_time, c.name AS course_name, p.class_id " +
            "FROM practice p " +
            "JOIN course c ON p.course_id = c.id " +
            "WHERE p.created_by = #{teacherId} ORDER BY p.created_at DESC")
    List<Map<String, Object>> getPracticesByTeacherId(@Param("teacherId") Long teacherId);
}