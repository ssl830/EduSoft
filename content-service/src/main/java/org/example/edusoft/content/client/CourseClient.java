package org.example.edusoft.content.client;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.example.edusoft.content.client.BaseServiceClient;

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

    @Value("${course.service.url}")
    private String courseServiceUrl;

    protected String getServiceName() {
        return "course-service";
    }

    protected String getBaseUrl() {
        return courseServiceUrl;
    }

    /**
     * 获取当前请求的token，兼容 satoken/Authorization/Cookie
     */
    private String getCurrentToken() {
        org.springframework.web.context.request.RequestAttributes attrs = org.springframework.web.context.request.RequestContextHolder.getRequestAttributes();
        if (attrs instanceof org.springframework.web.context.request.ServletRequestAttributes servlet) {
            jakarta.servlet.http.HttpServletRequest req = servlet.getRequest();
            String satoken = req.getHeader("satoken");
            if (satoken != null && !satoken.isEmpty()) return satoken;
            String cookie = req.getHeader("Cookie");
            if (cookie != null) {
                for (String part : cookie.split(";")) {
                    String p = part.trim();
                    if (p.startsWith("satoken=")) return p.substring("satoken=".length());
                }
            }
            String auth = req.getHeader("Authorization");
            if (auth != null && !auth.isEmpty()) return auth.replace("Bearer ", "");
        }
        return null;
    }

    /**
     * 根据班级ID获取所属课程的ID
     */
    public Long getCourseIdByClassId(Long classId) {
        if (classId == null) {
            throw new IllegalArgumentException("班级ID不能为空");
        }
        
        String token = getCurrentToken();
        org.springframework.http.HttpHeaders headers = new org.springframework.http.HttpHeaders();
        if (token != null) {
            headers.set("satoken", token);
            headers.set("Authorization", "Bearer " + token);
        }
        org.springframework.http.HttpEntity<Void> entity = new org.springframework.http.HttpEntity<>(headers);
        String url = getBaseUrl() + "/api/classes/" + classId;
        
        try {
            Map<String, Object> response = restTemplate.exchange(
                url,
                org.springframework.http.HttpMethod.GET,
                entity,
                Map.class
            ).getBody();

            if (response != null) {
                // 首先尝试从 data 字段获取
                if (response.containsKey("data")) {
                    Object data = response.get("data");
                    if (data instanceof Map) {
                        Map<?, ?> dataMap = (Map<?, ?>) data;
                        // 尝试从不同可能的字段名获取课程ID
                        Object courseIdObj = dataMap.get("courseId");
                        if (courseIdObj == null) courseIdObj = dataMap.get("course_id");
                        if (courseIdObj == null) {
                            Object courseObj = dataMap.get("course");
                            if (courseObj instanceof Map) {
                                courseIdObj = ((Map<?, ?>) courseObj).get("id");
                            }
                        }
                        
                        if (courseIdObj != null) {
                            if (courseIdObj instanceof Number) {
                                return ((Number) courseIdObj).longValue();
                            }
                            try {
                                return Long.valueOf(courseIdObj.toString());
                            } catch (NumberFormatException e) {
                                throw new RuntimeException("课程ID格式无效");
                            }
                        }
                    }
                }
                // 直接从根级别尝试获取
                Object courseIdObj = response.get("courseId");
                if (courseIdObj == null) courseIdObj = response.get("course_id");
                if (courseIdObj != null) {
                    if (courseIdObj instanceof Number) {
                        return ((Number) courseIdObj).longValue();
                    }
                    try {
                        return Long.valueOf(courseIdObj.toString());
                    } catch (NumberFormatException e) {
                        throw new RuntimeException("课程ID格式无效");
                    }
                }
            }
            throw new RuntimeException("获取课程ID失败：响应中没有找到课程ID");
        } catch (Exception e) {
            throw new RuntimeException("获取课程ID失败：" + e.getMessage(), e);
        }
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
            headers.set("Authorization", "Bearer " + token);
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
     * 根据课程ID获取课程信息（别名方法）
     */
    public Map<String, Object> getCourseInfo(Long courseId) {
        return getCourseById(courseId);
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
     * 根据班级ID获取班级信息（别名方法）
     */
    public Map<String, Object> getClassInfo(Long classId) {
        return getClassById(classId);
    }

    /**
     * 根据用户ID和课程ID获取班级ID
     */
    public Long getClassIdByUserIdAndCourseId(Long userId, Long courseId) {
        if (userId == null || courseId == null) {
            throw new IllegalArgumentException("用户ID和课程ID不能为空");
        }
        String token = getCurrentToken();
        org.springframework.http.HttpHeaders headers = new org.springframework.http.HttpHeaders();
        if (token != null) {
            headers.set("satoken", token);
        }
        org.springframework.http.HttpEntity<Void> entity = new org.springframework.http.HttpEntity<>(headers);
        String url = getBaseUrl() + "/api/classes/" + userId + "/" + courseId;
        org.springframework.http.ResponseEntity<Long> response = restTemplate.exchange(
            url,
            org.springframework.http.HttpMethod.GET,
            entity,
            Long.class
        );
        return response.getBody();
    }

    /**
     * 根据用户ID和课程ID列表批量获取班级信息（循环调用单个GET接口，收集结果）
     */
    public List<Map<String, Object>> getClassesByUserIdAndCourseIds(Long userId, List<Long> courseIds) {
        if (userId == null || courseIds == null || courseIds.isEmpty()) {
            throw new IllegalArgumentException("用户ID和课程ID列表不能为空");
        }
        String token = getCurrentToken();
        org.springframework.http.HttpHeaders headers = new org.springframework.http.HttpHeaders();
        if (token != null) {
            headers.set("satoken", token);
        }
        org.springframework.http.HttpEntity<Void> entity = new org.springframework.http.HttpEntity<>(headers);

        List<Map<String, Object>> result = new java.util.ArrayList<>();
        for (Long courseId : courseIds) {
            String url = getBaseUrl() + "/api/classes/" + userId + "/" + courseId;
            try {
                org.springframework.http.ResponseEntity<String> response = restTemplate.exchange(
                    url,
                    org.springframework.http.HttpMethod.GET,
                    entity,
                    String.class
                );
                // 假设返回的是JSON字符串，解析为Map
                String body = response.getBody();
                if (body != null && !body.isEmpty()) {
                    Map<String, Object> map = new com.fasterxml.jackson.databind.ObjectMapper().readValue(body, Map.class);
                    result.add(map);
                }
            } catch (Exception ex) {
                logger.warn("获取班级信息失败: userId={}, courseId={}, {}", userId, courseId, ex.getMessage());
            }
        }
        return result;
    }

    /**
     * 根据课程ID获取所有班级ID
     */
    public List<Long> getAllClassIdsByCourseId(Long courseId) throws JsonProcessingException {
        if (courseId == null) {
            throw new IllegalArgumentException("课程ID不能为空");
        }
        String token = getCurrentToken();
        org.springframework.http.HttpHeaders headers = new org.springframework.http.HttpHeaders();
        if (token != null) {
            headers.set("satoken", token);
        }
        org.springframework.http.HttpEntity<Void> entity = new org.springframework.http.HttpEntity<>(headers);
        String url = getBaseUrl() + "/api/classes/course/" + courseId;
        org.springframework.http.ResponseEntity<String> response = restTemplate.exchange(
            url,
            org.springframework.http.HttpMethod.GET,
            entity,
            String.class
        );
        String body = response.getBody();
        if (body != null && !body.isEmpty()) {
            // 兼容返回体为 { code, message, data: [...] }
            com.fasterxml.jackson.databind.ObjectMapper mapper = new com.fasterxml.jackson.databind.ObjectMapper();
            Map<String, Object> map = mapper.readValue(body, Map.class);
            Object dataObj = map.get("data");
            if (dataObj instanceof List<?> list) {
                List<Long> classIds = new java.util.ArrayList<>();
                for (Object item : list) {
                    if (item instanceof Map) {
                        Object idObj = ((Map<?, ?>) item).get("id");
                        if (idObj instanceof Number) {
                            classIds.add(((Number) idObj).longValue());
                        } else if (idObj != null) {
                            try {
                                classIds.add(Long.valueOf(idObj.toString()));
                            } catch (Exception ignore) {}
                        }
                    }
                }
                return classIds;
            }
        }
        return java.util.Collections.emptyList();
    }

    /**
     * 根据章节ID获取章节信息
     */
    public Map<String, Object> getSectionById(Long sectionId) {
        if (sectionId == null) {
            throw new IllegalArgumentException("章节ID不能为空");
        }
        String token = getCurrentToken();
        org.springframework.http.HttpHeaders headers = new org.springframework.http.HttpHeaders();
        if (token != null) {
            headers.set("satoken", token);
            headers.set("Authorization", "Bearer " + token);
        }
        org.springframework.http.HttpEntity<Void> entity = new org.springframework.http.HttpEntity<>(headers);
        String url = getBaseUrl() + "/api/courses/section/" + sectionId;
        logger.debug("[CourseClient] single section url: {}", url);
        org.springframework.http.ResponseEntity<Object> response = restTemplate.exchange(
            url,
            org.springframework.http.HttpMethod.GET,
            entity,
            Object.class
        );
        Object body = response.getBody();
        if (body instanceof java.util.Map<?, ?> map) {
            // unwrap { data: {...} } or return map directly if it is already the section
            Object data = map.get("data");
            if (data instanceof java.util.Map<?, ?> inner) {
                @SuppressWarnings("unchecked")
                Map<String, Object> res = (Map<String, Object>) inner;
                logger.debug("[CourseClient] single section parsed keys: {}", res.keySet());
                logger.info("[CourseClient] section {} body: {}", sectionId, res);
                return res;
            }
            @SuppressWarnings("unchecked")
            Map<String, Object> res = (Map<String, Object>) map;
            logger.debug("[CourseClient] single section (direct map) keys: {}", res.keySet());
            logger.info("[CourseClient] section {} body: {}", sectionId, res);
            return res;
        }
        logger.warn("[CourseClient] single section unexpected body: {}", String.valueOf(body));
        return java.util.Map.of();

    }
    
    /**
     * 根据章节ID批量获取章节信息（逐个调用 /api/courses/section/{id}，避免与单体路由冲突）
     */
    public List<Map<String, Object>> getSectionsByIds(String sectionIds) {
        if (sectionIds == null || sectionIds.trim().isEmpty()) {
            throw new IllegalArgumentException("章节ID列表不能为空");
        }
        List<Map<String, Object>> result = new java.util.ArrayList<>();
        String[] parts = sectionIds.split(",");
        for (String p : parts) {
            String trimmed = p.trim();
            if (trimmed.isEmpty()) continue;
            try {
                Long sid = Long.valueOf(trimmed);
                Map<String, Object> sec = getSectionById(sid);
                if (sec != null && !sec.isEmpty()) result.add(sec);
            } catch (Exception ex) {
                logger.warn("[CourseClient] 获取章节 {} 失败: {}", trimmed, ex.getMessage());
            }
        }
        return result;
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
            headers.set("Authorization", "Bearer " + token);
        }
        org.springframework.http.HttpEntity<Void> entity = new org.springframework.http.HttpEntity<>(headers);
        String url = getBaseUrl() + "/api/courses/" + courseId + "/sections";

        try {
            logger.info("[CourseClient] 发送请求到: {}", url);
            org.springframework.http.ResponseEntity<Object> response = restTemplate.exchange(
                url,
                org.springframework.http.HttpMethod.GET,
                entity,
                Object.class
            );
            Object body = response.getBody();
            if (body instanceof java.util.Map<?, ?> map) {
                Object data = map.get("data");
                if (data instanceof java.util.List<?> list) {
                    @SuppressWarnings("unchecked")
                    List<Map<String, Object>> res = (List<Map<String, Object>>) list;
                    logger.info("[CourseClient] 课程章节列表(data)条数: {}", res.size());
                    return res;
                }
            } else if (body instanceof java.util.List<?> list) {
                @SuppressWarnings("unchecked")
                List<Map<String, Object>> res = (List<Map<String, Object>>) list;
                logger.info("[CourseClient] 课程章节列表(直接List)条数: {}", res.size());
                return res;
            }
            logger.warn("[CourseClient] 课程章节列表返回体异常: {}", String.valueOf(body));
            return java.util.List.of();
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
        String token = getCurrentToken();
        org.springframework.http.HttpHeaders headers = new org.springframework.http.HttpHeaders();
        if (token != null) {
            // 同时设置 satoken 和 Authorization，兼容下游
            headers.set("satoken", token);
            headers.set("Authorization", "Bearer " + token);
        }
        org.springframework.http.HttpEntity<Void> entity = new org.springframework.http.HttpEntity<>(headers);
        String url = getBaseUrl() + "/api/courses/batch?ids=" + courseIds;

        // 使用 Object.class 接收，兼容 Result 包裹或直接 List 返回
        org.springframework.http.ResponseEntity<Object> response = restTemplate.exchange(
            url,
            org.springframework.http.HttpMethod.GET,
            entity,
            Object.class
        );

        Object body = response.getBody();
        if (body instanceof java.util.Map<?, ?> map) {
            Object data = map.get("data");
            if (data instanceof java.util.List<?> list) {
                // 安全转换
                @SuppressWarnings("unchecked")
                List<Map<String, Object>> res = (List<Map<String, Object>>) list;
                return res;
            }
        } else if (body instanceof java.util.List<?> list) {
            @SuppressWarnings("unchecked")
            List<Map<String, Object>> res = (List<Map<String, Object>>) list;
            return res;
        }
        return java.util.List.of();
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
