package org.example.edusoft.entity.course;

import lombok.Data;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class CourseDetailDTO {
    private Long id;
    private Long teacherId;
    private String teacherName;    // 教师姓名
    private String name;           // 课程名称
    private String code;           // 课程代码
    private String outline;        // 课程大纲
    private String objective;      // 教学目标
    private String assessment;     // 考核方式
    private LocalDateTime createdAt;
    private List<CourseSection> sections;  // 课程章节列表
    private List<ClassInfo> classes;       // 课程班级列表
    private Integer studentCount;          // 学生总数
    private Integer practiceCount;         // 练习总数
    private Integer homeworkCount;         // 作业总数
    private Integer resourceCount;         // 资源总数

    @Data
    public static class ClassInfo {
        private Long id;
        private String name;
        private String classCode;
        private Integer studentCount;
    }
}