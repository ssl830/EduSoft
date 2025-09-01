package org.example.edusoft.learning.client;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 课程服务客户端
 * 负责调用course-service的相关接口
 */
@Component
public class CourseClient extends BaseServiceClient {
    private static final Logger logger = LoggerFactory.getLogger(CourseClient.class);

    @Value("${service.course.url}")
    private String courseServiceUrl;

    @Override
    protected String getServiceName() {
        return "course-service";
    }

    @Override
    protected String getBaseUrl() {
        return courseServiceUrl;
    }

    /**
     * 获取当前请求的token
     */
    private String getCurrentToken() {
        return org.springframework.web.context.request.RequestContextHolder
            .getRequestAttributes() != null ? 
            ((org.springframework.web.context.request.ServletRequestAttributes) org.springframework.web.context.request.RequestContextHolder
                .getRequestAttributes()).getRequest().getHeader("satoken") : null;
    }

    /**
     * 根据课程ID获取课程信息
     */
    public Map<String, Object> getCourseById(Long courseId) {
        if (courseId == null) {
            throw new IllegalArgumentException("课程ID不能为空");
        }
        String token = getCurrentToken();
        org.springframework.http.HttpHeaders headers = new org.springframework.http.HttpHeaders();
        if (token != null) {
            headers.set("satoken", token);
        }
        org.springframework.http.HttpEntity<Void> entity = new org.springframework.http.HttpEntity<>(headers);
        String url = getBaseUrl() + "/api/courses/" + courseId;
        org.springframework.http.ResponseEntity<Map> response = restTemplate.exchange(
            url, 
            org.springframework.http.HttpMethod.GET, 
            entity, 
            Map.class
        );
        return response.getBody();
    }

    /**
     * 根据班级ID获取班级信息
     */
    public Map<String, Object> getClassById(Long classId) {
        if (classId == null) {
            throw new IllegalArgumentException("班级ID不能为空");
        }
        return getForMap("/api/classes/" + classId);
    }

    /**
     * 根据用户ID和课程ID获取班级信息，完成微服务化改造
     */
    public List<Map<String, Object>> getClassesByUserIdAndCourseIds(Long userId, List<Long> courseIds) {
        if (userId == null || courseIds == null || courseIds.isEmpty()) {
            throw new IllegalArgumentException("用户ID和课程ID列表不能为空");
        }
        return post("/api/classes/{userId}/{courseId}", Map.of("userId", userId, "courseIds", courseIds), List.class);
    }

    /**
     * 根据章节ID获取章节信息
     */
    public Map<String, Object> getSectionById(Long sectionId) {
        if (sectionId == null) {
            throw new IllegalArgumentException("章节ID不能为空");
        }
        return getForMap("/api/course-sections/section/" + sectionId);

    }
    
    /**
     * 根据章节ID批量获取章节信息，完成微服务化改造
     */
    public List<Map<String, Object>> getSectionsByIds(String sectionIds) {
        if (sectionIds == null || sectionIds.trim().isEmpty()) {
            throw new IllegalArgumentException("章节ID列表不能为空");
        }
        String token = getCurrentToken();
        org.springframework.http.HttpHeaders headers = new org.springframework.http.HttpHeaders();
        if (token != null) {
            headers.set("satoken", token);
        }
        org.springframework.http.HttpEntity<Void> entity = new org.springframework.http.HttpEntity<>(headers);
        String url = getBaseUrl() + "/api/course-sections/batch?ids=" + sectionIds;
        org.springframework.http.ResponseEntity<List> response = restTemplate.exchange(
            url, 
            org.springframework.http.HttpMethod.GET, 
            entity, 
            List.class
        );
        return response.getBody();
    }

