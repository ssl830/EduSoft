package org.example.edusoft.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.example.edusoft.entity.CourseSection;

@Mapper
public interface CourseSectionMapper extends BaseMapper<CourseSection> {
	// BaseMapper 已有 selectById 方法，无需额外声明
}
