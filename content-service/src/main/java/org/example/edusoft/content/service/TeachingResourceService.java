package org.example.edusoft.content.service;

import org.example.edusoft.content.entity.TeachingResource;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface TeachingResourceService {
    
    /**
     * 创建教学资源
     */
    TeachingResource createResource(TeachingResource resource, MultipartFile file);
    
    /**
     * 根据ID获取教学资源
     */
    TeachingResource getResourceById(Long id);
    
    /**
     * 根据作者ID获取教学资源列表
     */
    List<TeachingResource> getResourcesByAuthorId(Long authorId);
    
    /**
     * 根据课程ID获取教学资源列表
     */
    List<TeachingResource> getResourcesByCourseId(Long courseId);
    
    /**
     * 根据章节ID获取教学资源列表
     */
    List<TeachingResource> getResourcesByChapterId(Long chapterId);
    
    /**
     * 根据资源类型获取教学资源列表
     */
    List<TeachingResource> getResourcesByType(String resourceType);
    
    /**
     * 获取所有已发布的教学资源
     */
    List<TeachingResource> getAllPublishedResources();
    
    /**
     * 更新教学资源
     */
    TeachingResource updateResource(TeachingResource resource);
    
    /**
     * 增加浏览次数
     */
    void incrementViewCount(Long id);
    
    /**
     * 增加下载次数
     */
    void incrementDownloadCount(Long id);
    
    /**
     * 归档教学资源
     */
    void archiveResource(Long id);
}
