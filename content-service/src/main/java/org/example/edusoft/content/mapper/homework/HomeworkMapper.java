package org.example.edusoft.content.mapper.homework;

import org.apache.ibatis.annotations.*;
import org.example.edusoft.content.entity.homework.Homework;
import java.util.List;

/**
 * 作业数据访问接口
 */
@Mapper
public interface HomeworkMapper {
    /**
     * 创建新作业
     */
    @Insert({
        "INSERT INTO homework(title, description, course_id, chapter_id, chapter_name, class_id, created_by, created_by_name, attachment_url,",
        "object_name, file_name, deadline, status, created_at, updated_at)",
        "VALUES(#{title}, #{description}, #{courseId}, #{chapterId}, #{chapterName}, #{classId}, #{createdBy}, #{createdByName}, #{attachmentUrl},",
        "#{objectName}, #{fileName}, #{deadline}, #{status}, #{createdAt}, #{updatedAt})"
    })
    @Options(useGeneratedKeys = true, keyProperty = "id")
    void insert(Homework homework);

    /**
     * 根据ID查询作业
     */
    @Select("SELECT * FROM homework WHERE id = #{id}")
    Homework selectById(Long id);

    /**
     * 根据班级ID查询作业列表
     */
    @Select("SELECT * FROM homework WHERE class_id = #{classId} ORDER BY created_at DESC")
    List<Homework> selectByClassId(Long classId);

    /**
     * 更新作业信息
     */
    @Update({
        "UPDATE homework",
        "SET title = #{title},",
        "    description = #{description},",
        "    course_id = #{courseId},",
        "    chapter_id = #{chapterId},",
        "    chapter_name = #{chapterName},",
        "    attachment_url = #{attachmentUrl},",
        "    object_name = #{objectName},",
        "    file_name = #{fileName},",
        "    deadline = #{deadline},",
        "    status = #{status},",
        "    updated_at = #{updatedAt}",
        "WHERE id = #{id}"
    })
    void update(Homework homework);

    /**
     * 删除作业
     */
    @Delete("DELETE FROM homework WHERE id = #{id}")
    void deleteById(Long id);

    /**
     * 检查作业是否属于指定班级
     */
    @Select("SELECT COUNT(*) FROM homework WHERE id = #{homeworkId} AND class_id = #{classId}")
    int checkHomeworkBelongsToClass(@Param("homeworkId") Long homeworkId, @Param("classId") Long classId);
} 