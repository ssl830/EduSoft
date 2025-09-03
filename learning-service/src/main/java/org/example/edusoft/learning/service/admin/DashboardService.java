package org.example.edusoft.learning.service.admin;

import org.example.edusoft.learning.client.ContentClient;
import org.example.edusoft.learning.client.CourseClient;
import org.example.edusoft.learning.client.UserServiceClient;
import org.example.edusoft.learning.mapper.admin.DashboardMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class DashboardService {

    @Autowired
    private DashboardMapper dashboardMapper;

    @Autowired
    private UserServiceClient userServiceClient;

    @Autowired
    private CourseClient courseClient;

    @Value("${service.user.url:http://localhost:8081}")
    private String userServiceBaseUrl;

    @Value("${service.course.url:http://localhost:8082}")
    private String courseServiceUrl;

    @Value("${service.content.url:http://localhost:8083}")
    private String contentServiceUrl;

    @Autowired
    private ContentClient contentClient;

    // 获取请求头中的 satoken，参考 ManualJudgeServiceImpl
    private String resolveOutboundToken() {
        RequestAttributes attrs = RequestContextHolder.getRequestAttributes();
        if (attrs instanceof ServletRequestAttributes servlet) {
            String satoken = servlet.getRequest().getHeader("satoken");
            if (satoken != null && !satoken.isEmpty())
                return satoken;
            String cookie = servlet.getRequest().getHeader("Cookie");
            if (cookie != null) {
                for (String part : cookie.split(";")) {
                    String p = part.trim();
                    if (p.startsWith("satoken="))
                        return p.substring("satoken=".length());
                }
            }
            String auth = servlet.getRequest().getHeader("Authorization");
            if (auth != null && !auth.isEmpty())
                return auth;
        }
        return null;
    }

    /**
     * 获取教师 & 学生在给定时间区间内的关键统计数据
     *
     * @param start 开始日期（含）
     * @param end   结束日期（含）
     * @return Map 结构，包含 teacherStats / studentStats
     */
    private static final org.slf4j.Logger logger = org.slf4j.LoggerFactory.getLogger(DashboardService.class);
    
    public Map<String, Object> getBasicStatistics(LocalDate start, LocalDate end) {
        logger.info("Getting basic statistics from {} to {}", start, end);
        Map<String, Object> result = new HashMap<>();

        List<Long> teacherIds = new ArrayList<>();
        List<Long> studentIds = new ArrayList<>();

        String token = resolveOutboundToken();
        if (token == null) {
            logger.warn("未能获取到有效的认证Token");
            token = ""; // 提供一个空token，让服务端决定是否接受
        }

        try {
            List<Map<String, Object>> teachers = userServiceClient.fetchAllTeachers(userServiceBaseUrl, token);
            List<Map<String, Object>> students = userServiceClient.fetchAllStudents(userServiceBaseUrl, token);
            logger.debug("获取到教师数: {}, 学生数: {}", teachers.size(), students.size());

            if (teachers != null) {
                teacherIds = teachers.stream()
                        .map(t -> {
                            Object id = t.get("id");
                            if (id instanceof Number) {
                                return ((Number) id).longValue();
                            }
                            return Long.valueOf(id.toString());
                        })
                        .collect(Collectors.toList());
            }

            if (students != null) {
                studentIds = students.stream()
                        .map(s -> {
                            Object id = s.get("id");
                            if (id instanceof Number) {
                                return ((Number) id).longValue();
                            }
                            return Long.valueOf(s.get("id").toString());
                        })
                        .collect(Collectors.toList());
            }
        } catch (Exception e) {
            logger.error("获取用户列表失败: {}", e.getMessage());
            // 使用空列表继续执行，而不是抛出异常中断整个流程
        }

        Map<String, Integer> teacherStats = new HashMap<>();
//        TODO
//        teacherStats.put("uploadResource", dashboardMapper.countTeacherUploadResource(start, end, teacherIds));
        teacherStats.put("createHomework", contentClient.countTeacherHomework(start, end, teacherIds));
        teacherStats.put("createPractice", dashboardMapper.countTeacherCreatePractice(start, end, teacherIds));
        teacherStats.put("correctPractice", dashboardMapper.countTeacherCorrectPractice(start, end));
        teacherStats.put("createQuestion", dashboardMapper.countTeacherCreateQuestion(start, end, teacherIds));
        teacherStats.put("teacherCount", teacherIds.size());

        Map<String, Integer> studentStats = new HashMap<>();
        studentStats.put("downloadResource", dashboardMapper.countStudentDownloadResource(start, end, studentIds));
        studentStats.put("submitHomework", contentClient.countStudentHomework(start, end, studentIds));
        studentStats.put("submitPractice", dashboardMapper.countStudentSubmitPractice(start, end, studentIds));
        studentStats.put("assistantQuestions", dashboardMapper.countStudentAssistantQuestions(start, end, studentIds));
        studentStats.put("studentCount", studentIds.size());

        result.put("teacher", teacherStats);
        result.put("student", studentStats);
        return result;
    }

    /**
     * 获取教学效率指数
     *
     * @param start 开始日期
     * @param end 结束日期
     * @return 包含备课耗时、练习设计耗时等统计的Map
     */
    public Map<String, Object> getTeachingEfficiencyMetrics(LocalDate start, LocalDate end) {
        Map<String, Object> metrics = new HashMap<>();

        // 备课与修正耗时：调用AI接口 /rag/generate, /rag/detail, /rag/regenerate 的次数与平均耗时
        Double prepAvgDuration = dashboardMapper.getAiServiceAverageDuration("/rag/generate", start, end) +
                dashboardMapper.getAiServiceAverageDuration("/rag/detail", start, end) +
                dashboardMapper.getAiServiceAverageDuration("/rag/regenerate", start, end);
        int prepCallCount = dashboardMapper.getAiServiceCallCount("/rag/generate", start, end) +
                dashboardMapper.getAiServiceCallCount("/rag/detail", start, end) +
                dashboardMapper.getAiServiceCallCount("/rag/regenerate", start, end);
        metrics.put("lessonPrepAvgDuration", String.format("%.2f", prepAvgDuration));
        metrics.put("lessonPrepCallCount", prepCallCount);

        // 课后练习设计与修正耗时：调用AI接口 /rag/generate_exercise 的次数与平均耗时
        Double exerciseDesignAvgDuration = dashboardMapper.getAiServiceAverageDuration("/rag/generate_exercise", start, end);
        int exerciseDesignCallCount = dashboardMapper.getAiServiceCallCount("/rag/generate_exercise", start, end);
        metrics.put("exerciseDesignAvgDuration", String.format("%.2f", exerciseDesignAvgDuration));
        metrics.put("exerciseDesignCallCount", exerciseDesignCallCount);

        // 课程优化方向分析
        List<Map<String, Object>> stats = dashboardMapper.getClassCourseSectionStats(start, end);
        if (stats == null) {
            stats = Collections.emptyList();
        }

        // 使用 CourseClient 获取所有课程和章节信息
        List<Map<String, Object>> allCourses = courseClient.getAllCourses();
        List<Map<String, Object>> allCoursesAndSections = new java.util.ArrayList<>();
        if (allCourses != null) {
            for (Map<String, Object> course : allCourses) {
                Long courseId = ((Number) course.get("id")).longValue();
                String courseName = course.get("name") != null ? course.get("name").toString() : "";
                List<Map<String, Object>> sections = courseClient.getSectionsByCourseId(courseId);
                if (sections != null) {
                    for (Map<String, Object> section : sections) {
                        Map<String, Object> entry = new HashMap<>();
                        entry.put("course_id", courseId);
                        entry.put("course_name", courseName);
                        entry.put("section_id", section.get("id"));
                        entry.put("section_name", section.get("title"));
                        allCoursesAndSections.add(entry);
                    }
                }
            }
        }

        // 使用 CourseClient 获取所有班级信息
        List<Map<String, Object>> allClasses = courseClient.getAllClasses();

        Map<String, String> courseSectionNames = allCoursesAndSections != null ?
                allCoursesAndSections.stream().collect(Collectors.toMap(
                        map -> map.get("course_id") + "-" + map.get("section_id"),
                        map -> map.get("course_name") + " - " + map.get("section_name")
                )) : new HashMap<>();
        Map<String, String> classNames = allClasses != null ?
                allClasses.stream()
                        .collect(Collectors.toMap(
                                map -> map.get("class_id") != null ? map.get("class_id").toString() : map.get("id").toString(),
                                map -> map.get("class_name") != null ? map.get("class_name").toString() : map.get("name").toString()
                        )) : new HashMap<>();
        StringBuilder advice = new StringBuilder();
        int count = 0;
        for (Map<String, Object> stat : stats) {
            Double avgScore = stat.get("average_score") instanceof Number ? ((Number)stat.get("average_score")).doubleValue() : null;
            Double passRate = stat.get("pass_rate") instanceof Number ? ((Number)stat.get("pass_rate")).doubleValue() : null;
            Long classId = stat.get("class_id") instanceof Number ? ((Number)stat.get("class_id")).longValue() : null;
            Long courseId = stat.get("course_id") instanceof Number ? ((Number)stat.get("course_id")).longValue() : null;
            Long sectionId = stat.get("section_id") instanceof Number ? ((Number)stat.get("section_id")).longValue() : null;
            String className = classId != null ? classNames.getOrDefault(classId.toString(), null) : null;
            String courseSectionName = (courseId != null && sectionId != null) ? courseSectionNames.getOrDefault(courseId + "-" + sectionId, null) : null;
            if ((avgScore != null && avgScore < 60) || (passRate != null && passRate < 0.6)) {
                advice.append(
                        (className != null ? ("班级:" + className + " ") : "") +
                                (courseSectionName != null ? ("章节:" + courseSectionName + " ") : "") +
                                String.format("平均分:%.2f 通过率:%.2f。建议加强该章节讲解。\n", avgScore != null ? avgScore : 0, passRate != null ? passRate : 0)
                );
                count++;
            }
        }
        if (count == 0) {
            advice.append("暂无明显薄弱环节，整体通过率良好。");
        }
        metrics.put("courseOptimizationDirection", advice.toString());
        metrics.put("classNames", classNames);
        metrics.put("courseSectionNames", courseSectionNames);

        return metrics;
    }

    /**
     * 获取学生学习效果数据
     *
     * @param start 开始日期
     * @param end 结束日期
     * @return 包含平均正确率趋势、知识点掌握情况、高频错误知识点等统计的Map
     */
    public Map<String, Object> getStudentLearningEffect(LocalDate start, LocalDate end) {
        Map<String, Object> effect = new HashMap<>();

        // 平均正确率趋势 & 知识点掌握情况 (按课程-章节)
        List<Map<String, Object>> correctnessBySection = dashboardMapper.getAverageCorrectnessBySection(start, end);
        effect.put("correctnessBySection", correctnessBySection);

        // 高频错误知识点
        List<Map<String, Object>> topWrongKnowledgePoints = dashboardMapper.getTopWrongKnowledgePoints(start, end);
        effect.put("topWrongKnowledgePoints", topWrongKnowledgePoints);

        // 使用 CourseClient 获取所有课程和章节信息
        List<Map<String, Object>> allCourses = courseClient.getAllCourses();
        List<Map<String, Object>> allCoursesAndSections = new java.util.ArrayList<>();
        if (allCourses != null) {
            for (Map<String, Object> course : allCourses) {
                Long courseId = ((Number) course.get("id")).longValue();
                String courseName = course.get("name") != null ? course.get("name").toString() : "";
                List<Map<String, Object>> sections = courseClient.getSectionsByCourseId(courseId);
                if (sections != null) {
                    for (Map<String, Object> section : sections) {
                        Map<String, Object> entry = new HashMap<>();
                        entry.put("course_id", courseId);
                        entry.put("course_name", courseName);
                        entry.put("section_id", section.get("id"));
                        entry.put("section_name", section.get("title"));
                        allCoursesAndSections.add(entry);
                    }
                }
            }
        }
        Map<String, String> courseSectionNames = allCoursesAndSections.stream()
                .collect(Collectors.toMap(
                        map -> map.get("course_id") + "-" + map.get("section_id"),
                        map -> map.get("course_name") + " - " + map.get("section_name")
                ));
        effect.put("courseSectionNames", courseSectionNames);

        return effect;
    }

    /**
     * 组合今日 / 本周两套数据，用于大屏概览。
     */
    public Map<String, Object> getDashboardOverview() {
        Map<String, Object> overview = new HashMap<>();

        LocalDate today = LocalDate.now();
        logger.info("获取仪表盘概览数据，日期: {}", today);

        try {
            // 今天的数据
            Map<String, Object> todayStats = getBasicStatistics(today, today);
            Map<String, Object> todayEfficiency = getTeachingEfficiencyMetrics(today, today);
            Map<String, Object> todayLearning = getStudentLearningEffect(today, today);
            overview.put("today", todayStats);
            overview.put("todayEfficiency", todayEfficiency);
            overview.put("todayLearningEffect", todayLearning);

            // 本周（过去 7 天含今日）
            LocalDate weekStart = today.minusDays(6);
            Map<String, Object> weekStats = getBasicStatistics(weekStart, today);
            Map<String, Object> weekEfficiency = getTeachingEfficiencyMetrics(weekStart, today);
            Map<String, Object> weekLearning = getStudentLearningEffect(weekStart, today);
            overview.put("week", weekStats);
            overview.put("weekEfficiency", weekEfficiency);
            overview.put("weekLearningEffect", weekLearning);
        } catch (Exception e) {
            logger.error("获取仪表盘数据失败: {}", e.getMessage());
            // 在出错时，返回空数据而不是抛出异常
            overview.put("error", "获取仪表盘数据失败: " + e.getMessage());
        }

        return overview;
    }
}
