package org.example.edusoft.learning.client;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.time.LocalDate;
import java.util.HashMap;

/**
 * 内容服务客户端
 * 负责调用content-service的相关接口
 */
@Component
public class ContentClient extends BaseServiceClient {
    
    private static final org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(ContentClient.class);
    
    @Value("${service.content.url:http://localhost:8083}")
    private String contentServiceUrl;
    
    @Override
    protected String getServiceName() {
        return "content-service";
    }
    
    @Override
    protected String getBaseUrl() {
        return contentServiceUrl;
    }
    
    /**
     * 根据资源ID获取资源信息
     */
    public Map<String, Object> getResourceById(Long resourceId) {
        if (resourceId == null) {
            throw new IllegalArgumentException("资源ID不能为空");
        }
        return getForMap("/api/resource/" + resourceId);
    }

    /**
     * 获取课程下的所有资源
     */
    public List<Map<String, Object>> getResourcesByCourseId(Long courseId, Long userId) {
        if (courseId == null || userId == null) {
            throw new IllegalArgumentException("课程ID和用户ID不能为空");
        }
        // 构造请求体
        Map<String, Object> requestBody = Map.of(
                "courseId", courseId,
                "userId", userId
        );
        // 用POST请求
        Map<String, Object> result = post("/api/resources/" + courseId + "/filelist", requestBody, Map.class);
        Object data = result != null ? result.get("data") : null;
        if (data instanceof List) {
            return (List<Map<String, Object>>) data;
        } else {
            return java.util.Collections.emptyList();
        }
    }
    
    /**
     * 获取课程下的所有资源
     */
    public List<Map<String, Object>> getResourcesByCourseId2(Long courseId) {
        if (courseId == null) {
            throw new IllegalArgumentException("课程ID不能为空");
        }
        // 修正：先获取Map，再取data字段
        Map<String, Object> result = getForMap("/api/resources/list/" + courseId);
        Object data = result != null ? result.get("data") : null;
        // 兼容data为null或不是List的情况
        if (data instanceof Map) {
            // 按章节分组，合并所有章节资源为一个List
            List<Map<String, Object>> all = new java.util.ArrayList<>();
            ((Map<?, ?>) data).values().forEach(v -> {
                if (v instanceof List) {
                    all.addAll((List<Map<String, Object>>) v);
                }
            });
            return all;
        } else if (data instanceof List) {
            return (List<Map<String, Object>>) data;
        } else {
            return java.util.Collections.emptyList();
        }
    }
    
    /**
     * 获取章节下的所有资源
     */
    public List<Map<String, Object>> getResourcesBySectionId(Long sectionId) {
        if (sectionId == null) {
            throw new IllegalArgumentException("章节ID不能为空");
        }
        return get("/api/section/" + sectionId + "/resources", List.class);
    }
    
    /**
     * 获取资源的播放信息
     */
    public Map<String, Object> getResourcePlayInfo(Long resourceId, Long userId) {
        if (resourceId == null || userId == null) {
            throw new IllegalArgumentException("资源ID和用户ID不能为空");
        }
        return getForMap("/api/resource/" + resourceId + "/play?userId=" + userId);
    }
    
    /**
     * 更新资源播放进度
     */
    public void updateResourceProgress(Long resourceId, Long userId, int progress, int position) {
        if (resourceId == null || userId == null) {
            throw new IllegalArgumentException("资源ID和用户ID不能为空");
        }
        
        Map<String, Object> requestBody = Map.of(
            "resourceId", resourceId,
            "userId", userId,
            "progress", progress,
            "position", position
        );
        
        post("/api/resource/progress", requestBody, Void.class);
    }
    
    /**
     * 获取用户的学习统计信息
     */
    public Map<String, Object> getUserLearningStats(Long userId) {
        if (userId == null) {
            throw new IllegalArgumentException("用户ID不能为空");
        }
        return getForMap("/api/user/" + userId + "/learning-stats");
    }
    
    /**
     * 批量获取资源信息，完成微服务化改造
     */
    public List<Map<String, Object>> getResourcesByIds(String resourceIds) {
        if (resourceIds == null || resourceIds.trim().isEmpty()) {
            throw new IllegalArgumentException("资源ID列表不能为空");
        }
        return get("/api/content/resource/batch?ids=" + resourceIds, List.class);
    }
    
    /**
     * 验证用户是否可以访问指定资源
     */
    public boolean canUserAccessResource(Long userId, Long resourceId) {
        if (userId == null || resourceId == null) {
            throw new IllegalArgumentException("用户ID和资源ID不能为空");
        }
        try {
            Map<String, Object> result = getForMap("/api/resource/" + resourceId + "/access?userId=" + userId);
            return result != null && Boolean.TRUE.equals(result.get("canAccess"));
        } catch (Exception ex) {
            return false;
        }
    }

    /**
     * 新建通知
     */
    public Map<String, Object> createNotification(Map<String, Object> notification) {
        if (notification == null || notification.isEmpty()) {
            throw new IllegalArgumentException("通知内容不能为空");
        }
        return post("/api/content/notification", notification, Map.class);
    }

    /**
     * 统计教师创建的作业数量
     */
    public int countTeacherHomework(LocalDate start, LocalDate end, List<Long> teacherIds) {
        try {
            Map<String, Object> params = new HashMap<>();
            params.put("start", start);
            params.put("end", end);
            params.put("teacherIds", teacherIds);
            
            Map<String, Object> result = post("/api/homework/stats/teacher/count", params, Map.class);
            
            if (result != null && result.get("count") instanceof Number) {
                return ((Number) result.get("count")).intValue();
            }
            return 0;
        } catch (Exception e) {
            log.error("统计教师作业数量失败: {}", e.getMessage());
            return 0;
        }
    }

    /**
     * 统计学生提交的作业数量
     */
    public int countStudentHomework(LocalDate start, LocalDate end, List<Long> studentIds) {
        try {
            Map<String, Object> params = new HashMap<>();
            params.put("start", start);
            params.put("end", end);
            params.put("studentIds", studentIds);
            
            Map<String, Object> result = post("/api/homework/stats/student/submissions", params, Map.class);
            
            if (result != null && result.get("count") instanceof Number) {
                return ((Number) result.get("count")).intValue();
            }
            return 0;
        } catch (Exception e) {
            log.error("统计学生作业数量失败: {}", e.getMessage());
            return 0;
        }
    }
}
