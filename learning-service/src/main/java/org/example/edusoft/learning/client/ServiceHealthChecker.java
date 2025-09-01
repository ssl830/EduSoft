package org.example.edusoft.learning.client;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

/**
 * 微服务健康检查工具
 */
@Component
public class ServiceHealthChecker {
    
    @Autowired
    private UserServiceClient userServiceClient;
    
    @Autowired
    private CourseClient courseClient;
    
    @Autowired
    private ContentClient contentClient;
    
    /**
     * 检查所有微服务的健康状态
     */
    public Map<String, Boolean> checkAllServices() {
        Map<String, Boolean> healthStatus = new HashMap<>();
        
        healthStatus.put("user-service", checkUserService());
        healthStatus.put("course-service", checkCourseService());
        healthStatus.put("content-service", checkContentService());
        
        return healthStatus;
    }
    
    /**
     * 检查用户服务健康状态
     */
    public boolean checkUserService() {
        try {
            // 使用现有的业务接口进行健康检查
            return checkUserServiceFallback();
        } catch (Exception ex) {
            return false;
        }
    }
    
    /**
     * 用户服务健康检查回退方法
     */
    private boolean checkUserServiceFallback() {
        try {
            // 使用token验证接口进行健康检查
            String testToken = "test_token";
            boolean isValid = userServiceClient.validateToken("http://localhost:8081", testToken);
            // 即使token无效，如果服务响应说明服务正常运行
            return true;
        } catch (Exception ex) {
            // 如果是404错误，说明服务正常运行，只是接口不存在
            return ex.getMessage() != null && ex.getMessage().contains("404");
        }
    }
    
    /**
     * 检查课程服务健康状态
     */
    public boolean checkCourseService() {
        try {
            // 使用 Actuator 健康检查端点
            Map<String, Object> health = courseClient.getForMap("/actuator/health");
            return "UP".equals(health.get("status"));
        } catch (Exception ex) {
            // 如果 Actuator 端点不可用，回退到业务接口检查
            return checkCourseServiceFallback();
        }
    }
    
    /**
     * 课程服务健康检查回退方法
     */
    private boolean checkCourseServiceFallback() {
        try {
            courseClient.getCourseById(999999L);
            return true;
        } catch (Exception ex) {
            // 如果是404错误，说明服务正常运行，只是课程不存在
            return ex.getMessage() != null && ex.getMessage().contains("404");
        }
    }
    
    /**
     * 检查内容服务健康状态
     */
    public boolean checkContentService() {
        try {
            // 使用 Actuator 健康检查端点
            Map<String, Object> health = contentClient.getForMap("/actuator/health");
            return "UP".equals(health.get("status"));
        } catch (Exception ex) {
            // 如果 Actuator 端点不可用，回退到业务接口检查
            return checkContentServiceFallback();
        }
    }
    
    /**
     * 内容服务健康检查回退方法
     */
    private boolean checkContentServiceFallback() {
        try {
            contentClient.getResourceById(999999L);
            return true;
        } catch (Exception ex) {
            // 如果是404错误，说明服务正常运行，只是资源不存在
            return ex.getMessage() != null && ex.getMessage().contains("404");
        }
    }
    
    /**
     * 获取不健康的服务列表
     */
    public String[] getUnhealthyServices() {
        Map<String, Boolean> healthStatus = checkAllServices();
        return healthStatus.entrySet().stream()
                .filter(entry -> !entry.getValue())
                .map(Map.Entry::getKey)
                .toArray(String[]::new);
    }
}
