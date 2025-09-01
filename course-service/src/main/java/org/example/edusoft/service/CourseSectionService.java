package org.example.edusoft.service;

import org.example.edusoft.entity.CourseSection;
import java.util.List;

public interface CourseSectionService {
    List<CourseSection> getSectionsByCourseId(Long courseId);
    CourseSection createSection(CourseSection section);
    CourseSection updateSection(CourseSection section);
    boolean deleteSection(Long id);
    CourseSection getSectionById(Long id);
}
