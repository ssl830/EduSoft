package org.example.edusoft.controller;

import jakarta.validation.Valid;
import org.example.edusoft.common.Result;
import org.example.edusoft.entity.CourseSection;
import org.example.edusoft.service.CourseSectionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/course-sections")
public class CourseSectionController {

    @Autowired
    private CourseSectionService courseSectionService;

    @GetMapping("/course/{courseId}")
    public Result<List<CourseSection>> getSectionsByCourseId(@PathVariable Long courseId) {
        try {
            List<CourseSection> sections = courseSectionService.getSectionsByCourseId(courseId);
            return Result.success(sections);
        } catch (Exception e) {
            return Result.error(500, "获取章节列表失败：" + e.getMessage());
        }
    }

    @PostMapping
    public Result<CourseSection> createSection(@Valid @RequestBody CourseSection section) {
        try {
            CourseSection createdSection = courseSectionService.createSection(section);
            return Result.success(createdSection);
        } catch (Exception e) {
            return Result.error(500, "创建章节失败：" + e.getMessage());
        }
    }

    @PutMapping("/{id}")
    public Result<CourseSection> updateSection(@PathVariable Long id, @Valid @RequestBody CourseSection section) {
        try {
            section.setId(id);
            CourseSection updatedSection = courseSectionService.updateSection(section);
            return Result.success(updatedSection);
        } catch (Exception e) {
            return Result.error(500, "更新章节失败：" + e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    public Result<Boolean> deleteSection(@PathVariable Long id) {
        try {
            boolean success = courseSectionService.deleteSection(id);
            return Result.success(success);
        } catch (Exception e) {
            return Result.error(500, "删除章节失败：" + e.getMessage());
        }
    }
}
