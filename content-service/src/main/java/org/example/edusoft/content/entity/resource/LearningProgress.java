package org.example.edusoft.entity.resource;

import lombok.Data;
import java.time.LocalDateTime;
 
/**
 * 学习进度实体类
 */
@Data
public class LearningProgress {
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
} 