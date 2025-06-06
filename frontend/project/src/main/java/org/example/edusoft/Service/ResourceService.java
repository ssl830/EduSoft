package org.example.edusoft.service;

import org.example.edusoft.entity.Resource;
import java.util.List;

public interface ResourceService {
    Resource findById(Long id);
    List<Resource> findByCourseId(Long courseId);
    List<Resource> findBySectionId(Long sectionId);
    List<Resource> findByUploaderId(Long uploaderId);
    Resource createResource(Resource resource);
    Resource updateResource(Resource resource);
    void deleteResource(Long id);
} 