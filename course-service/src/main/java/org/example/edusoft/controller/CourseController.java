package org.example.edusoft.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.example.edusoft.common.Result;
import org.example.edusoft.entity.Course;
import org.example.edusoft.dto.CourseDetailDTO;
import org.example.edusoft.service.CourseService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/courses")
public class CourseController {

    private static final Logger logger = LoggerFactory.getLogger(CourseController.class);

    @Autowired
    private CourseService courseService;

    @PostMapping
    public Result<Course> createCourse(@Valid @RequestBody Course course, HttpServletRequest request) {
        try {
            // 从请求属性中获取当前用户信息
            Map<String, Object> currentUser = (Map<String, Object>) request.getAttribute("currentUser");
            if (currentUser == null) {
                return Result.error(401, "用户未认证");
            }
            
            // 如果请求中没有指定teacherId，使用当前用户的ID
            if (course.getTeacherId() == null) {
                // 尝试多种方式获取用户ID
                Object userIdObj = currentUser.get("id");
                if (userIdObj == null) {
                    // 尝试其他可能的字段名
                    userIdObj = currentUser.get("userId");
                }
                
                if (userIdObj == null) {
                    logger.error("无法获取当前用户ID，用户信息: {}", currentUser);
                    return Result.error(500, "无法获取用户ID");
                }
                
                Long userId;
                try {
                    userId = Long.valueOf(userIdObj.toString());
                } catch (NumberFormatException e) {
                    logger.error("用户ID格式错误: {}", userIdObj);
                    return Result.error(500, "用户ID格式错误");
                }
                course.setTeacherId(userId);
            }
            
            Course createdCourse = courseService.createCourse(course);
            return Result.success(createdCourse);
        } catch (Exception e) {
            return Result.error(500, "创建课程失败：" + e.getMessage());
        }
    }

    @GetMapping("/user/{userId}")
    public Result<List<CourseDetailDTO>> getCoursesByUserId(@PathVariable Long userId, HttpServletRequest request) {
        try {
            // 验证权限：只能查看自己的课程
            Map<String, Object> currentUser = (Map<String, Object>) request.getAttribute("currentUser");
            if (currentUser == null) {
                return Result.error(401, "用户未认证");
            }
            
            // 尝试多种方式获取用户ID
            Object currentUserIdObj = currentUser.get("id");
            if (currentUserIdObj == null) {
                // 尝试其他可能的字段名
                currentUserIdObj = currentUser.get("userId");
            }
            
            if (currentUserIdObj == null) {
                logger.error("无法获取当前用户ID，用户信息: {}", currentUser);
                return Result.error(500, "无法获取当前用户ID");
            }
            
            Long currentUserId;
            try {
                currentUserId = Long.valueOf(currentUserIdObj.toString());
            } catch (NumberFormatException e) {
                logger.error("用户ID格式错误: {}", currentUserIdObj);
                return Result.error(500, "用户ID格式错误");
            }
            
            if (!currentUserId.equals(userId)) {
                return Result.error(403, "无权限查看其他用户的课程");
            }
            
            String token = extractToken(request);
            List<CourseDetailDTO> courses = courseService.getCourseDetailsByUserId(userId, token);
            return Result.success(courses);
        } catch (Exception e) {
            logger.error("获取课程列表失败", e);
            return Result.error(500, "获取课程列表失败：" + e.getMessage());
        }
    }

    @GetMapping("/{id}")
    public Result<CourseDetailDTO> getCourseById(@PathVariable Long id, HttpServletRequest request) {
        try {
            String token = extractToken(request);
            CourseDetailDTO course = courseService.getCourseDetailById(id, token);
            return Result.success(course);
        } catch (Exception e) {
            return Result.error(500, "获取课程详情失败：" + e.getMessage());
        }
    }

    /**
     * 批量获取课程信息
     */
    @GetMapping("/batch")
    public Result<List<CourseDetailDTO>> getCoursesByIds(@RequestParam("ids") String ids, HttpServletRequest request) {
        if (ids == null || ids.trim().isEmpty()) {
            return Result.error(400, "课程ID列表不能为空");
        }
        String[] idArr = ids.split(",");
        List<CourseDetailDTO> result = new java.util.ArrayList<>();
        for (String idStr : idArr) {
            try {
                Long id = Long.valueOf(idStr.trim());
                // 复用已有的getCourseById接口逻辑
                Result<CourseDetailDTO> courseResult = getCourseById(id, request);
                if (courseResult.getCode() == 200 && courseResult.getData() != null) {
                    result.add(courseResult.getData());
                }
            } catch (Exception e) {
                // 忽略单个ID异常，继续处理其他ID
            }
        }
        return Result.success(result);
    }

