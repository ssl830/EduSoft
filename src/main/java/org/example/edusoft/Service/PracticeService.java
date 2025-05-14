package org.example.edusoft.service;

import org.example.edusoft.entity.Practice;
import java.util.List;

public interface PracticeService {
    Practice findById(Long id);
    List<Practice> findByCourseId(Long courseId);
    List<Practice> findByCreatedBy(Long createdBy);
    Practice createPractice(Practice practice);
    Practice updatePractice(Practice practice);
    void deletePractice(Long id);
} 