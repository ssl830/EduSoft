package org.example.edusoft.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.example.edusoft.common.Result;
import org.example.edusoft.entity.Course;
import org.example.edusoft.dto.CourseDetailDTO;
import org.example.edusoft.service.CourseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/courses")
public class CourseController {

    @Autowired
    private CourseService courseService;

    @PostMapping
    public Result<Course> createCourse(@Valid @RequestBody Course course) {
        try {
            Course createdCourse = courseService.createCourse(course);
            return Result.success(createdCourse);
        } catch (Exception e) {
            return Result.error(500, "创建课程失败：" + e.getMessage());
        }
    }

    @GetMapping("/user/{userId}")
    public Result<List<CourseDetailDTO>> getCoursesByUserId(@PathVariable Long userId, HttpServletRequest request) {
        try {
            String token = extractToken(request);
            List<CourseDetailDTO> courses = courseService.getCourseDetailsByUserId(userId, token);
            return Result.success(courses);
        } catch (Exception e) {
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

    @PutMapping("/{id}")
    public Result<Course> updateCourse(@PathVariable Long id, @Valid @RequestBody Course course) {
        try {
            course.setId(id);
            Course updatedCourse = courseService.updateCourse(course);
            return Result.success(updatedCourse);
        } catch (Exception e) {
            return Result.error(500, "更新课程失败：" + e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    public Result<Boolean> deleteCourse(@PathVariable Long id) {
        try {
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
