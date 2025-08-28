package org.example.edusoft.controller.course;

import jakarta.validation.Valid;
import org.example.edusoft.common.Result;
import org.example.edusoft.entity.course.Course;
import org.example.edusoft.entity.course.CourseDetailDTO;
import org.example.edusoft.service.course.CourseService;
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
    public Result<List<CourseDetailDTO>> getCoursesByUserId(@PathVariable Long userId) {
        try {
            List<CourseDetailDTO> courses = courseService.getCourseDetailsByUserId(userId);
            return Result.success(courses);
        } catch (Exception e) {
            return Result.error(500, "获取课程列表失败：" + e.getMessage());
        }
    }

    @GetMapping("/{id}")
    public Result<CourseDetailDTO> getCourseById(@PathVariable Long id) {
        try {
            CourseDetailDTO course = courseService.getCourseDetailById(id);
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
    public Result<List<CourseDetailDTO>> getAllCourses() {
        try {
            List<CourseDetailDTO> courses = courseService.getAllCourses();
            return Result.success(courses);
        } catch (Exception e) {
            return Result.error(500, "获取课程列表失败：" + e.getMessage());
        }
    }
} 