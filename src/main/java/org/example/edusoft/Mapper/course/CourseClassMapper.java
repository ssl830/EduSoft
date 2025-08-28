package org.example.edusoft.mapper.course;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.example.edusoft.entity.course.CourseClass;

@Mapper
public interface CourseClassMapper extends BaseMapper<CourseClass> {
    
    @Insert("INSERT INTO courseclass (course_id, class_id) VALUES (#{courseId}, #{classId})")
    int insertCourseClass(Long courseId, Long classId);
    
    @Select("SELECT COUNT(*) FROM courseclass WHERE course_id = #{courseId} AND class_id = #{classId}")
    int checkCourseClassExists(Long courseId, Long classId);
} 