package org.example.edusoft.service;

import org.example.edusoft.entity.Progress;
import java.util.List;

public interface ProgressService {
    Progress findById(Long id);
    List<Progress> findByStudentId(String studentId);
    List<Progress> findByCourseId(Long courseId);
    List<Progress> findByStudentAndCourse(String studentId, Long courseId);
    Progress createProgress(Progress progress);
    Progress updateProgress(Progress progress);
    void deleteProgress(Long id);
} 