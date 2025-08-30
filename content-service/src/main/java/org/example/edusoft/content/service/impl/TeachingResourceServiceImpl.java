package org.example.edusoft.content.service.impl;

import org.example.edusoft.content.entity.TeachingResource;
import org.example.edusoft.content.mapper.TeachingResourceMapper;
import org.example.edusoft.content.service.TeachingResourceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Service
public class TeachingResourceServiceImpl implements TeachingResourceService {

    @Autowired
    private TeachingResourceMapper teachingResourceMapper;

    @Override
    public TeachingResource createResource(TeachingResource resource, MultipartFile file) {
        // 如果有文件上传，处理文件
        if (file != null && !file.isEmpty()) {
            // 这里可以调用文件服务来处理文件上传
            // 暂时简单处理
            resource.setFileUrl("/resource/" + file.getOriginalFilename());
            resource.setObjectName(file.getOriginalFilename());
        }
        
        // 设置默认值
        if (resource.getViewCount() == null) {
            resource.setViewCount(0);
        }
        if (resource.getDownloadCount() == null) {
            resource.setDownloadCount(0);
        }
        if (resource.getStatus() == null) {
            resource.setStatus("published");
        }
        
        teachingResourceMapper.insert(resource);
        return resource;
    }

    @Override
    public TeachingResource getResourceById(Long id) {
        return teachingResourceMapper.findById(id);
    }

    @Override
    public List<TeachingResource> getResourcesByAuthorId(Long authorId) {
        return teachingResourceMapper.findByAuthorId(authorId);
    }

    @Override
    public List<TeachingResource> getResourcesByCourseId(Long courseId) {
        return teachingResourceMapper.findByCourseId(courseId);
    }

    @Override
    public List<TeachingResource> getResourcesByChapterId(Long chapterId) {
        return teachingResourceMapper.findByChapterId(chapterId);
    }

    @Override
    public List<TeachingResource> getResourcesByType(String resourceType) {
        return teachingResourceMapper.findByResourceType(resourceType);
    }

    @Override
    public List<TeachingResource> getAllPublishedResources() {
        return teachingResourceMapper.findAllPublished();
    }

    @Override
    public TeachingResource updateResource(TeachingResource resource) {
        // 先获取现有资源
        TeachingResource existingResource = teachingResourceMapper.findById(resource.getId());
        if (existingResource == null) {
            throw new RuntimeException("教学资源不存在，ID: " + resource.getId());
        }
        
        // 只更新非null字段，保留原有值
        if (resource.getTitle() != null) {
            existingResource.setTitle(resource.getTitle());
        }
        if (resource.getDescription() != null) {
            existingResource.setDescription(resource.getDescription());
        }
        if (resource.getContent() != null) {
            existingResource.setContent(resource.getContent());
        }
        if (resource.getCourseId() != null) {
            existingResource.setCourseId(resource.getCourseId());
        }
        if (resource.getChapterId() != null) {
            existingResource.setChapterId(resource.getChapterId());
        }
        if (resource.getChapterName() != null) {
            existingResource.setChapterName(resource.getChapterName());
        }
        if (resource.getResourceType() != null) {
            existingResource.setResourceType(resource.getResourceType());
        }
        if (resource.getFileUrl() != null) {
            existingResource.setFileUrl(resource.getFileUrl());
        }
        if (resource.getObjectName() != null) {
            existingResource.setObjectName(resource.getObjectName());
        }
        if (resource.getDuration() != null) {
            existingResource.setDuration(resource.getDuration());
        }
        if (resource.getTags() != null) {
            existingResource.setTags(resource.getTags());
        }
        if (resource.getStatus() != null) {
            existingResource.setStatus(resource.getStatus());
        }
        
        // 设置更新时间
        existingResource.setUpdatedAt(java.time.LocalDateTime.now());
        
        // 执行更新
        teachingResourceMapper.update(existingResource);
        return existingResource;
    }

    @Override
    public void incrementViewCount(Long id) {
        teachingResourceMapper.incrementViewCount(id);
    }

    @Override
    public void incrementDownloadCount(Long id) {
        teachingResourceMapper.incrementDownloadCount(id);
    }

    @Override
    public void archiveResource(Long id) {
        teachingResourceMapper.archiveById(id);
    }
}

