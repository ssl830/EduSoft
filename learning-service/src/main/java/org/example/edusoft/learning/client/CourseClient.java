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
    private static final Logger logger = LoggerFactory.getLogger(UserClient.class);

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
     * 根据课程ID获取课程信息
     */
    public Map<String, Object> getCourseById(Long courseId) {
        if (courseId == null) {
            throw new IllegalArgumentException("课程ID不能为空");
        }
        return getForMap("/api/course/" + courseId);
    }

    /**
     * 根据班级ID获取班级信息
     */
    public Map<String, Object> getClassById(Long classId) {
        if (classId == null) {
            throw new IllegalArgumentException("班级ID不能为空");
        }
        return getForMap("/api/class/" + classId);
    }

    /**
     * 根据章节ID获取章节信息
     */
    public Map<String, Object> getSectionById(Long sectionId) {
        if (sectionId == null) {
            throw new IllegalArgumentException("章节ID不能为空");
        }
        return getForMap("/api/section/" + sectionId);
    }

    /**
     * 获取课程下的所有章节
     */
    public List<Map<String, Object>> getSectionsByCourseId(Long courseId) {
        if (courseId == null) {
            throw new IllegalArgumentException("课程ID不能为空");
        }
        return get("/api/course/" + courseId + "/sections", List.class);
    }

    /**
     * 获取班级下的所有学生
     */
    public List<Map<String, Object>> getStudentsByClassId(Long classId) {
        if (classId == null) {
            throw new IllegalArgumentException("班级ID不能为空");
        }
        return get("/api/class/" + classId + "/students", List.class);
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
     * 批量获取课程信息
     */
    public List<Map<String, Object>> getCoursesByIds(String courseIds) {
        if (courseIds == null || courseIds.trim().isEmpty()) {
            throw new IllegalArgumentException("课程ID列表不能为空");
        }
        return get("/api/course/batch?ids=" + courseIds, List.class);
    }

    // ============ 微服务模拟数据方法 ============

    /**
     * 批量获取课程信息（逐个调用 /api/courses/{id}，失败时降级为模拟数据）
     */
    public Map<Long, Map<String, Object>> getCoursesByIds(List<Long> courseIds) {
        Map<Long, Map<String, Object>> result = new HashMap<>();
        if (courseIds == null || courseIds.isEmpty()) {
            return result;
        }
        for (Long courseId : courseIds) {
            try {
                // 逐个调用课程详情接口
                Map<String, Object> courseDetail = getForMap("/api/courses/" + courseId);
                if (courseDetail != null) {
                    Object dataObj = courseDetail.get("data");
                    if (dataObj instanceof Map) {
                        result.put(courseId, (Map<String, Object>) dataObj);
                        continue;
                    }
                }
            } catch (Exception ex) {
                // 记录日志，降级为模拟数据
                 logger.warn("获取课程 {} 失败，使用模拟数据: {}", courseId, ex.getMessage());
            }
//            // 模拟数据（降级）
//            Map<String, Object> course = new HashMap<>();
//            course.put("id", courseId);
//            course.put("name", "模拟课程-" + courseId);
//            course.put("description", "这是模拟课程描述-" + courseId);
//            course.put("teacherId", courseId * 10 + 1);
//            result.put(courseId, course);
        }
        return result;
    }

    /**
     * 批量获取练习信息（模拟数据）
     */
    public Map<Long, Map<String, Object>> getPracticesByIds(List<Long> practiceIds) {
        Map<Long, Map<String, Object>> result = new HashMap<>();

        // 模拟数据
        for (Long practiceId : practiceIds) {
            Map<String, Object> practice = new HashMap<>();
            practice.put("id", practiceId);
            practice.put("title", "模拟练习-" + practiceId);
            practice.put("courseId", practiceId % 3 + 1);
            practice.put("courseName", "模拟课程-" + (practiceId % 3 + 1));
            result.put(practiceId, practice);
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
