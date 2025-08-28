package org.example.edusoft.mapper.classroom;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.example.edusoft.entity.classroom.Class;
import org.example.edusoft.entity.classroom.ClassDetailDTO;
import java.util.List;

@Mapper
public interface ClassMapper extends BaseMapper<Class> {
    
    @Select("SELECT c.id, c.course_id as courseId, co.name as courseName, " +
            "co.teacher_id as teacherId, u.username as teacherName, " +
            "c.name as className, c.class_code as classCode " +
            "FROM class c " +
            "LEFT JOIN course co ON c.course_id = co.id " +
            "LEFT JOIN user u ON co.teacher_id = u.id " +
            "WHERE c.id = #{id}")
    ClassDetailDTO getClassDetailById(Long id);

    @Select("SELECT DISTINCT c.id, c.course_id as courseId, co.name as courseName, " +
            "co.teacher_id as teacherId, u.username as teacherName, " +
            "c.name as className, c.class_code as classCode " +
            "FROM class c " +
            "LEFT JOIN course co ON c.course_id = co.id " +
            "LEFT JOIN user u ON co.teacher_id = u.id " +
            "LEFT JOIN classuser cu ON c.id = cu.class_id " +
            "WHERE co.teacher_id = #{userId} OR cu.user_id = #{userId}")
    List<ClassDetailDTO> getClassesByUserId(Long userId);

    @Select("SELECT c.* FROM class c " +
            "LEFT JOIN course co ON c.course_id = co.id " +
            "WHERE co.teacher_id = #{teacherId}")
    List<Class> getClassesByTeacherId(Long teacherId);
    
    @Select("SELECT c.* FROM class c " +
            "LEFT JOIN classuser cu ON c.id = cu.class_id " +
            "WHERE cu.user_id = #{studentId}")
    List<Class> getClassesByStudentId(Long studentId);

    @Select("SELECT COUNT(*) FROM classuser WHERE class_id = #{classId}")
    int getClassStudentCount(Long classId);
}