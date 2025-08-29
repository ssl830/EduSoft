package org.example.edusoft.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.example.edusoft.entity.Class;
import org.example.edusoft.dto.ClassDetailDTO;
import java.util.List;

@Mapper
public interface ClassMapper extends BaseMapper<Class> {
    
    @Select("SELECT c.id, c.course_id as courseId, co.name as courseName, " +
            "co.teacher_id as teacherId, '教师' as teacherName, " +
            "c.name as className, c.class_code as classCode " +
            "FROM class c " +
            "LEFT JOIN course co ON c.course_id = co.id " +
            "WHERE c.id = #{id}")
    ClassDetailDTO getClassDetailById(Long id);

    @Select("SELECT DISTINCT c.id, c.course_id as courseId, co.name as courseName, " +
            "co.teacher_id as teacherId, '教师' as teacherName, " +
            "c.name as className, c.class_code as classCode " +
            "FROM class c " +
            "LEFT JOIN course co ON c.course_id = co.id " +
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
    
    @Insert("INSERT INTO courseclass (course_id, class_id) VALUES (#{courseId}, #{classId})")
    int insertCourseClassRelation(Long courseId, Long classId);
    
    @Delete("DELETE FROM courseclass WHERE course_id = #{courseId} AND class_id = #{classId}")
    int deleteCourseClassRelation(Long courseId, Long classId);
    
    @Delete("DELETE FROM courseclass WHERE class_id = #{classId}")
    int deleteCourseClassRelationByClassId(Long classId);
}
