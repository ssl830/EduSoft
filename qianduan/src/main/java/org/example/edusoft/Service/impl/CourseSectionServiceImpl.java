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
    public CourseSection findById(Long id) {
        return courseSectionMapper.findById(id);
    }

    @Override
    public List<CourseSection> findByCourseId(Long courseId) {
        return courseSectionMapper.findByCourseId(courseId);
    }

    @Override
    @Transactional
    public CourseSection createSection(CourseSection section) {
        courseSectionMapper.insert(section);
        return section;
    }

    @Override
    @Transactional
    public CourseSection updateSection(CourseSection section) {
        courseSectionMapper.update(section);
        return section;
    }

    @Override
    @Transactional
    public void deleteSection(Long id) {
        courseSectionMapper.deleteById(id);
    }
} 