    @PutMapping("/{id}")
    public Result<Course> updateCourse(@PathVariable Long id, @Valid @RequestBody Course course, HttpServletRequest request) {
        try {
            // 验证权限：只能更新自己创建的课程
            Map<String, Object> currentUser = (Map<String, Object>) request.getAttribute("currentUser");
            if (currentUser == null) {
                return Result.error(401, "用户未认证");
            }
            
            // 获取课程信息验证权限
            String token = extractToken(request);
            CourseDetailDTO existingCourse = courseService.getCourseDetailById(id, token);
            if (existingCourse == null) {
                return Result.error(404, "课程不存在");
            }
            
            // 尝试多种方式获取用户ID
            Object currentUserIdObj = currentUser.get("id");
            if (currentUserIdObj == null) {
                // 尝试其他可能的字段名
                currentUserIdObj = currentUser.get("userId");
            }
            
            if (currentUserIdObj == null) {
                logger.error("无法获取当前用户ID，用户信息: {}", currentUser);
                return Result.error(500, "无法获取当前用户ID");
            }
            
            Long currentUserId;
            try {
                currentUserId = Long.valueOf(currentUserIdObj.toString());
            } catch (NumberFormatException e) {
                logger.error("用户ID格式错误: {}", currentUserIdObj);
                return Result.error(500, "用户ID格式错误");
            }
            if (!currentUserId.equals(existingCourse.getTeacherId())) {
                return Result.error(403, "无权限更新此课程");
            }
            
            course.setId(id);
            Course updatedCourse = courseService.updateCourse(course);
            return Result.success(updatedCourse);
        } catch (Exception e) {
            return Result.error(500, "更新课程失败：" + e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    public Result<Boolean> deleteCourse(@PathVariable Long id, HttpServletRequest request) {
        try {
            // 验证权限：只能删除自己创建的课程
            Map<String, Object> currentUser = (Map<String, Object>) request.getAttribute("currentUser");
            if (currentUser == null) {
                return Result.error(401, "用户未认证");
            }
            
            // 获取课程信息验证权限
            String token = extractToken(request);
            CourseDetailDTO existingCourse = courseService.getCourseDetailById(id, token);
            if (existingCourse == null) {
                return Result.error(404, "课程不存在");
            }
            
            // 尝试多种方式获取用户ID
            Object currentUserIdObj = currentUser.get("id");
            if (currentUserIdObj == null) {
                // 尝试其他可能的字段名
                currentUserIdObj = currentUser.get("userId");
            }
            
            if (currentUserIdObj == null) {
                logger.error("无法获取当前用户ID，用户信息: {}", currentUser);
                return Result.error(500, "无法获取当前用户ID");
            }
            
            Long currentUserId;
            try {
                currentUserId = Long.valueOf(currentUserIdObj.toString());
            } catch (NumberFormatException e) {
                logger.error("用户ID格式错误: {}", currentUserIdObj);
                return Result.error(500, "用户ID格式错误");
            }
            if (!currentUserId.equals(existingCourse.getTeacherId())) {
                return Result.error(403, "无权限删除此课程");
            }
            
            boolean success = courseService.deleteCourse(id);
            return Result.success(success);
        } catch (Exception e) {
            return Result.error(500, "删除课程失败：" + e.getMessage());
        }
    }

    @GetMapping("/list")
    public Result<List<CourseDetailDTO>> getAllCourses(HttpServletRequest request) {
        try {
            String token = extractToken(request);
            List<CourseDetailDTO> courses = courseService.getAllCourses(token);
            return Result.success(courses);
        } catch (Exception e) {
            return Result.error(500, "获取课程列表失败：" + e.getMessage());
        }
    }
    
    private String extractToken(HttpServletRequest request) {
        String auth = request.getHeader("Authorization");
        if (auth != null && !auth.trim().isEmpty()) {
            return auth;
        }
        String satoken = request.getHeader("satoken");
        if (satoken != null && !satoken.trim().isEmpty()) {
            return satoken;
        }
        return null;
    }
}
