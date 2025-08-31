package org.example.edusoft.service.impl;

import org.example.edusoft.entity.CourseSection;
import org.example.edusoft.mapper.CourseSectionMapper;
import org.example.edusoft.service.CourseSectionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class CourseSectionServiceImpl implements CourseSectionService {

    @Autowired
    private CourseSectionMapper courseSectionMapper;

    @Override
    public List<CourseSection> getSectionsByCourseId(Long courseId) {
        if (courseId == null) {
            throw new IllegalArgumentException("课程ID不能为空");
        }
        return courseSectionMapper.selectList(
            new com.baomidou.mybatisplus.core.conditions.query.QueryWrapper<CourseSection>()
                .eq("course_id", courseId)
                .orderByAsc("sort_order")
        );
    }

    @Override
    @Transactional
    public CourseSection createSection(CourseSection section) {
        if (section.getCourseId() == null) {
            throw new IllegalArgumentException("课程ID不能为空");
        }
        if (section.getTitle() == null || section.getTitle().trim().isEmpty()) {
            throw new IllegalArgumentException("章节标题不能为空");
        }
        
        courseSectionMapper.insert(section);
        return section;
    }

    @Override
    @Transactional
    public CourseSection updateSection(CourseSection section) {
        if (section.getId() == null) {
            throw new IllegalArgumentException("章节ID不能为空");
        }
        
        CourseSection existingSection = courseSectionMapper.selectById(section.getId());
        if (existingSection == null) {
            throw new IllegalArgumentException("章节不存在");
        }
        
        courseSectionMapper.updateById(section);
        return section;
    }

    @Override
    @Transactional
    public boolean deleteSection(Long id) {
        if (id == null) {
            throw new IllegalArgumentException("章节ID不能为空");
        }
        
        CourseSection section = courseSectionMapper.selectById(id);
        if (section == null) {
            throw new IllegalArgumentException("章节不存在");
        }
        
        return courseSectionMapper.deleteById(id) > 0;
    }

    @Override
    public CourseSection getSectionById(Long id) {
        if (id == null) {
            throw new IllegalArgumentException("章节ID不能为空");
        }
        return courseSectionMapper.selectById(id);
    }
}
