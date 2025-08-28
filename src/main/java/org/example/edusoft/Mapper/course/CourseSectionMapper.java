package org.example.edusoft.mapper.course;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.example.edusoft.entity.course.CourseSection;
import java.util.List;

@Mapper
public interface CourseSectionMapper extends BaseMapper<CourseSection> {
    @Select("SELECT * FROM coursesection WHERE course_id = #{courseId} ORDER BY sort_order")
    List<CourseSection> getSectionsByCourseId(Long courseId);
} 