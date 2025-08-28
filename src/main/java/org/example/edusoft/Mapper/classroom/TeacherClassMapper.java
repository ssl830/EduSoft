package org.example.edusoft.mapper.classroom;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.example.edusoft.entity.classroom.TeacherClassDTO;
import java.util.List;

@Mapper
public interface TeacherClassMapper {
    @Select("""
        SELECT c.id as classId, c.name as className, c.class_code as classCode, 
               c.course_id as courseId, co.name as courseName
        FROM class c
        JOIN course co ON c.course_id = co.id
        WHERE co.teacher_id = #{teacherId}
    """)
    List<TeacherClassDTO> getClassesByTeacherId(@Param("teacherId") Long teacherId);
} 