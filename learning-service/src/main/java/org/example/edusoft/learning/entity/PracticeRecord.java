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

    // 手动添加getter/setter方法
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getPracticeId() {
        return practiceId;
    }

    public void setPracticeId(Long practiceId) {
        this.practiceId = practiceId;
    }

    public Long getStudentId() {
        return studentId;
    }

    public void setStudentId(Long studentId) {
        this.studentId = studentId;
    }

    public LocalDateTime getSubmittedAt() {
        return submittedAt;
    }

    public void setSubmittedAt(LocalDateTime submittedAt) {
        this.submittedAt = submittedAt;
    }

    public Integer getScore() {
        return score;
    }

    public void setScore(Integer score) {
        this.score = score;
    }

    public String getFeedback() {
        return feedback;
    }

    public void setFeedback(String feedback) {
        this.feedback = feedback;
    }

    public List<QuestionRecord> getQuestions() {
        return questions;
    }

    public void setQuestions(List<QuestionRecord> questions) {
        this.questions = questions;
    }

    public String getPracticeTitle() {
        return practiceTitle;
    }

    public void setPracticeTitle(String practiceTitle) {
        this.practiceTitle = practiceTitle;
    }

    public String getCourseName() {
        return courseName;
    }

    public void setCourseName(String courseName) {
        this.courseName = courseName;
    }

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public Long getCourseId() {
        return courseId;
    }

    public void setCourseId(Long courseId) {
        this.courseId = courseId;
    }

    public Long getClassId() {
        return classId;
    }

    public void setClassId(Long classId) {
        this.classId = classId;
    }
}