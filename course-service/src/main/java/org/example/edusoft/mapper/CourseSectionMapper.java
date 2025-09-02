package org.example.edusoft.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.example.edusoft.entity.CourseSection;

import java.util.List;

@Mapper
public interface CourseSectionMapper extends BaseMapper<CourseSection> {
	// BaseMapper 已有 selectById 方法，无需额外声明
    @Select("SELECT * FROM coursesection WHERE course_id = #{courseId} ORDER BY sort_order")
    List<CourseSection> getSectionsByCourseId(Long courseId);
}
