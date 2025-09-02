package org.example.edusoft.content.service.progress.impl;

import org.example.edusoft.content.dto.progress.LearningProgressDTO;
import org.example.edusoft.content.dto.progress.ProgressStatisticsDTO;
import org.example.edusoft.content.entity.resource.LearningProgress;
import org.example.edusoft.content.mapper.progress.LearningProgressMapper;
import org.example.edusoft.content.service.progress.LearningProgressService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.util.List;

@Service
@Transactional
public class LearningProgressServiceImpl implements LearningProgressService {
    
    @Autowired
    private LearningProgressMapper learningProgressMapper;
    
    @Override
    public LearningProgress updateProgress(Long resourceId, Long studentId, Double progress, Integer position) {
        LearningProgress learningProgress = new LearningProgress();
        learningProgress.setResourceId(resourceId);
        learningProgress.setStudentId(studentId);
        learningProgress.setProgress(progress);
        learningProgress.setPosition(position);
        learningProgress.setLastAccessedAt(LocalDateTime.now());
        learningProgress.setUpdatedAt(LocalDateTime.now());
        
        if (learningProgressMapper.selectProgress(resourceId, studentId) == null) {
            learningProgress.setCreatedAt(LocalDateTime.now());
        }
        
        learningProgressMapper.insertOrUpdateProgress(learningProgress);
        return learningProgress;
    }
    
    @Override
    public LearningProgress getProgress(Long resourceId, Long studentId) {
        return learningProgressMapper.selectProgress(resourceId, studentId);
    }
    
    @Override
    public List<LearningProgressDTO> getStudentProgress(Long studentId) {
        return learningProgressMapper.selectStudentProgress(studentId);
    }
    
    @Override
    public List<LearningProgressDTO> getResourceProgress(Long resourceId) {
        return learningProgressMapper.selectResourceProgress(resourceId);
    }
    
    @Override
    public List<ProgressStatisticsDTO> getCourseProgressStatistics(Long courseId) {
        return learningProgressMapper.selectCourseProgressStatistics(courseId);
    }
    
    @Override
    public List<ProgressStatisticsDTO> getChapterProgressStatistics(Long chapterId) {
        return learningProgressMapper.selectChapterProgressStatistics(chapterId);
    }
    
    @Override
    public boolean deleteProgress(Long resourceId, Long studentId) {
        return learningProgressMapper.deleteProgress(resourceId, studentId) > 0;
    }
    
    @Override
    public List<LearningProgress> batchUpdateProgress(List<LearningProgress> progressList) {
        for (LearningProgress progress : progressList) {
            progress.setUpdatedAt(LocalDateTime.now());
            learningProgressMapper.insertOrUpdateProgress(progress);
        }
        return progressList;
    }
}
