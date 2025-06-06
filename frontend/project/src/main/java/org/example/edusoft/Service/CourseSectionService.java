package org.example.edusoft.service;

import org.example.edusoft.entity.CourseSection;
import java.util.List;

public interface CourseSectionService {
    CourseSection findById(Long id);
    List<CourseSection> findByCourseId(Long courseId);
    CourseSection createSection(CourseSection section);
    CourseSection updateSection(CourseSection section);
    void deleteSection(Long id);
} 