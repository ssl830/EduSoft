package org.example.edusoft.learning.controller;

import org.example.edusoft.learning.client.ServiceHealthChecker;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

/**
 * 微服务健康检查控制器
 */
@RestController
@RequestMapping("/api/health")
public class HealthController {
    
    @Autowired
    private ServiceHealthChecker serviceHealthChecker;
    
    /**
     * 检查所有微服务的健康状态
     */
    @GetMapping("/services")
    public Map<String, Object> checkAllServices() {
        Map<String, Boolean> healthStatus = serviceHealthChecker.checkAllServices();
        String[] unhealthyServices = serviceHealthChecker.getUnhealthyServices();
        
        Map<String, Object> result = new HashMap<>();
        result.put("services", healthStatus);
        result.put("unhealthyServices", unhealthyServices);
        result.put("allHealthy", unhealthyServices.length == 0);
        result.put("timestamp", System.currentTimeMillis());
        
        return result;
    }
    
    /**
     * 检查用户服务健康状态
     */
    @GetMapping("/user-service")
    public Map<String, Object> checkUserService() {
        boolean isHealthy = serviceHealthChecker.checkUserService();
        Map<String, Object> result = new HashMap<>();
        result.put("service", "user-service");
        result.put("status", isHealthy ? "UP" : "DOWN");
        result.put("timestamp", System.currentTimeMillis());
        return result;
    }
    
    /**
     * 检查课程服务健康状态
     */
    @GetMapping("/course-service")
    public Map<String, Object> checkCourseService() {
        boolean isHealthy = serviceHealthChecker.checkCourseService();
        Map<String, Object> result = new HashMap<>();
        result.put("service", "course-service");
        result.put("status", isHealthy ? "UP" : "DOWN");
        result.put("timestamp", System.currentTimeMillis());
        return result;
    }
    
    /**
     * 检查内容服务健康状态
     */
    @GetMapping("/content-service")
    public Map<String, Object> checkContentService() {
        boolean isHealthy = serviceHealthChecker.checkContentService();
        Map<String, Object> result = new HashMap<>();
        result.put("service", "content-service");
        result.put("status", isHealthy ? "UP" : "DOWN");
        result.put("timestamp", System.currentTimeMillis());
        return result;
    }
}
