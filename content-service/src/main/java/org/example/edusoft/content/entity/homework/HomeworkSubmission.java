package org.example.edusoft.content.entity.homework;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.time.LocalDateTime;

@Entity
@Table(name = "homework_submissions")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class HomeworkSubmission {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "homework_id", nullable = false)
    private Long homeworkId;
    
    @Column(name = "student_id", nullable = false)
    private Long studentId;
    
    @Column(name = "student_name", length = 100)
    private String studentName;
    
    @Column(name = "content", columnDefinition = "TEXT")
    private String content;
    
    @Column(name = "file_url")
    private String fileUrl;
    
    @Column(name = "file_name")
    private String fileName;
    
    @Column(name = "status", length = 20)
    private String status = "submitted";
    
    @Column(name = "feedback", columnDefinition = "TEXT")
    private String feedback;
    
    @Column(name = "score")
    private Integer score;
    
    @Column(name = "submitted_at")
    private LocalDateTime submittedAt;
    
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
    
    @PrePersist
    protected void onCreate() {
        submittedAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }
    
    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}
