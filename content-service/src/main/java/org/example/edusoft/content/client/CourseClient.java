package org.example.edusoft.content.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * Course Service Client
 * Used for communication with course-service
 */
@FeignClient(name = "course-service", url = "${course.service.url:http://localhost:8082}")
public interface CourseClient {

    /**
     * Get course information
     * @param courseId Course ID
     * @return Course information
     */
    @GetMapping("/api/course/{courseId}")
    Map<String, Object> getCourseInfo(@PathVariable Long courseId);

    /**
     * Check user course permission
     * @param courseId Course ID
     * @param userId User ID
     * @return Permission check result
     */
    @GetMapping("/api/course/{courseId}/user/{userId}/permission")
    Map<String, Object> checkCoursePermission(@PathVariable Long courseId, @PathVariable Long userId);

    /**
     * Get course members list
     * @param courseId Course ID
     * @return Course members list
     */
    @GetMapping("/api/course/{courseId}/members")
    Map<String, Object> getCourseMembers(@PathVariable Long courseId);

    /**
     * Validate chapter exists
     * @param courseId Course ID
     * @param chapterId Chapter ID
     * @return Chapter information
     */
    @GetMapping("/api/course/{courseId}/chapter/{chapterId}")
    Map<String, Object> getChapterInfo(@PathVariable Long courseId, @PathVariable Long chapterId);

    /**
     * Check if user is course teacher
     * @param courseId Course ID
     * @param userId User ID
     * @return Check result
     */
    @GetMapping("/api/course/{courseId}/teacher/{userId}")
    Map<String, Object> isCourseTeacher(@PathVariable Long courseId, @PathVariable Long userId);

    /**
     * Check if user is course student
     * @param courseId Course ID
     * @param userId User ID
     * @return Check result
     */
    @GetMapping("/api/course/{courseId}/student/{userId}")
    Map<String, Object> isCourseStudent(@PathVariable Long courseId, @PathVariable Long userId);

    /**
     * Get class information
     * @param classId Class ID
     * @return Class information
     */
    @GetMapping("/api/class/{classId}")
    Map<String, Object> getClassInfo(@PathVariable Long classId);

    /**
     * Check if user is class member
     * @param classId Class ID
     * @param userId User ID
     * @return Check result
     */
    @GetMapping("/api/class/{classId}/member/{userId}")
    Map<String, Object> isClassMember(@PathVariable Long classId, @PathVariable Long userId);

    /**
     * Get course chapters list
     * @param courseId Course ID
     * @return Chapters list
     */
    @GetMapping("/api/course/{courseId}/chapters")
    Map<String, Object> getCourseChapters(@PathVariable Long courseId);

    /**
     * Validate course is active
     * @param courseId Course ID
     * @return Validation result
     */
    @GetMapping("/api/course/{courseId}/status")
    Map<String, Object> getCourseStatus(@PathVariable Long courseId);

    /**
     * Get user enrolled courses list
     * @param userId User ID
     * @return Courses list
     */
    @GetMapping("/api/user/{userId}/courses")
    Map<String, Object> getUserCourses(@PathVariable Long userId);

    /**
     * Get user teaching courses list
     * @param userId User ID
     * @return Courses list
     */
    @GetMapping("/api/user/{userId}/teaching-courses")
    Map<String, Object> getUserTeachingCourses(@PathVariable Long userId);
}
