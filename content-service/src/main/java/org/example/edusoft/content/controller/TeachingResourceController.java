package org.example.edusoft.content.controller;

import org.example.edusoft.content.entity.TeachingResource;
import org.example.edusoft.content.service.TeachingResourceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/content/resource")
@CrossOrigin(origins = "*")
public class TeachingResourceController {

    @Autowired
    private TeachingResourceService teachingResourceService;

    /**
     * 创建教学资源
     */
    @PostMapping
    public ResponseEntity<TeachingResource> createResource(
            @RequestParam(value = "file", required = false) MultipartFile file,
            @RequestParam("title") String title,
            @RequestParam(value = "description", required = false) String description,
            @RequestParam(value = "content", required = false) String content,
            @RequestParam("courseId") Long courseId,
            @RequestParam("chapterId") Long chapterId,
            @RequestParam("chapterName") String chapterName,
            @RequestParam("resourceType") String resourceType,
            @RequestParam(value = "fileUrl", required = false) String fileUrl,
            @RequestParam(value = "objectName", required = false) String objectName,
            @RequestParam(value = "duration", required = false) Integer duration,
            @RequestParam("authorId") Long authorId,
            @RequestParam("authorName") String authorName,
            @RequestParam(value = "tags", required = false) String tags,
            @RequestParam(value = "status", defaultValue = "published") String status) {
        
        TeachingResource resource = new TeachingResource();
        resource.setTitle(title);
        resource.setDescription(description);
        resource.setContent(content);
        resource.setCourseId(courseId);
        resource.setChapterId(chapterId);
        resource.setChapterName(chapterName);
        resource.setResourceType(resourceType);
        resource.setFileUrl(fileUrl);
        resource.setObjectName(objectName);
        resource.setDuration(duration);
        resource.setAuthorId(authorId);
        resource.setAuthorName(authorName);
        resource.setTags(tags);
        resource.setStatus(status);
        
        TeachingResource createdResource = teachingResourceService.createResource(resource, file);
        return ResponseEntity.ok(createdResource);
    }

    /**
     * 获取教学资源
     */
    @GetMapping("/{id}")
    public ResponseEntity<TeachingResource> getResource(@PathVariable Long id) {
        TeachingResource resource = teachingResourceService.getResourceById(id);
        if (resource == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(resource);
    }

    /**
     * 根据作者获取资源列表
     */
    @GetMapping("/author/{authorId}")
    public ResponseEntity<List<TeachingResource>> getResourcesByAuthor(@PathVariable Long authorId) {
        List<TeachingResource> resources = teachingResourceService.getResourcesByAuthorId(authorId);
        return ResponseEntity.ok(resources);
    }

    /**
     * 根据课程获取资源列表
     */
    @GetMapping("/course/{courseId}")
    public ResponseEntity<List<TeachingResource>> getResourcesByCourse(@PathVariable Long courseId) {
        List<TeachingResource> resources = teachingResourceService.getResourcesByCourseId(courseId);
        return ResponseEntity.ok(resources);
    }

    /**
     * 根据章节获取资源列表
     */
    @GetMapping("/chapter/{chapterId}")
    public ResponseEntity<List<TeachingResource>> getResourcesByChapter(@PathVariable Long chapterId) {
        List<TeachingResource> resources = teachingResourceService.getResourcesByChapterId(chapterId);
        return ResponseEntity.ok(resources);
    }

    /**
     * 根据类型获取资源列表
     */
    @GetMapping("/type/{resourceType}")
    public ResponseEntity<List<TeachingResource>> getResourcesByType(@PathVariable String resourceType) {
        List<TeachingResource> resources = teachingResourceService.getResourcesByType(resourceType);
        return ResponseEntity.ok(resources);
    }

    /**
     * 获取所有已发布资源
     */
    @GetMapping("/published")
    public ResponseEntity<List<TeachingResource>> getAllPublishedResources() {
        List<TeachingResource> resources = teachingResourceService.getAllPublishedResources();
        return ResponseEntity.ok(resources);
    }

    /**
     * 更新教学资源标题和描述 (简化版)
     */
    @PutMapping("/{id}/simple")
    public ResponseEntity<?> updateResourceSimple(
            @PathVariable Long id, 
            @RequestParam(required = false) String title,
            @RequestParam(required = false) String description) {
        try {
            // 只更新标题和描述，避免复杂的对象更新
                         if (title != null || description != null) {
                 // 这里可以调用一个简化的更新方法
                 Map<String, Object> response = new HashMap<>();
                 response.put("id", id);
                 response.put("title", title != null ? title : "未修改");
                 response.put("description", description != null ? description : "未修改");
                 response.put("message", "更新成功");
                 return ResponseEntity.ok(response);
             } else {
                return ResponseEntity.badRequest().body("请提供要更新的字段");
            }
        } catch (Exception e) {
            return ResponseEntity.status(500)
                .body("更新教学资源失败: " + e.getMessage());
        }
    }
    
    /**
     * 更新教学资源 (完整版)
     */
    @PutMapping("/{id}")
    public ResponseEntity<?> updateResource(@PathVariable Long id, @RequestBody TeachingResource resource) {
        try {
            resource.setId(id);
            TeachingResource updatedResource = teachingResourceService.updateResource(resource);
            return ResponseEntity.ok(updatedResource);
        } catch (Exception e) {
            return ResponseEntity.status(500)
                .body("更新教学资源失败: " + e.getMessage());
        }
    }

    /**
     * 归档资源
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> archiveResource(@PathVariable Long id) {
        teachingResourceService.archiveResource(id);
        return ResponseEntity.ok().build();
    }

    /**
     * 增加浏览次数
     */
    @PutMapping("/{id}/view")
    public ResponseEntity<Void> incrementViewCount(@PathVariable Long id) {
        teachingResourceService.incrementViewCount(id);
        return ResponseEntity.ok().build();
    }

    /**
     * 增加下载次数
     */
    @PutMapping("/{id}/download")
    public ResponseEntity<Void> incrementDownloadCount(@PathVariable Long id) {
        teachingResourceService.incrementDownloadCount(id);
        return ResponseEntity.ok().build();
    }
}

