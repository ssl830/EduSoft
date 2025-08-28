package org.example.edusoft.controller.course;

import org.example.edusoft.entity.course.CourseSection;
import org.example.edusoft.service.course.CourseSectionService;
import org.example.edusoft.common.Result;
import org.example.edusoft.exception.CourseSectionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/courses")
public class CourseSectionController {
    @Autowired
    private CourseSectionService courseSectionService;

    @PostMapping("/{courseId}/sections")
    public Result<Map<String, Object>> createSections(
            @PathVariable Long courseId,
            @RequestBody Map<String, List<CourseSection>> request) {
        try {
            List<CourseSection> sections = request.get("sections");
            for (CourseSection section : sections) {
                section.setCourseId(courseId);
            }
            courseSectionService.createSections(sections);
            return Result.success(Map.of("code", 200, "message", "章节创建成功"));
        } catch (CourseSectionException e) {
            return Result.error(e.getCode() + ": " + e.getMessage());
        } catch (Exception e) {
            return Result.error("系统错误：" + e.getMessage());
        }
    }

    @DeleteMapping("/{courseId}/sections/{sectionId}")
    public Result<Map<String, Object>> deleteSection(
            @PathVariable Long courseId,
            @PathVariable Long sectionId) {
        try {
            courseSectionService.deleteSection(sectionId);
            return Result.success(Map.of("code", 200, "message", "章节删除成功"));
        } catch (CourseSectionException e) {
            return Result.error(e.getCode() + ": " + e.getMessage());
        } catch (Exception e) {
            return Result.error("系统错误：" + e.getMessage());
        }
    }

    @GetMapping("/{courseId}/sections")
    public Result<List<CourseSection>> getSectionsByCourseId(@PathVariable Long courseId) {
        try {
            List<CourseSection> sections = courseSectionService.getSectionsByCourseId(courseId);
            return Result.success(sections);
        } catch (CourseSectionException e) {
            return Result.error(e.getCode() + ": " + e.getMessage());
        } catch (Exception e) {
            return Result.error("系统错误：" + e.getMessage());
        }
    }
} 