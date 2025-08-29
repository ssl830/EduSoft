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
        teachingResourceMapper.update(resource);
        return resource;
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

