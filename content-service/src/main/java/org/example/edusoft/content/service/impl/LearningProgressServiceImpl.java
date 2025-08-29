package org.example.edusoft.content.service.impl;

import org.example.edusoft.content.entity.LearningProgress;
import org.example.edusoft.content.mapper.LearningProgressMapper;
import org.example.edusoft.content.service.LearningProgressService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class LearningProgressServiceImpl implements LearningProgressService {

    @Autowired
    private LearningProgressMapper learningProgressMapper;

    @Override
    public LearningProgress createProgress(LearningProgress progress) {
        learningProgressMapper.insert(progress);
        return progress;
    }

    @Override
    public LearningProgress getProgressById(Long id) {
        return learningProgressMapper.findById(id);
    }

    @Override
    public LearningProgress getProgressByResourceAndStudent(Long resourceId, Long studentId) {
        return learningProgressMapper.findByResourceAndStudent(resourceId, studentId);
    }

    @Override
    public List<LearningProgress> getProgressByStudentId(Long studentId) {
        return learningProgressMapper.findByStudentId(studentId);
    }

    @Override
    public List<LearningProgress> getProgressByResourceId(Long resourceId) {
        return learningProgressMapper.findByResourceId(resourceId);
    }

    @Override
    public LearningProgress updateProgress(LearningProgress progress) {
        learningProgressMapper.update(progress);
        return progress;
    }

    @Override
    public void updateProgress(Long resourceId, Long studentId, Integer progress, Integer lastPosition) {
        learningProgressMapper.updateProgress(resourceId, studentId, progress, lastPosition);
    }

    @Override
    public void incrementWatchCount(Long resourceId, Long studentId) {
        learningProgressMapper.incrementWatchCount(resourceId, studentId);
    }
}

