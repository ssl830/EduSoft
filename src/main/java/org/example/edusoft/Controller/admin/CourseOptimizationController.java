package org.example.edusoft.controller.admin;

import org.example.edusoft.common.Result;
import org.example.edusoft.service.admin.CourseOptimizationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/admin/dashboard")
public class CourseOptimizationController {

    @Autowired
    private CourseOptimizationService courseOptimizationService;

    /**
     * 调用 AI 微服务生成课程优化建议
     */
    @PostMapping("/optimize")
    public Result<?> getOptimizationSuggestions(@RequestBody Map<String, Object> request) {
        try {
            Map<String, Object> suggestions = courseOptimizationService.generateOptimizationSuggestions(
                Long.parseLong(request.get("courseId").toString()),
                Long.parseLong(request.get("sectionId").toString()),
                Double.parseDouble(request.get("averageScore").toString()),
                Double.parseDouble(request.get("errorRate").toString()),
                Integer.parseInt(request.get("studentCount").toString())
            );
            return Result.success(suggestions);
        } catch (Exception e) {
            return Result.error("Failed to generate optimization suggestions: " + e.getMessage());
        }
    }
} 