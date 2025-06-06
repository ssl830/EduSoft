package org.example.edusoft.mapper;

import org.apache.ibatis.annotations.*;
import org.example.edusoft.entity.Class;
import java.util.List;

@Mapper
public interface ClassMapper {
    @Select("SELECT * FROM Class WHERE id = #{id}")
    Class findById(Long id);

    @Select("SELECT * FROM Class WHERE course_id = #{courseId}")
    List<Class> findByCourseId(Long courseId);

    @Select("SELECT * FROM Class WHERE class_code = #{classCode}")
    Class findByClassCode(String classCode);

    @Insert("INSERT INTO Class (course_id, name, class_code) " +
            "VALUES (#{courseId}, #{name}, #{classCode})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(Class clazz);

    @Update("UPDATE Class SET name = #{name} WHERE id = #{id}")
    int update(Class clazz);

    @Delete("DELETE FROM Class WHERE id = #{id}")
    int deleteById(Long id);
} 