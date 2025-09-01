package org.example.edusoft.learning.service.example;

import org.example.edusoft.learning.client.UserServiceClient;
import org.example.edusoft.learning.client.CourseClient;
import org.example.edusoft.learning.client.ContentClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;

/**
 * 微服务调用示例服务
 * 展示如何使用各种Client进行微服务间通信
 */
@Service
public class MicroserviceExampleService {
    
    @Autowired
    private UserServiceClient userClient;
    
    @Autowired
    private CourseClient courseClient;
    
    @Autowired
    private ContentClient contentClient;
    
    /**
     * 示例：获取用户的学习信息
     */
    public Map<String, Object> getUserLearningInfo(Long userId) {
        // 1. 获取用户基本信息
        Map<String, Object> userInfo = userClient.getUserById(userId);
        
        // 2. 获取用户的课程列表
        var userCourses = courseClient.getUserCourses(userId);
        
        // 3. 获取用户的学习统计
        Map<String, Object> learningStats = contentClient.getUserLearningStats(userId);
        
        // 4. 组装返回结果
        return Map.of(
            "user", userInfo,
            "courses", userCourses,
            "stats", learningStats
        );
    }
    
    /**
     * 示例：验证用户是否可以提交作业
     */
    public boolean canUserSubmitHomework(Long userId, Long homeworkId, Long classId) {
        // 1. 验证用户是否存在
        if (!userClient.userExists(userId)) {
            return false;
        }
        
        // 2. 验证用户是否属于该班级
        if (!courseClient.isUserInClass(userId, classId)) {
            return false;
        }
        
        // 3. 这里可以添加更多业务逻辑验证
        // 比如检查作业是否过期、用户是否已经提交过等
        
        return true;
    }
    
    /**
     * 示例：获取课程的完整信息（包含资源）
     */
    public Map<String, Object> getCourseFullInfo(Long courseId, Long userId) {
        // 1. 获取课程基本信息
        Map<String, Object> courseInfo = courseClient.getCourseById(courseId);
        
        // 2. 验证用户是否可以访问该课程
        boolean canAccess = courseClient.canUserAccessCourse(userId, courseId);
        if (!canAccess) {
            throw new RuntimeException("用户无权访问该课程");
        }
        
        // 3. 获取课程下的所有章节
        var sections = courseClient.getSectionsByCourseId(courseId);
        
        // 4. 获取课程下的所有资源
        var resources = contentClient.getResourcesByCourseId(courseId);
        
        // 5. 组装返回结果
        return Map.of(
            "course", courseInfo,
            "sections", sections,
            "resources", resources,
            "canAccess", canAccess
        );
    }
}