    /**
     * 获取课程下的所有章节，已完成微服务化改造
     */
    public List<Map<String, Object>> getSectionsByCourseId(Long courseId) {
        if (courseId == null) {
            throw new IllegalArgumentException("课程ID不能为空");
        }
        String token = getCurrentToken();
        logger.info("[CourseClient] 获取课程章节列表，课程ID: {}, token: {}", courseId, token);
        
        org.springframework.http.HttpHeaders headers = new org.springframework.http.HttpHeaders();
        if (token != null) {
            headers.set("satoken", token);
        }
        org.springframework.http.HttpEntity<Void> entity = new org.springframework.http.HttpEntity<>(headers);
        String url = getBaseUrl() + "/api/course-sections/course/" + courseId;
        
        try {
            logger.info("[CourseClient] 发送请求到: {}", url);
            org.springframework.http.ResponseEntity<List> response = restTemplate.exchange(
                url, 
                org.springframework.http.HttpMethod.GET, 
                entity, 
                List.class
            );
            List<Map<String, Object>> result = response.getBody();
            logger.info("[CourseClient] 获取课程章节列表成功，返回数据: {}", result);
            return result;
        } catch (Exception e) {
            logger.error("[CourseClient] 获取课程章节列表失败: {}", e.getMessage());
            throw e;
        }
    }

    /**
     * 获取班级下的所有学生
     */
    public List<Map<String, Object>> getStudentsByClassId(Long classId) {
        if (classId == null) {
            throw new IllegalArgumentException("班级ID不能为空");
        }
        Object resp = get("/api/classes/" + classId + "/users", Object.class);
        // 兼容Result对象包裹
        if (resp instanceof Map) {
            Object dataObj = ((Map<?, ?>) resp).get("data");
            if (dataObj instanceof List) {
                return (List<Map<String, Object>>) dataObj;
            }
        }
        if (resp instanceof List) {
            return (List<Map<String, Object>>) resp;
        }
        return List.of();
    }

    /**
     * 验证用户是否可以访问指定课程
     */
    public boolean canUserAccessCourse(Long userId, Long courseId) {
        if (userId == null || courseId == null) {
            throw new IllegalArgumentException("用户ID和课程ID不能为空");
        }
        try {
            Map<String, Object> result = getForMap("/api/course/" + courseId + "/access?userId=" + userId);
            return result != null && Boolean.TRUE.equals(result.get("canAccess"));
        } catch (Exception ex) {
            return false;
        }
    }

    /**
     * 验证用户是否属于指定班级
     */
    public boolean isUserInClass(Long userId, Long classId) {
        if (userId == null || classId == null) {
            throw new IllegalArgumentException("用户ID和班级ID不能为空");
        }
        try {
            Map<String, Object> result = getForMap("/api/class/" + classId + "/member?userId=" + userId);
            return result != null && Boolean.TRUE.equals(result.get("isMember"));
        } catch (Exception ex) {
            return false;
        }
    }

    /**
     * 获取用户的所有课程
     */
    public List<Map<String, Object>> getUserCourses(Long userId) {
        if (userId == null) {
            throw new IllegalArgumentException("用户ID不能为空");
        }
        return get("/api/user/" + userId + "/courses", List.class);
    }

    /**
     * 批量获取课程信息，已完成微服务化改造
     */
    public List<Map<String, Object>> getCoursesByIds(String courseIds) {
        if (courseIds == null || courseIds.trim().isEmpty()) {
            throw new IllegalArgumentException("课程ID列表不能为空");
        }
        return get("/api/courses/batch?ids=" + courseIds, List.class);
    }

