package org.example.edusoft.learning.entity;
import java.time.LocalDateTime;
import java.util.List;
import lombok.Data;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import jakarta.persistence.*;

@NoArgsConstructor  // 添加无参构造函数
@AllArgsConstructor // 添加全参构造函数
@Entity
@Data
public class PracticeRecord {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    private Long practiceId;
    private Long studentId;
    private LocalDateTime submittedAt;
    private Integer score;
    private String feedback;
    
    @Transient  // 该字段不会被持久化到数据库
    private List<QuestionRecord> questions;  // 添加题目记录列表
    
    private String practiceTitle;
    private String courseName;    // 添加课程名称
    private String className;     // 添加班级名称
    
    // 添加课程ID和班级ID字段
    private Long courseId;
    private Long classId;
}