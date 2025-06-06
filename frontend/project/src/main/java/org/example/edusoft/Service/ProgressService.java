package org.example.edusoft.service;

import org.example.edusoft.entity.Progress;
import java.util.List;

public interface ProgressService {
    Progress findById(Long id);
    List<Progress> findByStudentId(Long studentId);
    List<Progress> findByCourseId(Long courseId);
    List<Progress> findByStudentAndCourse(Long studentId, Long courseId);
    Progress createProgress(Progress progress);
    Progress updateProgress(Progress progress);
    void deleteProgress(Long id);
} 