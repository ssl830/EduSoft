package org.example.edusoft.content.entity.homework;

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
     * 课程ID
     */
    private Long courseId;
    
    /**
     * 章节ID
     */
    private Long chapterId;
    
    /**
     * 章节名称
     */
    private String chapterName;
    
    /**
     * 班级ID
     */
    private Long classId;
    
    /**
     * 创建者ID（教师）
     */
    private Long createdBy;
    
    /**
     * 创建者姓名
     */
    private String createdByName;
    
    /**
     * 作业附件URL（阿里云OSS）
     */
    private String attachmentUrl;
    
    /**
     * 对象存储中的文件路径
     * 格式：homework/{classId}/{fileName}
     */
    private String objectName;
    
    /**
     * 文件名
     */
    private String fileName;
    
    /**
     * 截止时间
     */
    private LocalDateTime deadline;
    
    /**
     * 状态
     */
    private String status;
    
    /**
     * 提交数量
     */
    private Integer submissionCount;
    
    /**
     * 创建时间
     */
    private LocalDateTime createdAt;
    
    /**
     * 更新时间
     */
    private LocalDateTime updatedAt;
} 