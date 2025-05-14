package org.example.edusoft.mapper;

import org.apache.ibatis.annotations.*;
import org.example.edusoft.entity.Course;
import java.util.List;

@Mapper
public interface CourseMapper {
    @Select("SELECT * FROM Course WHERE id = #{id}")
    Course findById(Long id);

    @Select("SELECT * FROM Course WHERE teacher_id = #{teacherId}")
    List<Course> findByTeacherId(String teacherId);

    @Select("SELECT * FROM Course WHERE code = #{code}")
    Course findByCode(String code);

    @Select("SELECT DISTINCT c.* FROM Course c " +
            "LEFT JOIN Class cl ON c.id = cl.course_id " +
            "LEFT JOIN ClassStudent cs ON cl.id = cs.class_id " +
            "WHERE (c.teacher_id = #{userId} AND #{role} = 'teacher') " +
            "OR (cs.student_id = #{userId} AND #{role} = 'student') " +
            "OR (c.teacher_id = #{userId} AND #{role} = 'ta')")
    List<Course> findByUserIdAndRole(@Param("userId") String userId, @Param("role") String role);

    @Insert("INSERT INTO Course (name, code, teacher_id, outline, objective, assessment, created_at) " +
            "VALUES (#{name}, #{code}, #{teacherId}, #{outline}, #{objective}, #{assessment}, #{createdAt})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(Course course);

    @Update("UPDATE Course SET name = #{name}, code = #{code}, outline = #{outline}, " +
            "objective = #{objective}, assessment = #{assessment} WHERE id = #{id}")
    int update(Course course);

    @Delete("DELETE FROM Course WHERE id = #{id}")
    int deleteById(Long id);

    @Delete("DELETE FROM Course")
    int deleteAll();
} 