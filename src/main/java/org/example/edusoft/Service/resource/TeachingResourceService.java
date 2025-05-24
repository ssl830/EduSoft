package org.example.edusoft.service.resource;

import org.example.edusoft.entity.resource.TeachingResource;
import org.example.edusoft.entity.resource.LearningProgress;
import org.springframework.web.multipart.MultipartFile;
import java.util.List;
import java.util.Map;

/**
 * 教学资源服务接口
 */
public interface TeachingResourceService {
    
    /**
     * 上传教学资源
     * @param file 视频文件
     * @param courseId 课程ID
     * @param chapterId 章节ID
     * @param chapterName 章节名称
     * @param title 资源标题
     * @param description 资源描述
     * @param createdBy 创建者ID
     * @return 教学资源对象
     */
    TeachingResource uploadResource(MultipartFile file, Long courseId, Long chapterId, 
        String chapterName, String title, String description, Long createdBy);

    /**
     * 获取教学资源详情
     * @param resourceId 资源ID
     * @return 教学资源对象
     */
    TeachingResource getResource(Long resourceId);

    /**
     * 获取课程的所有教学资源（按章节分组）
     * @param courseId 课程ID
     * @return 按章节分组的教学资源Map
     */
    Map<Long, List<TeachingResource>> getResourcesByCourse(Long courseId);

    /**
     * 获取章节的教学资源
     * @param courseId 课程ID
     * @param chapterId 章节ID
     * @return 教学资源列表
     */
    List<TeachingResource> getResourcesByChapter(Long courseId, Long chapterId);

    /**
     * 删除教学资源
     * @param resourceId 资源ID
     * @param operatorId 操作者ID
     * @return 是否删除成功
     */
    boolean deleteResource(Long resourceId, Long operatorId);

    /**
     * 更新学习进度
     * @param resourceId 资源ID
     * @param studentId 学生ID
     * @param progress 当前进度（秒）
     * @param position 当前播放位置（秒）
     * @return 更新后的学习进度
     */
    LearningProgress updateProgress(Long resourceId, Long studentId, Integer progress, Integer position);

    /**
     * 获取学习进度
     * @param resourceId 资源ID
     * @param studentId 学生ID
     * @return 学习进度对象
     */
    LearningProgress getProgress(Long resourceId, Long studentId);

    /**
     * 获取资源的基础访问URL
     * @param resourceId 资源ID
     * @return 基础访问URL
     */
    String getResourceUrl(Long resourceId);

    /**
     * 获取资源的临时访问URL（带签名，有效期1小时）
     * @param resourceId 资源ID
     * @return 带签名的临时访问URL
     */
    String getSignedResourceUrl(Long resourceId);
} 