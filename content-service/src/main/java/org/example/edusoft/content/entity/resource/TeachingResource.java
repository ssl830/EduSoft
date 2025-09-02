package org.example.edusoft.content.entity.resource;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.time.LocalDateTime;

@Entity
@Table(name = "teaching_resources")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class TeachingResource {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "title", nullable = false, length = 255)
    private String title;
    
    @Column(name = "description", columnDefinition = "TEXT")
    private String description;
    
    @Column(name = "type", length = 50)
    private String type;
    
    @Column(name = "url", columnDefinition = "TEXT")
    private String url;
    
    @Column(name = "file_size")
    private Long fileSize;
    
    @Column(name = "duration")
    private Integer duration;
    
    @Column(name = "course_id", nullable = false)
    private Long courseId;
    
    @Column(name = "chapter_id")
    private Long chapterId;
    
    @Column(name = "creator_id", nullable = false)
    private Long creatorId;
    
    @Column(name = "created_at")
    private LocalDateTime createdAt;
    
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
    
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }
    
    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
} 