package org.example.edusoft.learning.mapper.homework;

import org.apache.ibatis.annotations.*;
import org.example.edusoft.learning.entity.homework.Homework;
import java.util.List;

@Mapper
public interface HomeworkMapper {
    @Insert({
        "INSERT INTO homework(title, description, class_id, created_by, attachment_url,",
        "object_name, deadline, created_at, updated_at)",
        "VALUES(#{title}, #{description}, #{classId}, #{createdBy}, #{attachmentUrl},",
        "#{objectName}, #{deadline}, #{createdAt}, #{updatedAt})"
    })
    @Options(useGeneratedKeys = true, keyProperty = "id")
    void insert(Homework homework);

    @Select("SELECT * FROM homework WHERE id = #{id}")
    Homework selectById(Long id);

    @Select("SELECT * FROM homework WHERE class_id = #{classId} ORDER BY created_at DESC")
    List<Homework> selectByClassId(Long classId);

    @Update({
        "UPDATE homework",
        "SET title = #{title},",
        "    description = #{description},",
        "    attachment_url = #{attachmentUrl},",
        "    object_name = #{objectName},",
        "    deadline = #{deadline},",
        "    updated_at = #{updatedAt}",
        "WHERE id = #{id}"
    })
    void update(Homework homework);

    @Delete("DELETE FROM homework WHERE id = #{id}")
    void deleteById(Long id);

    @Select("SELECT COUNT(*) FROM homework WHERE id = #{homeworkId} AND class_id = #{classId}")
    int checkHomeworkBelongsToClass(@Param("homeworkId") Long homeworkId, @Param("classId") Long classId);
}
