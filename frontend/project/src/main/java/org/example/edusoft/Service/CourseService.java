package org.example.edusoft.service;

import org.example.edusoft.entity.Course;
import java.util.List;

public interface CourseService {
    Course findById(Long id);
    Course findByCode(String code);
    List<Course> findByTeacherId(Long teacherId);
    Course createCourse(Course course);
    Course updateCourse(Course course);
    void deleteCourse(Long id);
} 