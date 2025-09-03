package org.example.edusoft.content.service.progress;

import org.example.edusoft.content.dto.progress.LearningProgressDTO;
import org.example.edusoft.content.dto.progress.ProgressStatisticsDTO;
import org.example.edusoft.content.entity.resource.LearningProgress;
import java.util.List;

public interface LearningProgressService {
    
    /**
     * 更新学习进度
     */
    LearningProgress updateProgress(Long resourceId, Long studentId, Integer progress, Integer position);
    
    /**
     * 获取学习进度
     */
    LearningProgress getProgress(Long resourceId, Long studentId);
    
    /**
     * 获取学生的所有学习进度
     */
    List<LearningProgressDTO> getStudentProgress(Long studentId);
    
    /**
     * 获取资源的所有学习进度
     */
    List<LearningProgressDTO> getResourceProgress(Long resourceId);
    
    /**
     * 获取课程的学习进度统计
     */
    List<ProgressStatisticsDTO> getCourseProgressStatistics(Long courseId);
    
    /**
     * 获取章节的学习进度统计
     */
    List<ProgressStatisticsDTO> getChapterProgressStatistics(Long chapterId);
    
    /**
     * 删除学习进度
     */
    boolean deleteProgress(Long resourceId, Long studentId);
    
    /**
     * 批量更新学习进度
     */
    List<LearningProgress> batchUpdateProgress(List<LearningProgress> progressList);
}
