package org.example.edusoft.mapper.homework;

import org.apache.ibatis.annotations.*;
import org.example.edusoft.entity.homework.HomeworkSubmission;
import java.util.List;

/**
 * 作业提交数据访问接口
 */
@Mapper
public interface HomeworkSubmissionMapper {
    /**
     * 创建作业提交记录
     */
    @Insert({
        "INSERT INTO HomeworkSubmission(homework_id, student_id, submission_type,",
        "file_url, object_name, submitted_at)",
        "VALUES(#{homeworkId}, #{studentId}, #{submissionType},",
        "#{fileUrl}, #{objectName}, #{submittedAt})"
    })
    @Options(useGeneratedKeys = true, keyProperty = "id")
    void insert(HomeworkSubmission submission);

    /**
     * 根据ID查询提交记录
     */
    @Select("SELECT * FROM HomeworkSubmission WHERE id = #{id}")
    HomeworkSubmission selectById(Long id);

    /**
     * 根据作业ID查询所有提交记录
     */
    @Select("SELECT * FROM HomeworkSubmission WHERE homework_id = #{homeworkId} ORDER BY submitted_at DESC")
    List<HomeworkSubmission> selectByHomeworkId(Long homeworkId);

    /**
     * 查询学生的提交记录
     */
    @Select("SELECT * FROM HomeworkSubmission WHERE homework_id = #{homeworkId} AND student_id = #{studentId}")
    HomeworkSubmission selectByHomeworkAndStudent(@Param("homeworkId") Long homeworkId, @Param("studentId") Long studentId);

    /**
     * 更新提交记录
     */
    @Update({
        "UPDATE HomeworkSubmission",
        "SET submission_type = #{submissionType},",
        "    file_url = #{fileUrl},",
        "    object_name = #{objectName},",
        "    submitted_at = #{submittedAt}",
        "WHERE id = #{id}"
    })
    void update(HomeworkSubmission submission);

    /**
     * 删除提交记录
     */
    @Delete("DELETE FROM HomeworkSubmission WHERE id = #{id}")
    void deleteById(Long id);

    /**
     * 统计作业的提交数量
     */
    @Select("SELECT COUNT(*) FROM HomeworkSubmission WHERE homework_id = #{homeworkId}")
    int countSubmissions(Long homeworkId);
} 