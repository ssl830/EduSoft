package org.example.edusoft.content.service.resource;

import org.example.edusoft.content.entity.resource.TeachingResource;
import org.example.edusoft.content.entity.resource.LearningProgress;
import org.example.edusoft.content.dto.resource.ResourceProgressDTO;
import org.springframework.web.multipart.MultipartFile;
import java.util.List;
import java.util.Map;

public interface TeachingResourceService {

    /**
     * 上传教学资源
     */
    TeachingResource uploadResource(MultipartFile file, Long courseId, Long chapterId,
                                    String chapterName, String title, String description, Long createdBy);

    /**
     * 获取教学资源详情
     */
    TeachingResource getResource(Long resourceId);
    
    /**
     * 获取课程的所有教学资源（按章节分组）
     */
    Map<Long, List<TeachingResource>> getResourcesByCourse(Long courseId);
    
    /**
     * 获取章节的教学资源
     */
    List<TeachingResource> getResourcesByChapter(Long courseId, Long chapterId);
    
    /**
     * 删除教学资源
     */
    boolean deleteResource(Long resourceId, Long operatorId);
    
    /**
     * 更新学习进度
     */
    LearningProgress updateProgress(Long resourceId, Long studentId, Integer progress, Integer position);
    
    /**
     * 获取学习进度
     */
    LearningProgress getProgress(Long resourceId, Long studentId);
    
    /**
     * 获取资源访问URL（带签名的临时访问URL）
     */
    String getSignedResourceUrl(Long resourceId);
    
    /**
     * 获取课程资源及学习进度信息
     */
    List<ResourceProgressDTO> getCourseResourcesWithProgress(Long courseId, Long studentId, Long chapterId);
    
    /**
     * 更新资源时长
     */
    TeachingResource updateResourceDuration(Long resourceId, Integer duration);
    

    /**
     * 同步到AI知识库
     */
    void syncToAIKnowledgeBase(MultipartFile file, Long resourceId);

    /**
     * 创建教学资源
     */
    TeachingResource createResource(TeachingResource resource);

    /**
     * 统计指定课程的资源总数
     * @param courseId 课程ID
     * @return 资源总数
     */
    int countResourcesByCourseId(Long courseId);

    /**
     * 获取某个学生的所有学习记录
     */
    List<org.example.edusoft.content.entity.StudyRecord> getStudyRecordsByStudentId(Long studentId);
}