package org.example.edusoft.service.impl;

import org.example.edusoft.entity.Course;
import org.example.edusoft.mapper.CourseMapper;
import org.example.edusoft.service.CourseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class CourseServiceImpl implements CourseService {
    @Autowired
    private CourseMapper courseMapper;

    @Override
    public Course findById(Long id) {
        return courseMapper.findById(id);
    }

    @Override
    public Course findByCode(String code) {
        return courseMapper.findByCode(code);
    }

    @Override
    public List<Course> findByTeacherId(Long teacherId) {
        return courseMapper.findByTeacherId(teacherId);
    }

    @Override
    @Transactional
    public Course createCourse(Course course) {
        courseMapper.insert(course);
        return course;
    }

    @Override
    @Transactional
    public Course updateCourse(Course course) {
        courseMapper.update(course);
        return course;
    }

    @Override
    @Transactional
    public void deleteCourse(Long id) {
        courseMapper.deleteById(id);
    }
} 