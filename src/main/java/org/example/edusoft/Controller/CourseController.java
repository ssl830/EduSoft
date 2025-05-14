package org.example.edusoft.Controller;

import org.example.edusoft.entity.Course;
import org.example.edusoft.entity.Resource;
import org.example.edusoft.entity.CourseSection;
import org.example.edusoft.service.CourseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/**
 * 课程管理控制器
 * 提供课程创建、更新、查询、章节管理和资源管理等功能的REST API
 */
@RestController
@RequestMapping("/api/courses")
public class CourseController {

    @Autowired
    private CourseService courseService;

    /**
     * 创建新课程
     * @param course 课程信息
     * @return 创建成功的课程信息
     */
    @PostMapping
    public ResponseEntity<Course> createCourse(@RequestBody Course course) {
        return ResponseEntity.ok(courseService.createCourse(course));
    }

    /**
     * 更新课程信息
     * @param courseId 课程ID
     * @param course 更新的课程信息
     * @return 更新后的课程信息
     */
    @PutMapping("/{courseId}")
    public ResponseEntity<Course> updateCourse(@PathVariable Long courseId, @RequestBody Course course) {
        course.setId(courseId);
        return ResponseEntity.ok(courseService.updateCourse(course));
    }

    /**
     * 获取课程详情
     * @param courseId 课程ID
     * @return 课程详细信息
     */
    @GetMapping("/{courseId}")
    public ResponseEntity<Course> getCourse(@PathVariable Long courseId) {
        return ResponseEntity.ok(courseService.getCourse(courseId));
    }

    /**
     * 获取用户的课程列表
     * @param userId 用户ID
     * @param role 用户角色（teacher/student/ta）
     * @return 用户的课程列表
     */
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<Course>> getUserCourses(
            @PathVariable String userId,
            @RequestParam String role) {
        return ResponseEntity.ok(courseService.getUserCourses(userId, role));
    }

    /**
     * 添加课程章节
     * @param courseId 课程ID
     * @param section 章节信息
     * @return 创建成功的章节信息
     */
    @PostMapping("/{courseId}/sections")
    public ResponseEntity<CourseSection> addSection(@PathVariable Long courseId, @RequestBody CourseSection section) {
        section.setCourseId(courseId);
        return ResponseEntity.ok(courseService.addSection(section));
    }

    /**
     * 删除课程章节
     * @param courseId 课程ID
     * @param sectionId 章节ID
     * @return 无返回内容
     */
    @DeleteMapping("/{courseId}/sections/{sectionId}")
    public ResponseEntity<Void> deleteSection(@PathVariable Long courseId, @PathVariable Long sectionId) {
        courseService.deleteSection(sectionId);
        return ResponseEntity.ok().build();
    }

    /**
     * 上传课程资源
     * @param courseId 课程ID
     * @param file 资源文件
     * @param title 资源标题
     * @param type 资源类型
     * @param sectionId 所属章节ID（可选）
     * @return 上传成功的资源信息
     */
    @PostMapping("/{courseId}/resources")
    public ResponseEntity<Resource> uploadResource(
            @PathVariable Long courseId,
            @RequestParam("file") MultipartFile file,
            @RequestParam("title") String title,
            @RequestParam("type") Resource.ResourceType type,
            @RequestParam(value = "sectionId", required = false) Long sectionId) {
        return ResponseEntity.ok(courseService.uploadResource(courseId, sectionId, file, title, type));
    }

    /**
     * 获取课程预览信息（包含所有章节）
     * @param courseId 课程ID
     * @return 包含章节信息的课程详情
     */
    @GetMapping("/{courseId}/preview")
    public ResponseEntity<Course> previewCourse(@PathVariable Long courseId) {
        return ResponseEntity.ok(courseService.getCourseWithSections(courseId));
    }
} 