package org.example.edusoft.content.mapper.homework;

import org.apache.ibatis.annotations.*;
import org.example.edusoft.content.entity.homework.HomeworkSubmission;
// import org.example.edusoft.entity.homework.HomeworkSubmissionWithName;

import java.time.LocalDate;
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
        "INSERT INTO homework_submission(homework_id, student_id, file_url, object_name, submitted_at)",
        "VALUES(#{homeworkId}, #{studentId}, #{fileUrl}, #{objectName}, #{submittedAt})"
    })
    @Options(useGeneratedKeys = true, keyProperty = "id")
    void insert(HomeworkSubmission submission);

    /**
     * 根据ID查询提交记录
     */
    @Select("SELECT * FROM homework_submission WHERE id = #{id}")
    HomeworkSubmission selectById(Long id);

//     /**
//      * 根据作业ID查询所有提交记录
//      */
//     @Select("SELECT * FROM homeworksubmission WHERE homework_id = #{homeworkId} ORDER BY submitted_at DESC")
//     List<HomeworkSubmission> selectByHomeworkId(Long homeworkId);

    /**
     * 根据作业ID查询所有提交记录
     */
    @Select("SELECT * FROM homework_submission WHERE homework_id = #{homeworkId} ORDER BY submitted_at DESC")
    List<HomeworkSubmission> selectByHomeworkId(Long homeworkId);

    /**
     * 根据作业ID和学生ID查询提交记录
     */
    @Select("SELECT * FROM homework_submission WHERE homework_id = #{homeworkId} AND student_id = #{studentId} LIMIT 1")
    HomeworkSubmission selectByHomeworkAndStudent(@Param("homeworkId") Long homeworkId, @Param("studentId") Long studentId);

    /**
     * 删除提交记录
     */
    @Delete("DELETE FROM homework_submission WHERE id = #{id}")
    void deleteById(Long id);

    /**
     * 删除作业的所有提交记录
     */
    @Delete("DELETE FROM homework_submission WHERE homework_id = #{homeworkId}")
    void deleteByHomeworkId(Long homeworkId);
    
    /**
     * 更新提交记录
     */
    @Update("UPDATE homework_submission SET feedback = #{feedback}, score = #{score}, updated_at = #{updatedAt} WHERE id = #{id}")
    int updateById(HomeworkSubmission submission);

    /**
     * 统计学生提交的作业数量
     */
    @Select({"<script>",
            "SELECT COUNT(*) FROM homework_submission",
            "WHERE student_id IN",
            "<foreach collection='studentIds' item='studentId' open='(' separator=',' close=')'>",
            "#{studentId}",
            "</foreach>",
            "AND DATE(submitted_at) BETWEEN #{start} AND #{end}",
            "</script>"})
    int countStudentSubmitHomework(@Param("start") LocalDate start,
                                 @Param("end") LocalDate end,
                                 @Param("studentIds") List<Long> studentIds);
}
