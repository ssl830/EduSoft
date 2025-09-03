package org.example.edusoft.content.entity.resource;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.time.LocalDateTime;

@Entity
@Table(name = "learning_progress")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class LearningProgress {
    
    @Id
    /**
     * 进度记录ID
     */
    private Long id;

    /**
     * 教学资源ID
     */
    private Long resourceId;

    /**
     * 学生ID
     */
    private Long studentId;

    /**
     * 学习进度（秒）
     */
    private Integer progress;

    /**
     * 最后观看位置（秒）
     */
    private Integer lastPosition;

    /**
     * 观看次数
     */
    private Integer watchCount;

    /**
     * 最后观看时间
     */
    private LocalDateTime lastWatchTime;

    /**
     * 创建时间
     */
    private LocalDateTime createdAt;

    /**
     * 更新时间
     */
    private LocalDateTime updatedAt;

    /**
     * 版本号（用于乐观锁）
     */
    private Integer version;
    
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
        lastWatchTime = LocalDateTime.now();
    }
    
    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
        lastWatchTime = LocalDateTime.now();
    }
} 