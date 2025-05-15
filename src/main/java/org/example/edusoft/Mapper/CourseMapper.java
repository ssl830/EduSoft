package org.example.edusoft.Mapper;

import org.apache.ibatis.annotations.*;
import org.example.edusoft.entity.Course;
import java.util.List;

@Mapper
public interface CourseMapper {
    @Select("SELECT * FROM Course WHERE id = #{id}")
    Course findById(Long id);

    @Select("SELECT * FROM Course WHERE teacher_id = #{teacherId}")
    List<Course> findByTeacherId(Long teacherId);

    @Select("SELECT * FROM Course WHERE code = #{code}")
    Course findByCode(String code);

    @Insert("INSERT INTO Course (teacher_id, name, code, outline, objective, assessment) " +
            "VALUES (#{teacherId}, #{name}, #{code}, #{outline}, #{objective}, #{assessment})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(Course course);

    @Update("UPDATE Course SET name = #{name}, outline = #{outline}, " +
            "objective = #{objective}, assessment = #{assessment} WHERE id = #{id}")
    int update(Course course);

    @Delete("DELETE FROM Course WHERE id = #{id}")
    int deleteById(Long id);
} 