package org.example.edusoft.learning.mapper;

import org.apache.ibatis.annotations.*;
import org.example.edusoft.learning.entity.HomeworkSubmission;
import java.util.List;

@Mapper
public interface HomeworkSubmissionMapper {
    @Insert({
        "INSERT INTO homework_submission(homework_id, student_id, file_url, object_name, submitted_at)",
        "VALUES(#{homeworkId}, #{studentId}, #{fileUrl}, #{objectName}, #{submittedAt})"
    })
    @Options(useGeneratedKeys = true, keyProperty = "id")
    void insert(HomeworkSubmission submission);

    @Select("SELECT * FROM homework_submission WHERE id = #{id}")
    HomeworkSubmission selectById(Long id);

    @Select("SELECT hs.*, u.username AS student_name " +
            "FROM homework_submission hs " +
            "LEFT JOIN user u ON hs.student_id = u.id " +
            "WHERE hs.homework_id = #{homeworkId} " +
            "ORDER BY hs.submitted_at DESC")
    List<HomeworkSubmission> selectByHomeworkId(Long homeworkId);

    @Select("SELECT * FROM homework_submission WHERE homework_id = #{homeworkId} AND student_id = #{studentId} LIMIT 1")
    HomeworkSubmission selectByHomeworkAndStudent(@Param("homeworkId") Long homeworkId, @Param("studentId") Long studentId);

    @Delete("DELETE FROM homework_submission WHERE id = #{id}")
    void deleteById(Long id);

    @Delete("DELETE FROM homework_submission WHERE homework_id = #{homeworkId}")
    void deleteByHomeworkId(Long homeworkId);
}
