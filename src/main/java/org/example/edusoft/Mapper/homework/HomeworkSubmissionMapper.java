package org.example.edusoft.mapper.homework;

import org.apache.ibatis.annotations.*;
import org.example.edusoft.entity.homework.HomeworkSubmission;
// import org.example.edusoft.entity.homework.HomeworkSubmissionWithName;

import java.util.List;

/**
 * 作业提交数据访问接口
 */
@Mapper
public interface HomeworkSubmissionMapper {
    /**
     * 创建提交记录
     */
    @Insert({
        "INSERT INTO homeworksubmission(homework_id, student_id, file_url, object_name, submitted_at)",
        "VALUES(#{homeworkId}, #{studentId}, #{fileUrl}, #{objectName}, #{submittedAt})"
    })
    @Options(useGeneratedKeys = true, keyProperty = "id")
    void insert(HomeworkSubmission submission);

    /**
     * 根据ID查询提交记录
     */
    @Select("SELECT * FROM homeworksubmission WHERE id = #{id}")
    HomeworkSubmission selectById(Long id);

//     /**
//      * 根据作业ID查询所有提交记录
//      */
//     @Select("SELECT * FROM homeworksubmission WHERE homework_id = #{homeworkId} ORDER BY submitted_at DESC")
//     List<HomeworkSubmission> selectByHomeworkId(Long homeworkId);

    /**
     * 根据作业ID查询所有提交记录（带学生姓名）
     */
    @Select("SELECT hs.*, u.username AS student_name " +
            "FROM homeworksubmission hs " +
            "LEFT JOIN user u ON hs.student_id = u.id " +
            "WHERE hs.homework_id = #{homeworkId} " +
            "ORDER BY hs.submitted_at DESC")
    List<HomeworkSubmission> selectByHomeworkId(Long homeworkId);

    /**
     * 根据作业ID和学生ID查询提交记录
     */
    @Select("SELECT * FROM homeworksubmission WHERE homework_id = #{homeworkId} AND student_id = #{studentId} LIMIT 1")
    HomeworkSubmission selectByHomeworkAndStudent(@Param("homeworkId") Long homeworkId, @Param("studentId") Long studentId);

    /**
     * 删除提交记录
     */
    @Delete("DELETE FROM homeworksubmission WHERE id = #{id}")
    void deleteById(Long id);

    /**
     * 删除作业的所有提交记录
     */
    @Delete("DELETE FROM homeworksubmission WHERE homework_id = #{homeworkId}")
    void deleteByHomeworkId(Long homeworkId);


}
