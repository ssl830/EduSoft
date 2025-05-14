package org.example.edusoft.service.impl;

import org.example.edusoft.entity.Course;
import org.example.edusoft.entity.Resource;
import org.example.edusoft.entity.CourseSection;
import org.example.edusoft.mapper.CourseMapper;
import org.example.edusoft.mapper.ResourceMapper;
import org.example.edusoft.mapper.CourseSectionMapper;
import org.example.edusoft.service.CourseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 课程管理服务实现类
 * 实现课程管理相关的业务逻辑
 */
@Service
public class CourseServiceImpl implements CourseService {

    @Autowired
    private CourseMapper courseMapper;

    @Autowired
    private ResourceMapper resourceMapper;

    @Autowired
    private CourseSectionMapper courseSectionMapper;

    /**
     * 创建新课程
     * 设置创建时间并保存到数据库
     */
    @Override
    @Transactional
    public Course createCourse(Course course) {
        course.setCreatedAt(LocalDateTime.now());
        courseMapper.insert(course);
        return course;
    }

    /**
     * 更新课程信息
     * 更新数据库中的课程信息
     */
    @Override
    @Transactional
    public Course updateCourse(Course course) {
        courseMapper.update(course);
        return course;
    }

    /**
     * 获取课程详情
     * 根据课程ID查询课程信息
     */
    @Override
    public Course getCourse(Long courseId) {
        return courseMapper.findById(courseId);
    }

    /**
     * 获取教师的所有课程
     * 根据教师ID查询其创建的所有课程
     */
    @Override
    public List<Course> getTeacherCourses(Long teacherId) {
        return courseMapper.findByTeacherId(teacherId);
    }

    /**
     * 添加课程章节
     * 自动计算章节排序号并保存到数据库
     */
    @Override
    @Transactional
    public CourseSection addSection(CourseSection section) {
        // 获取当前课程的最大排序号
        Integer maxSortOrder = courseSectionMapper.findMaxSortOrderByCourseId(section.getCourseId());
        section.setSortOrder(maxSortOrder != null ? maxSortOrder + 1 : 1);
        courseSectionMapper.insert(section);
        return section;
    }

    /**
     * 删除课程章节
     * 从数据库中删除指定章节
     */
    @Override
    @Transactional
    public void deleteSection(Long sectionId) {
        courseSectionMapper.deleteById(sectionId);
    }

    /**
     * 上传课程资源
     * 保存资源文件并创建资源记录
     * TODO: 实现实际的文件上传逻辑
     */
    @Override
    @Transactional
    public Resource uploadResource(Long courseId, Long sectionId, MultipartFile file, String title, Resource.ResourceType type) {
        // TODO: 实现文件上传逻辑，将文件保存到文件系统或云存储
        String fileUrl = "uploaded_file_url"; // 这里需要实现实际的文件上传逻辑

        Resource resource = new Resource();
        resource.setCourseId(courseId);
        resource.setSectionId(sectionId);
        resource.setTitle(title);
        resource.setType(type);
        resource.setFileUrl(fileUrl);
        resource.setVersion(1);
        resource.setCreatedAt(LocalDateTime.now());
        resource.setVisibility(Resource.Visibility.PUBLIC);

        resourceMapper.insert(resource);
        return resource;
    }

    /**
     * 获取课程预览信息
     * 获取课程基本信息及其所有章节信息
     */
    @Override
    public Course getCourseWithSections(Long courseId) {
        Course course = courseMapper.findById(courseId);
        if (course != null) {
            List<CourseSection> sections = courseSectionMapper.findByCourseId(courseId);
            course.setSections(sections);
        }
        return course;
    }
} 