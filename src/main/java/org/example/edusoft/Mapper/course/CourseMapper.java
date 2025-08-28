package org.example.edusoft.mapper.course;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.toolkit.Constants;
import org.apache.ibatis.annotations.*;
import org.example.edusoft.entity.course.Course;
import org.example.edusoft.entity.course.CourseDetailDTO;
import org.example.edusoft.entity.course.CourseSection;
import java.util.List;

@Mapper
public interface CourseMapper extends BaseMapper<Course> {
    
    @Select("SELECT c.* FROM course c " +
            "LEFT JOIN classuser cu ON c.id = cu.class_id " +
            "WHERE c.teacher_id = #{userId} OR cu.user_id = #{userId}")
    List<Course> getCoursesByUserId(Long userId);

    @Select("""
            SELECT 
                c.*,
                u.username as teacherName,
                (SELECT COUNT(DISTINCT cu.user_id) 
                 FROM classuser cu 
                 JOIN class cl ON cu.class_id = cl.id 
                 WHERE cl.course_id = c.id) as studentCount,
                (SELECT COUNT(*) FROM practice p WHERE p.course_id = c.id) as practiceCount,
                (SELECT COUNT(*) FROM homework h 
                 JOIN class cl ON h.class_id = cl.id 
                 WHERE cl.course_id = c.id) as homeworkCount,
                0 as resourceCount
            FROM course c
            LEFT JOIN user u ON c.teacher_id = u.id
            WHERE c.id = #{courseId}
            """)
    CourseDetailDTO getCourseDetailById(Long courseId);

    @Select("SELECT * FROM coursesection WHERE course_id = #{courseId} ORDER BY sort_order")
    List<CourseSection> getSectionsByCourseId(Long courseId);

    @Select("""
            SELECT 
                c.*,
                u.username as teacherName,
                (SELECT COUNT(DISTINCT cu.user_id) 
                 FROM classuser cu 
                 JOIN class cl ON cu.class_id = cl.id 
                 WHERE cl.course_id = c.id) as studentCount,
                (SELECT COUNT(*) FROM practice p WHERE p.course_id = c.id) as practiceCount,
                (SELECT COUNT(*) FROM homework h 
                 JOIN class cl ON h.class_id = cl.id 
                 WHERE cl.course_id = c.id) as homeworkCount,
                0 as resourceCount
            FROM course c
            LEFT JOIN user u ON c.teacher_id = u.id
            WHERE c.teacher_id = #{userId} OR EXISTS (
                SELECT 1 FROM classuser cu 
                JOIN class cl ON cu.class_id = cl.id 
                WHERE cl.course_id = c.id AND cu.user_id = #{userId}
            )
            """)
    List<CourseDetailDTO> getCourseDetailsByUserId(Long userId);

    @Insert("INSERT INTO course (name, code, teacher_id, outline, objective, assessment) " +
            "VALUES (#{name}, #{code}, #{teacherId}, #{outline}, #{objective}, #{assessment})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(Course course);

    @Select("SELECT * FROM course WHERE id = #{id}")
    Course selectById(Long id);

    @Select("SELECT c.*, " +
            "t.name as teacher_name, " +
            "s.name as section_name " +
            "FROM course c " +
            "LEFT JOIN teacher t ON c.teacher_id = t.id " +
            "LEFT JOIN section s ON c.section_id = s.id " +
            "WHERE c.id = #{id}")
    CourseDetailDTO selectCourseDetailById(Long id);

    @Select("SELECT c.*, " +
            "t.name as teacher_name, " +
            "s.name as section_name " +
            "FROM course c " +
            "LEFT JOIN teacher t ON c.teacher_id = t.id " +
            "LEFT JOIN section s ON c.section_id = s.id " +
            "WHERE c.teacher_id = #{teacherId} " +
            "ORDER BY c.id DESC")
    List<CourseDetailDTO> getCourseDetailsByTeacherId(Long teacherId);

    @Select("SELECT c.*, " +
            "u.username as teacher_name " +
            "FROM course c " +
            "LEFT JOIN user u ON c.teacher_id = u.id " +
            "WHERE 1=1 " +
            "${ew.customSqlSegment}")
    List<CourseDetailDTO> selectAllCoursesWithNames(@Param(Constants.WRAPPER) Wrapper<Course> queryWrapper);

    @Update("UPDATE course SET name = #{name}, code = #{code}, " +
            "teacher_id = #{teacherId}, outline = #{outline}, " +
            "objective = #{objective}, assessment = #{assessment} " +
            "WHERE id = #{id}")
    int update(Course course);

    @Delete("DELETE FROM course WHERE id = #{id}")
    int deleteById(Long id);
    @Select("""
            SELECT 
                cl.id,
                cl.name,
                cl.class_code as classCode,
                (SELECT COUNT(DISTINCT cu.user_id) 
                 FROM classuser cu 
                 WHERE cu.class_id = cl.id) as studentCount
            FROM courseclass cc
            JOIN class cl ON cc.class_id = cl.id
            WHERE cc.course_id = #{courseId}
            """)
    List<CourseDetailDTO.ClassInfo> getClassesByCourseId(Long courseId);
} 