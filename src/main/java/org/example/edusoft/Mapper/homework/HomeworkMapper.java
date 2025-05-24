package org.example.edusoft.mapper.homework;

import org.apache.ibatis.annotations.*;
import org.example.edusoft.entity.homework.Homework;
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
        "INSERT INTO Homework(title, description, class_id, created_by, attachment_url,",
        "object_name, deadline, created_at, updated_at)",
        "VALUES(#{title}, #{description}, #{classId}, #{createdBy}, #{attachmentUrl},",
        "#{objectName}, #{deadline}, #{createdAt}, #{updatedAt})"
    })
    @Options(useGeneratedKeys = true, keyProperty = "id")
    void insert(Homework homework);

    /**
     * 根据ID查询作业
     */
    @Select("SELECT * FROM Homework WHERE id = #{id}")
    Homework selectById(Long id);

    /**
     * 根据班级ID查询作业列表
     */
    @Select("SELECT * FROM Homework WHERE class_id = #{classId} ORDER BY created_at DESC")
    List<Homework> selectByClassId(Long classId);

    /**
     * 更新作业信息
     */
    @Update({
        "UPDATE Homework",
        "SET title = #{title},",
        "    description = #{description},",
        "    attachment_url = #{attachmentUrl},",
        "    object_name = #{objectName},",
        "    deadline = #{deadline},",
        "    updated_at = #{updatedAt}",
        "WHERE id = #{id}"
    })
    void update(Homework homework);

    /**
     * 删除作业
     */
    @Delete("DELETE FROM Homework WHERE id = #{id}")
    void deleteById(Long id);

    /**
     * 检查作业是否属于指定班级
     */
    @Select("SELECT COUNT(*) FROM Homework WHERE id = #{homeworkId} AND class_id = #{classId}")
    int checkHomeworkBelongsToClass(@Param("homeworkId") Long homeworkId, @Param("classId") Long classId);
} 