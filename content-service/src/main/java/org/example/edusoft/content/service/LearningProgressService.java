package org.example.edusoft.content.service;

import org.example.edusoft.content.entity.LearningProgress;

import java.util.List;

public interface LearningProgressService {
    
    LearningProgress createProgress(LearningProgress progress);
    
    LearningProgress getProgressById(Long id);
    
    LearningProgress getProgressByResourceAndStudent(Long resourceId, Long studentId);
    
    List<LearningProgress> getProgressByStudentId(Long studentId);
    
    List<LearningProgress> getProgressByResourceId(Long resourceId);
    
    LearningProgress updateProgress(LearningProgress progress);
    
    void updateProgress(Long resourceId, Long studentId, Integer progress, Integer lastPosition);
    
    void incrementWatchCount(Long resourceId, Long studentId);
}