    /**
     * 批量获取课程信息（逐个调用 /api/courses/{id}）
     */
    public Map<Long, Map<String, Object>> getCoursesByIds(List<Long> courseIds) {
        Map<Long, Map<String, Object>> result = new HashMap<>();
        if (courseIds == null || courseIds.isEmpty()) {
            return result;
        }

        String token = getCurrentToken();
        org.springframework.http.HttpHeaders headers = new org.springframework.http.HttpHeaders();
        if (token != null) {
            headers.set("satoken", token);
        }
        org.springframework.http.HttpEntity<Void> entity = new org.springframework.http.HttpEntity<>(headers);

        for (Long courseId : courseIds) {
            try {
                String url = getBaseUrl() + "/api/courses/" + courseId;
                org.springframework.http.ResponseEntity<Map> response = restTemplate.exchange(
                    url, 
                    org.springframework.http.HttpMethod.GET, 
                    entity, 
                    Map.class
                );

                if (response.getBody() != null) {
                    Object dataObj = response.getBody().get("data");
                    if (dataObj instanceof Map) {
                        result.put(courseId, (Map<String, Object>) dataObj);
                    }
                }
            } catch (Exception ex) {
                logger.error("获取课程 {} 失败: {}", courseId, ex.getMessage());
                throw new RuntimeException("获取课程信息失败：" + ex.getMessage());
            }
        }
        return result;
    }

    /**
     * 批量获取练习信息
     */
    public Map<Long, Map<String, Object>> getPracticesByIds(List<Long> practiceIds) {
        Map<Long, Map<String, Object>> result = new HashMap<>();
        if (practiceIds == null || practiceIds.isEmpty()) {
            return result;
        }
        
        String ids = String.join(",", practiceIds.stream().map(String::valueOf).toList());
        try {
            List<Map<String, Object>> practices = get("/api/practices/batch?ids=" + ids, List.class);
            if (practices != null) {
                for (Map<String, Object> practice : practices) {
                    Long id = ((Number) practice.get("id")).longValue();
                    result.put(id, practice);
                }
            }
        } catch (Exception ex) {
            logger.error("批量获取练习信息失败: {}", ex.getMessage());
            throw new RuntimeException("获取练习信息失败：" + ex.getMessage());
        }
        return result;
    }

    /**
     * 批量获取课程信息（参考UserClient的fetchUserById实现，支持token传递，逐个调用course-service）
     * @param baseUrl 课程服务基础URL
     * @param token 认证token（支持satoken或Bearer前缀）
     * @param courseIds 课程ID列表
     * @return Map<课程ID, 课程详情Map>
     */
    public Map<Long, Map<String, Object>> getCoursesByIds(String baseUrl, String token, List<Long> courseIds) {
        Map<Long, Map<String, Object>> result = new HashMap<>();
        if (courseIds == null || courseIds.isEmpty()) {
            return result;
        }
        String pureToken = token == null ? null : token.replace("Bearer ", "");
        for (Long courseId : courseIds) {
            try {
                String url = baseUrl + "/api/courses/" + courseId;
                org.springframework.http.HttpHeaders headers = new org.springframework.http.HttpHeaders();
                if (pureToken != null && !pureToken.isEmpty()) {
                    headers.set("satoken", pureToken);
                    headers.set("Authorization", "Bearer " + pureToken);
                }
                org.springframework.http.HttpEntity<Void> entity = new org.springframework.http.HttpEntity<>(headers);
                org.springframework.http.ResponseEntity<Map> response = restTemplate.exchange(url, org.springframework.http.HttpMethod.GET, entity, Map.class);
                if (response.getBody() != null) {
                    Map<String, Object> body = response.getBody();
                    Object dataObj = body.get("data");
                    if (dataObj instanceof Map) {
                        result.put(courseId, (Map<String, Object>) dataObj);
                        continue;
                    }
                }
            } catch (Exception ex) {
                logger.warn("获取课程 {} 失败，使用模拟数据: {}", courseId, ex.getMessage());
            }
            // 模拟数据（降级）
            Map<String, Object> course = new HashMap<>();
            course.put("id", courseId);
            course.put("name", "模拟课程-" + courseId);
            course.put("description", "这是模拟课程描述-" + courseId);
            course.put("teacherId", courseId * 10 + 1);
            result.put(courseId, course);
        }
        return result;
    }
}
