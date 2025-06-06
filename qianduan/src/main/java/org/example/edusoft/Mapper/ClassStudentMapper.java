package org.example.edusoft.mapper;

import org.apache.ibatis.annotations.*;
import org.example.edusoft.entity.ClassStudent;
import java.util.List;

@Mapper
public interface ClassStudentMapper {
    @Select("SELECT * FROM ClassStudent WHERE class_id = #{classId} AND student_id = #{studentId}")
    ClassStudent findByClassAndStudent(Long classId, Long studentId);

    @Select("SELECT * FROM ClassStudent WHERE class_id = #{classId}")
    List<ClassStudent> findByClassId(Long classId);

    @Select("SELECT * FROM ClassStudent WHERE student_id = #{studentId}")
    List<ClassStudent> findByStudentId(Long studentId);

    @Insert("INSERT INTO ClassStudent (class_id, student_id) VALUES (#{classId}, #{studentId})")
    int insert(ClassStudent classStudent);

    @Delete("DELETE FROM ClassStudent WHERE class_id = #{classId} AND student_id = #{studentId}")
    int delete(Long classId, Long studentId);
} 