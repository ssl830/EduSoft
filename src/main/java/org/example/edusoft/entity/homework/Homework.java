package org.example.edusoft.entity.homework;

import java.time.LocalDateTime;
import lombok.Data;

/**
 * 作业实体类
 */
@Data
public class Homework {
    /**
     * 作业ID
     */
    private Long id;
    
    /**
     * 作业标题
     */
    private String title;
    
    /**
     * 作业描述
     */
    private String description;
    
    /**
     * 班级ID
     */
    private Long classId;
    
    /**
     * 创建者ID（教师）
     */
    private Long createdBy;
    
    /**
     * 作业附件URL（阿里云OSS）
     */
    private String attachmentUrl;
    
    /**
     * 对象存储中的文件路径
     */
    private String objectName;
    
    /**
     * 截止时间
     */
    private LocalDateTime deadline;
    
    /**
     * 创建时间
     */
    private LocalDateTime createdAt;
    
    /**
     * 更新时间
     */
    private LocalDateTime updatedAt;
} 