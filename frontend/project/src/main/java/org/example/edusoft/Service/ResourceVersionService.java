package org.example.edusoft.service;

import org.example.edusoft.entity.ResourceVersion;
import java.util.List;

public interface ResourceVersionService {
    ResourceVersion findById(Long id);
    List<ResourceVersion> findByResourceId(Long resourceId);
    ResourceVersion findByResourceAndVersion(Long resourceId, Integer version);
    ResourceVersion createVersion(ResourceVersion version);
    void deleteVersion(Long id);
} 