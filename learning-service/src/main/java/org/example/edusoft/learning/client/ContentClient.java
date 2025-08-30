package org.example.edusoft.learning.client;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

/**
 * 内容服务客户端
 * 负责调用content-service的相关接口
 */
@Component
public class ContentClient extends BaseServiceClient {
    
    @Value("${service.content.url:http://localhost:8084}")
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
    public List<Map<String, Object>> getResourcesByCourseId(Long courseId) {
        if (courseId == null) {
            throw new IllegalArgumentException("课程ID不能为空");
        }
        return get("/api/course/" + courseId + "/resources", List.class);
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
     * 批量获取资源信息
     */
    public List<Map<String, Object>> getResourcesByIds(String resourceIds) {
        if (resourceIds == null || resourceIds.trim().isEmpty()) {
            throw new IllegalArgumentException("资源ID列表不能为空");
        }
        return get("/api/resource/batch?ids=" + resourceIds, List.class);
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
}
