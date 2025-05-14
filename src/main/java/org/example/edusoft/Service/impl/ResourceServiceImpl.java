package org.example.edusoft.service.impl;

import org.example.edusoft.entity.Resource;
import org.example.edusoft.mapper.ResourceMapper;
import org.example.edusoft.service.ResourceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class ResourceServiceImpl implements ResourceService {
    @Autowired
    private ResourceMapper resourceMapper;

    @Override
    public Resource findById(Long id) {
        return resourceMapper.findById(id);
    }

    @Override
    public List<Resource> findByCourseId(Long courseId) {
        return resourceMapper.findByCourseId(courseId);
    }

    @Override
    public List<Resource> findBySectionId(Long sectionId) {
        return resourceMapper.findBySectionId(sectionId);
    }

    @Override
    public List<Resource> findByUploaderId(Long uploaderId) {
        return resourceMapper.findByUploaderId(uploaderId);
    }

    @Override
    @Transactional
    public Resource createResource(Resource resource) {
        resourceMapper.insert(resource);
        return resource;
    }

    @Override
    @Transactional
    public Resource updateResource(Resource resource) {
        resourceMapper.update(resource);
        return resource;
    }

    @Override
    @Transactional
    public void deleteResource(Long id) {
        resourceMapper.deleteById(id);
    }
} 