package org.example.edusoft.service;

import org.example.edusoft.entity.Course;
import org.example.edusoft.entity.Resource;
import org.example.edusoft.entity.CourseSection;
import org.springframework.web.multipart.MultipartFile;
import java.util.List;

/**
 * 课程管理服务接口
 * 定义课程管理相关的业务逻辑方法
 */
public interface CourseService {
    /**
     * 创建新课程
     * @param course 课程信息
     * @return 创建成功的课程信息
     */
    Course createCourse(Course course);

    /**
     * 更新课程信息
     * @param course 更新的课程信息
     * @return 更新后的课程信息
     */
    Course updateCourse(Course course);

    /**
     * 获取课程详情
     * @param courseId 课程ID
     * @return 课程详细信息
     */
    Course getCourse(Long courseId);

    /**
     * 获取教师的所有课程
     * @param teacherId 教师ID
     * @return 教师的所有课程列表
     */
    List<Course> getTeacherCourses(String teacherId);

    /**
     * 根据用户ID和角色获取课程列表
     * @param userId 用户ID
     * @param role 用户角色（teacher/student/ta）
     * @return 课程列表
     */
    List<Course> getUserCourses(String userId, String role);

    /**
     * 添加课程章节
     * @param section 章节信息
     * @return 创建成功的章节信息
     */
    CourseSection addSection(CourseSection section);

    /**
     * 删除课程章节
     * @param sectionId 章节ID
     */
    void deleteSection(Long sectionId);

    /**
     * 上传课程资源
     * @param courseId 课程ID
     * @param sectionId 所属章节ID（可选）
     * @param file 资源文件
     * @param title 资源标题
     * @param type 资源类型
     * @return 上传成功的资源信息
     */
    Resource uploadResource(Long courseId, Long sectionId, MultipartFile file, String title, Resource.ResourceType type);

    /**
     * 获取课程预览信息（包含所有章节）
     * @param courseId 课程ID
     * @return 包含章节信息的课程详情
     */
    Course getCourseWithSections(Long courseId);
} 