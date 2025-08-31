package org.example.edusoft.learning.client;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

/**
 * 课程服务客户端
 * 负责调用course-service的相关接口
 */
@Component
public class CourseClient extends BaseServiceClient {
    
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
        return getForMap("/api/courses/" + courseId);
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
     * 根据章节ID获取章节信息（暂时不可用，course-service没有此接口）
     */
    public Map<String, Object> getSectionById(Long sectionId) {
        throw new UnsupportedOperationException("course-service暂未提供按ID获取章节的接口");
    }
    
    /**
     * 获取课程下的所有章节
     */
    public List<Map<String, Object>> getSectionsByCourseId(Long courseId) {
        if (courseId == null) {
            throw new IllegalArgumentException("课程ID不能为空");
        }
        return get("/api/course-sections/course/" + courseId, List.class);
    }
    
    /**
     * 获取班级下的所有学生
     */
    public List<Map<String, Object>> getStudentsByClassId(Long classId) {
        if (classId == null) {
            throw new IllegalArgumentException("班级ID不能为空");
        }
        return get("/api/classes/" + classId + "/users", List.class);
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
}
