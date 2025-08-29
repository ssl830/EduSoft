package org.example.edusoft.mapper;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.toolkit.Constants;
import org.apache.ibatis.annotations.*;
import org.example.edusoft.entity.Course;
import org.example.edusoft.dto.CourseDetailDTO;
import org.example.edusoft.entity.CourseSection;
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
                '教师' as teacherName,
                (SELECT COUNT(DISTINCT cu.user_id) 
                 FROM classuser cu 
                 JOIN class cl ON cu.class_id = cl.id 
                 WHERE cl.course_id = c.id) as studentCount,
                0 as practiceCount,
                0 as homeworkCount,
                0 as resourceCount
            FROM course c
            WHERE c.id = #{courseId}
            """)
    CourseDetailDTO getCourseDetailById(Long courseId);

    @Select("SELECT * FROM coursesection WHERE course_id = #{courseId} ORDER BY sort_order")
    List<CourseSection> getSectionsByCourseId(Long courseId);

    @Select("""
            SELECT 
                c.*,
                '教师' as teacherName,
                (SELECT COUNT(DISTINCT cu.user_id) 
                 FROM classuser cu 
                 JOIN class cl ON cu.class_id = cl.id 
                 WHERE cl.course_id = c.id) as studentCount,
                0 as practiceCount,
                0 as homeworkCount,
                0 as resourceCount
            FROM course c
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

    @Select("""
            SELECT 
                c.*,
                '教师' as teacherName,
                (SELECT COUNT(DISTINCT cu.user_id) 
                 FROM classuser cu 
                 JOIN class cl ON cu.class_id = cl.id 
                 WHERE cl.course_id = c.id) as studentCount,
                0 as practiceCount,
                0 as homeworkCount,
                0 as resourceCount
            FROM course c
            WHERE c.id = #{id}
            """)
    CourseDetailDTO selectCourseDetailById(Long id);

    @Select("""
            SELECT 
                c.*,
                '教师' as teacherName,
                (SELECT COUNT(DISTINCT cu.user_id) 
                 FROM classuser cu 
                 JOIN class cl ON cu.class_id = cl.id 
                 WHERE cl.course_id = c.id) as studentCount,
                0 as practiceCount,
                0 as homeworkCount,
                0 as resourceCount
            FROM course c
            WHERE c.teacher_id = #{teacherId} 
            ORDER BY c.id DESC
            """)
    List<CourseDetailDTO> getCourseDetailsByTeacherId(Long teacherId);

    @Select("""
            SELECT 
                c.*,
                '教师' as teacherName,
                (SELECT COUNT(DISTINCT cu.user_id) 
                 FROM classuser cu 
                 JOIN class cl ON cu.class_id = cl.id 
                 WHERE cl.course_id = c.id) as studentCount,
                0 as practiceCount,
                0 as homeworkCount,
                0 as resourceCount
            FROM course c
            WHERE 1=1 
            ${ew.customSqlSegment}
            """)
    List<CourseDetailDTO> selectAllCoursesWithNames(@Param(Constants.WRAPPER) Wrapper<Course> queryWrapper);

    @Update("UPDATE course SET name = #{name}, code = #{code}, " +
            "teacher_id = #{teacherId}, outline = #{outline}, " +
            "objective = #{objective}, assessment = #{assessment} " +
            "WHERE id = #{id}")
    int update(Course course);

    @Delete("DELETE FROM course WHERE id = #{id}")
    int deleteById(Long id);
    
    // 删除课程相关的题库题目
    @Delete("DELETE FROM question WHERE course_id = #{courseId}")
    int deleteQuestionsByCourseId(Long courseId);
    
    // 删除课程相关的练习
    @Delete("DELETE FROM practice WHERE course_id = #{courseId}")
    int deletePracticesByCourseId(Long courseId);
    
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
