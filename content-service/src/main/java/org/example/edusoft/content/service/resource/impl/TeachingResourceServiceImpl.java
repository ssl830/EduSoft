package org.example.edusoft.content.service.resource.impl;

import org.example.edusoft.content.entity.resource.TeachingResource;
import org.example.edusoft.content.entity.resource.LearningProgress;
import org.example.edusoft.content.dto.resource.ResourceProgressDTO;
import org.example.edusoft.content.mapper.resource.TeachingResourceMapper;
import org.example.edusoft.content.service.resource.TeachingResourceService;
import org.example.edusoft.content.service.FileUploadService;
import org.example.edusoft.content.exception.BusinessException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@Transactional
public class TeachingResourceServiceImpl implements TeachingResourceService {
    
    @Autowired
    private TeachingResourceMapper teachingResourceMapper;
    
    @Autowired
    private FileUploadService fileUploadService;
    
    @Override
    public TeachingResource createResource(TeachingResource resource) {
        if (resource == null) {
            throw new BusinessException("教学资源不能为空");
        }
        
        resource.setCreatedAt(LocalDateTime.now());
        resource.setUpdatedAt(LocalDateTime.now());
        
        teachingResourceMapper.insert(resource);
        return resource;
    }
    
    @Override
    public TeachingResource uploadResource(MultipartFile file, Long courseId, Long chapterId, String chapterName, String title, String description, Long createdBy) {
        if (file == null || file.isEmpty()) {
            throw new BusinessException("上传文件不能为空");
        }
        
        // 上传文件到阿里云OSS
        String folder = "teaching-resources/" + courseId + "/" + chapterId;
        String fileUrl = fileUploadService.uploadFile(file, folder);
        
        TeachingResource resource = new TeachingResource();
        resource.setTitle(title);
        resource.setDescription(description);
        resource.setType(file.getContentType());
        resource.setUrl(fileUrl); // 使用真实的OSS文件URL
        resource.setFileSize(file.getSize());
        resource.setCourseId(courseId);
        resource.setChapterId(chapterId);
        resource.setCreatorId(createdBy);
        resource.setCreatedAt(LocalDateTime.now());
        resource.setUpdatedAt(LocalDateTime.now());
        
        teachingResourceMapper.insert(resource);
        return resource;
    }
    
    @Override
    public TeachingResource getResource(Long resourceId) {
        if (resourceId == null) {
            throw new BusinessException("资源ID不能为空");
        }
        return teachingResourceMapper.selectById(resourceId);
    }
    
    @Override
    public Map<Long, List<TeachingResource>> getResourcesByCourse(Long courseId) {
        if (courseId == null) {
            throw new BusinessException("课程ID不能为空");
        }
        
        List<TeachingResource> resources = teachingResourceMapper.selectByCourseId(courseId);
        return resources.stream()
                .collect(Collectors.groupingBy(TeachingResource::getChapterId));
    }
    
    @Override
    public List<TeachingResource> getResourcesByChapter(Long courseId, Long chapterId) {
        if (courseId == null) {
            throw new BusinessException("课程ID不能为空");
        }
        if (chapterId == null) {
            throw new BusinessException("章节ID不能为空");
        }
        
        return teachingResourceMapper.selectByCourseAndChapter(courseId, chapterId);
    }
    
    @Override
    public boolean deleteResource(Long resourceId, Long operatorId) {
        if (resourceId == null) {
            throw new BusinessException("资源ID不能为空");
        }
        
        TeachingResource resource = teachingResourceMapper.selectById(resourceId);
        if (resource == null) {
            return false;
        }
        
        // TODO: 检查操作权限
        int deleted = teachingResourceMapper.deleteById(resourceId);
        return deleted > 0;
    }
    
    @Override
    public LearningProgress updateProgress(Long resourceId, Long studentId, Double progress, Integer position) {
        if (resourceId == null) {
            throw new BusinessException("资源ID不能为空");
        }
        if (studentId == null) {
            throw new BusinessException("学生ID不能为空");
        }
        
        LearningProgress learningProgress = new LearningProgress();
        learningProgress.setResourceId(resourceId);
        learningProgress.setStudentId(studentId);
        learningProgress.setProgress(progress.doubleValue());
        learningProgress.setPosition(position);
        learningProgress.setLastAccessedAt(LocalDateTime.now());
        
        teachingResourceMapper.insertOrUpdateProgress(learningProgress);
        return learningProgress;
    }
    
    @Override
    public LearningProgress getProgress(Long resourceId, Long studentId) {
        if (resourceId == null) {
            throw new BusinessException("资源ID不能为空");
        }
        if (studentId == null) {
            throw new BusinessException("学生ID不能为空");
        }
        
        return teachingResourceMapper.selectProgress(resourceId, studentId);
    }
    
    @Override
    public String getSignedResourceUrl(Long resourceId) {
        // TODO: 实现签名URL生成逻辑
        return "signed_url_" + resourceId;
    }
    
    @Override
    public List<ResourceProgressDTO> getCourseResourcesWithProgress(Long courseId, Long studentId, Long chapterId) {
        if (courseId == null) {
            throw new BusinessException("课程ID不能为空");
        }
        if (studentId == null) {
            throw new BusinessException("学生ID不能为空");
        }
        if (chapterId == null) {
            throw new BusinessException("章节ID不能为空");
        }
        
        return teachingResourceMapper.selectResourcesWithProgress(courseId, chapterId, studentId);
    }
    
    @Override
    public TeachingResource updateResourceDuration(Long resourceId, Integer duration) {
        if (resourceId == null) {
            throw new BusinessException("资源ID不能为空");
        }
        if (duration == null || duration < 0) {
            throw new BusinessException("视频时长不能为负数");
        }
        
        int updated = teachingResourceMapper.updateDuration(resourceId, duration);
        if (updated == 0) {
            throw new BusinessException("资源不存在");
        }
        
        return teachingResourceMapper.selectById(resourceId);
    }
    
    @Override
    public void syncToAIKnowledgeBase(MultipartFile file, Long resourceId) {
        // TODO: 实现AI知识库同步逻辑
        // 这里只是占位符，实际应该调用AI服务
    }
}
