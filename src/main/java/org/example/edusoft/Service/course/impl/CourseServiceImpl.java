package org.example.edusoft.service.course.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.example.edusoft.entity.course.Course;
import org.example.edusoft.entity.course.CourseDetailDTO;
import org.example.edusoft.entity.course.CourseSection;
import org.example.edusoft.mapper.course.CourseMapper;
import org.example.edusoft.service.course.CourseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.List;

@Service
public class CourseServiceImpl implements CourseService {

    @Autowired
    private CourseMapper courseMapper;

    @Override
    @Transactional
    public Course createCourse(Course course) {
        // 验证课程代码唯一性
        if (isCourseCodeExists(course.getCode())) {
            throw new IllegalArgumentException("课程代码已存在");
        }
        
        // 验证课程名称
        if (!StringUtils.hasText(course.getName())) {
            throw new IllegalArgumentException("课程名称不能为空");
        }
        
        // 验证教师ID
        if (course.getTeacherId() == null) {
            throw new IllegalArgumentException("教师ID不能为空");
        }
        
        courseMapper.insert(course);
        return course;
    }

    @Override
    public List<Course> getCoursesByUserId(Long userId) {
        if (userId == null) {
            throw new IllegalArgumentException("用户ID不能为空");
        }
        return courseMapper.getCoursesByUserId(userId);
    }

    @Override
    public Course getCourseById(Long id) {
        if (id == null) {
            throw new IllegalArgumentException("课程ID不能为空");
        }
        Course course = courseMapper.selectById(id);
        if (course == null) {
            throw new IllegalArgumentException("课程不存在");
        }
        return course;
    }

    @Override
    @Transactional
    public Course updateCourse(Course course) {
        if (course.getId() == null) {
            throw new IllegalArgumentException("课程ID不能为空");
        }
        
        // 检查课程是否存在
        Course existingCourse = courseMapper.selectById(course.getId());
        if (existingCourse == null) {
            throw new IllegalArgumentException("课程不存在");
        }
        
        // 如果修改了课程代码，需要检查唯一性
        if (!existingCourse.getCode().equals(course.getCode()) 
            && isCourseCodeExists(course.getCode())) {
            throw new IllegalArgumentException("课程代码已存在");
        }
        
        // 不允许修改教师ID
        course.setTeacherId(existingCourse.getTeacherId());
        
        courseMapper.updateById(course);
        return course;
    }

    @Override
    @Transactional
    public boolean deleteCourse(Long id) {
        if (id == null) {
            throw new IllegalArgumentException("课程ID不能为空");
        }
        
        // 检查课程是否存在
        Course course = courseMapper.selectById(id);
        if (course == null) {
            throw new IllegalArgumentException("课程不存在");
        }
        
        // TODO: 检查是否有关联的班级和学生
        // 这里可以添加检查逻辑，如果有关联数据则抛出异常
        
        return courseMapper.deleteById(id) > 0;
    }
    
    // 检查课程代码是否存在
    private boolean isCourseCodeExists(String code) {
        QueryWrapper<Course> wrapper = new QueryWrapper<>();
        wrapper.eq("code", code);
        return courseMapper.selectCount(wrapper) > 0;
    }

    @Override
    public CourseDetailDTO getCourseDetailById(Long courseId) {
        CourseDetailDTO courseDetail = courseMapper.getCourseDetailById(courseId);
        if (courseDetail != null) {
            // 获取并设置章节信息
            List<CourseSection> sections = courseMapper.getSectionsByCourseId(courseId);
            courseDetail.setSections(sections);
            
            // 获取并设置班级信息
            List<CourseDetailDTO.ClassInfo> classes = courseMapper.getClassesByCourseId(courseId);
            courseDetail.setClasses(classes);
        }
        return courseDetail;
    }

    @Override
    public List<CourseDetailDTO> getCourseDetailsByUserId(Long userId) {
        if (userId == null) {
            throw new IllegalArgumentException("用户ID不能为空");
        }
        List<CourseDetailDTO> courses = courseMapper.getCourseDetailsByUserId(userId);
        
        // 为每个课程获取章节和班级信息
        for (CourseDetailDTO course : courses) {
            List<CourseSection> sections = courseMapper.getSectionsByCourseId(course.getId());
            course.setSections(sections);
            
            List<CourseDetailDTO.ClassInfo> classes = courseMapper.getClassesByCourseId(course.getId());
            course.setClasses(classes);
        }
        return courses;
    }

    @Override
    public List<CourseDetailDTO> getAllCourses() {
        return courseMapper.selectAllCoursesWithNames(new QueryWrapper<Course>().orderByDesc("id"));
    }
} 