package org.example.edusoft.controller.resource;

import org.example.edusoft.common.domain.Result;
import org.example.edusoft.entity.resource.TeachingResource;
import org.example.edusoft.entity.resource.LearningProgress;
import org.example.edusoft.entity.resource.ResourceProgressDTO;
import org.example.edusoft.entity.resource.ChapterResourceRequest;
import org.example.edusoft.service.resource.TeachingResourceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.example.edusoft.common.exception.BusinessException;
import org.springframework.http.ResponseEntity;
import org.springframework.http.MediaType;

import java.util.List;
import java.util.Map;

@Data
class ProgressUpdateRequest {
    @NotNull(message = "资源ID不能为空")
    private Long resourceId;
    
    @NotNull(message = "学生ID不能为空")
    private Long studentId;
    
    @NotNull(message = "进度不能为空")
    private Integer progress;
    
    @NotNull(message = "播放位置不能为空")
    private Integer position;
}

/**
 * 教学资源控制器
 */
@RestController
@RequestMapping("/api/resources")
public class TeachingResourceController {

    private static final Logger log = LoggerFactory.getLogger(TeachingResourceController.class);

    @Autowired
    private TeachingResourceService resourceService;

    /**
     * 上传教学资源
     */
    @PostMapping("/upload") 
    public Result<TeachingResource> uploadResource(
            @RequestParam("file") MultipartFile file,
            @RequestParam("courseId") Long courseId,
            @RequestParam("chapterId") Long chapterId,
            @RequestParam("chapterName") String chapterName,
            @RequestParam("title") String title,
            @RequestParam("description") String description,
            @RequestParam("createdBy") Long createdBy) {
        try {
            // 文件大小检查
            if (file.isEmpty()) {
                return Result.error("上传文件不能为空");
            }
            
            // 检查文件大小 (500MB)
            long maxSize = 500 * 1024 * 1024L;
            if (file.getSize() > maxSize) {
                return Result.error("文件大小不能超过500MB");
            }
            
            // 检查文件类型
            String contentType = file.getContentType();
            if (contentType == null || !contentType.startsWith("video/")) {
                return Result.error("请上传视频文件");
            }
            
            log.info("开始上传文件: {}, 大小: {} bytes", file.getOriginalFilename(), file.getSize());
            
            TeachingResource resource = resourceService.uploadResource(
                file, courseId, chapterId, chapterName, title, description, createdBy);
                
            log.info("文件上传成功, 资源ID: {}", resource.getId());
            
            // 课件上传成功后，自动同步到AI知识库
            try {
                resourceService.syncToAIKnowledgeBase(file, resource.getId());
            } catch (Exception e) {
                log.warn("同步到AI知识库失败: {}", e.getMessage());
                // 不影响主流程，只记录警告
            }
            
            return Result.ok(resource, "资源上传成功");
        } catch (Exception e) {
            log.error("资源上传失败", e);
            return Result.error("资源上传失败：" + e.getMessage());
        }
    }

    /**
     * 获取教学资源详情
     */
    @GetMapping("/{resourceId}")
    public Result<TeachingResource> getResource(@PathVariable Long resourceId) {
        try {
            TeachingResource resource = resourceService.getResource(resourceId);
            if (resource == null) {
                return Result.error("资源不存在");
            }
            return Result.ok(resource, "获取资源成功");
        } catch (Exception e) {
            return Result.error("获取资源失败：" + e.getMessage());
        }
    }

    /**
     * 获取课程的所有教学资源（按章节分组）
     */
    @GetMapping("/list/{courseId}")
    public Result<Map<Long, List<TeachingResource>>> getResourcesByCourse(@PathVariable Long courseId) {
        try {
            Map<Long, List<TeachingResource>> resources = resourceService.getResourcesByCourse(courseId);
            return Result.ok(resources, "获取课程资源列表成功");
        } catch (Exception e) {
            return Result.error("获取课程资源列表失败：" + e.getMessage());
        }
    }

    /**
     * 获取章节的教学资源
     */
    @GetMapping("/chapter/{courseId}/{chapterId}")
    public Result<List<TeachingResource>> getResourcesByChapter(
            @PathVariable Long courseId,
            @PathVariable Long chapterId) {
        try {
            List<TeachingResource> resources = resourceService.getResourcesByChapter(courseId, chapterId);
            return Result.ok(resources, "获取章节资源列表成功");
        } catch (Exception e) {
            return Result.error("获取章节资源列表失败：" + e.getMessage());
        }
    } 

