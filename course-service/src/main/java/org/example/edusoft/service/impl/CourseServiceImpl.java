package org.example.edusoft.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.example.edusoft.entity.Course;
import org.example.edusoft.dto.CourseDetailDTO;
import org.example.edusoft.entity.CourseSection;
import org.example.edusoft.mapper.CourseMapper;
import org.example.edusoft.service.CourseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.util.List;
import java.util.Map;

// 用户服务客户端
import org.example.edusoft.client.UserServiceClient;

@Service
public class CourseServiceImpl implements CourseService {

    @Autowired
    private CourseMapper courseMapper;

    @Autowired
    private UserServiceClient userServiceClient;

    @Value("${services.user.base-url:http://localhost:8081}")
    private String userServiceBaseUrl;

    private String resolveOutboundToken() {
        RequestAttributes attrs = RequestContextHolder.getRequestAttributes();
        if (attrs instanceof ServletRequestAttributes servlet) {
            String satoken = servlet.getRequest().getHeader("satoken");
            if (satoken != null && !satoken.isEmpty()) return satoken;
            String cookie = servlet.getRequest().getHeader("Cookie");
            if (cookie != null) {
                for (String part : cookie.split(";")) {
                    String p = part.trim();
                    if (p.startsWith("satoken=")) return p.substring("satoken=".length());
                }
            }
            String auth = servlet.getRequest().getHeader("Authorization");
            if (auth != null && !auth.isEmpty()) return auth;
        }
        return null;
    }

    private String fetchUsernameByUserId(Long userId) {
        if (userId == null) return null;
        String token = resolveOutboundToken();
        try {
            Map<String, Object> user = userServiceClient.fetchUserById(userServiceBaseUrl, token, String.valueOf(userId));
            if (user != null) {
                Object name = user.get("username");
                return name == null ? null : String.valueOf(name);
            }
        } catch (Exception ignore) {
        }
        return null;
    }

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
            // 用户服务获取教师名
            String teacherName = fetchUsernameByUserId(courseDetail.getTeacherId());
            if (teacherName != null) {
                courseDetail.setTeacherName(teacherName);
            }
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
        for (CourseDetailDTO course : courses) {
            String teacherName = fetchUsernameByUserId(course.getTeacherId());
            if (teacherName != null) {
                course.setTeacherName(teacherName);
            }
            List<CourseSection> sections = courseMapper.getSectionsByCourseId(course.getId());
            course.setSections(sections);
            List<CourseDetailDTO.ClassInfo> classes = courseMapper.getClassesByCourseId(course.getId());
            course.setClasses(classes);
        }
        return courses;
    }

    @Override
    public List<CourseDetailDTO> getAllCourses() {
        List<CourseDetailDTO> list = courseMapper.selectAllCoursesWithNames(new QueryWrapper<Course>().orderByDesc("id"));
        for (CourseDetailDTO dto : list) {
            // 教师名补齐
            String teacherName = fetchUsernameByUserId(dto.getTeacherId());
            if (teacherName != null) {
                dto.setTeacherName(teacherName);
            }
            // 章节与班级补齐
            List<CourseSection> sections = courseMapper.getSectionsByCourseId(dto.getId());
            dto.setSections(sections);
            List<CourseDetailDTO.ClassInfo> classes = courseMapper.getClassesByCourseId(dto.getId());
            dto.setClasses(classes);
            // 学生总数（按班级学生数汇总，避免为0）
            int totalStudents = 0;
            if (classes != null) {
                for (CourseDetailDTO.ClassInfo ci : classes) {
                    totalStudents += (ci.getStudentCount() == null ? 0 : ci.getStudentCount());
                }
            }
            if (totalStudents > 0) {
                dto.setStudentCount(totalStudents);
            }
        }
        return list;
    }
}
