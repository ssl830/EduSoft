package org.example.edusoft.service.impl;

import org.example.edusoft.entity.ResourceVersion;
import org.example.edusoft.mapper.ResourceVersionMapper;
import org.example.edusoft.service.ResourceVersionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class ResourceVersionServiceImpl implements ResourceVersionService {
    @Autowired
    private ResourceVersionMapper resourceVersionMapper;

    @Override
    public ResourceVersion findById(Long id) {
        return resourceVersionMapper.findById(id);
    }

    @Override
    public List<ResourceVersion> findByResourceId(Long resourceId) {
        return resourceVersionMapper.findByResourceId(resourceId);
    }

    @Override
    public ResourceVersion findByResourceAndVersion(Long resourceId, Integer version) {
        return resourceVersionMapper.findByResourceAndVersion(resourceId, version);
    }

    @Override
    @Transactional
    public ResourceVersion createVersion(ResourceVersion version) {
        resourceVersionMapper.insert(version);
        return version;
    }

    @Override
    @Transactional
    public void deleteVersion(Long id) {
        resourceVersionMapper.deleteById(id);
    }
} 