    /**
     * 删除教学资源
     */
    @DeleteMapping("/{resourceId}")
    public Result<Void> deleteResource(
            @PathVariable Long resourceId,
            @RequestParam("operatorId") Long operatorId) {
        try {
            boolean success = resourceService.deleteResource(resourceId, operatorId);
            if (success) {
                return Result.ok(null, "资源删除成功");
            } else {
                return Result.error("资源不存在或删除失败");
            }
        } catch (Exception e) {
            return Result.error("删除资源失败：" + e.getMessage());
        }
    }

    /**
     * 更新学习进度
     */
    @PostMapping("/progress")
    public Result<LearningProgress> updateProgress(@Valid @RequestBody ProgressUpdateRequest request) {
        try {
            // 先检查资源是否存在
            TeachingResource resource = resourceService.getResource(request.getResourceId());
            if (resource == null) {
                return Result.error("教学资源不存在，ID: " + request.getResourceId());
            }

            LearningProgress learningProgress = resourceService.updateProgress(
                request.getResourceId(),
                request.getStudentId(),
                request.getProgress(),
                request.getPosition()
            );
            return Result.ok(learningProgress, "更新学习进度成功");
        } catch (BusinessException e) {
            return Result.error(e.getMessage());
        } catch (Exception e) {
            log.error("更新学习进度失败", e);
            return Result.error("更新学习进度失败：" + e.getMessage());
        }
    }

    /**
     * 获取学习进度
     */
    @GetMapping("/progress/{resourceId}/{studentId}")
    public Result<LearningProgress> getProgress(
            @PathVariable Long resourceId,
            @PathVariable Long studentId) {
        try {
            LearningProgress progress = resourceService.getProgress(resourceId, studentId);
            if (progress == null) {
                return Result.error("未找到学习进度记录");
            }
            return Result.ok(progress, "获取学习进度成功");
        } catch (Exception e) {
            return Result.error("获取学习进度失败：" + e.getMessage());
        }
    }
 
    /**
     * 获取资源访问URL（带签名的临时访问URL，有效期1小时）
     */
    @GetMapping("/url/{resourceId}")
    public Result<String> getResourceUrl(@PathVariable Long resourceId) {
        try {
            TeachingResource resource = resourceService.getResource(resourceId);
            if (resource == null) {
                return Result.error("资源不存在");
            }

            String url = resourceService.getSignedResourceUrl(resourceId);
            if (url == null) {
                return Result.error("获取资源访问链接失败");
            }
            return Result.ok(url, "获取资源访问链接成功（有效期1小时）");
        } catch (BusinessException e) {
            return Result.error(e.getMessage());
        } catch (Exception e) {
            log.error("获取资源访问链接失败", e);
            return Result.error("获取资源访问链接失败：" + e.getMessage());
        }
    }

    /**
     * 获取课程资源及学习进度信息
     * @param courseId 课程ID
     * @param request 请求参数（包含学生ID和章节ID）
     * @return 资源及学习进度信息列表
     */
    @PostMapping(value = "/chapter/{courseId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Result<List<ResourceProgressDTO>>> getCourseResourcesWithProgress(
            @PathVariable Long courseId,
            @Valid @RequestBody ChapterResourceRequest request) {
        try {
            List<ResourceProgressDTO> resources = resourceService.getCourseResourcesWithProgress(
                courseId, request.getStudentId(), request.getChapterId());
            System.out.println(resources + "resources");    
            return ResponseEntity.ok(Result.ok(resources, "获取课程资源及进度信息成功"));
        } catch (Exception e) {
            log.error("获取课程资源及进度信息失败", e);
            return ResponseEntity.ok(Result.error("获取课程资源及进度信息失败：" + e.getMessage()));
        }
    }

    /**
     * 更新资源时长
     */
    @PutMapping("/{resourceId}/duration")
    public Result<TeachingResource> updateResourceDuration(
            @PathVariable Long resourceId,
            @RequestBody Map<String, Integer> request) {
        try {
            Integer duration = request.get("duration");
            if (duration == null) {
                return Result.error("视频时长不能为空");
            }

            TeachingResource resource = resourceService.updateResourceDuration(resourceId, duration);
            return Result.ok(resource, "更新视频时长成功");
        } catch (Exception e) {
            return Result.error("更新视频时长失败：" + e.getMessage());
        }
    }
} 