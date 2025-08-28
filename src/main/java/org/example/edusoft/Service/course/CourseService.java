package org.example.edusoft.service.course;

import org.example.edusoft.entity.course.Course;
import org.example.edusoft.entity.course.CourseDetailDTO;
import java.util.List;

public interface CourseService {
    // 创建课程
    Course createCourse(Course course);
    
    // 获取课程列表（根据用户角色返回不同结果）
    List<Course> getCoursesByUserId(Long userId);
    
    // 获取课程详情
    Course getCourseById(Long id);
    
    // 获取课程详细信息（包含教师信息、学生数量等）
    CourseDetailDTO getCourseDetailById(Long id);
    
    // 获取用户的课程详细信息列表
    List<CourseDetailDTO> getCourseDetailsByUserId(Long userId);
    
    // 获取所有课程的详细信息列表
    List<CourseDetailDTO> getAllCourses();
    
    // 更新课程信息
    Course updateCourse(Course course);
    
    // 删除课程
    boolean deleteCourse(Long id);
} 