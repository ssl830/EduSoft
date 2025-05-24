package org.example.edusoft.entity.resource;

import lombok.Data;
import java.time.LocalDateTime;

/**
 * 教学资源实体类
 */
@Data
public class TeachingResource {
    /**
     * 资源ID
     */
    private Long id;

    /**
     * 资源标题
     */
    private String title;

    /**
     * 资源描述
     */
    private String description;

    /**
     * 所属课程ID
     */
    private Long courseId;

    /**
     * 所属章节ID
     */
    private Long chapterId;

    /**
     * 章节名称
     */
    private String chapterName;

    /**
     * 资源类型（VIDEO/DOCUMENT等）
     */
    private String resourceType;

    /**
     * 文件URL
     */
    private String fileUrl;

    /**
     * 对象存储中的文件名
     */
    private String objectName;

    /**
     * 视频时长（秒）
     */
    private Integer duration;

    /**
     * 创建者ID
     */
    private Long createdBy;

    /**
     * 创建时间
     */
    private LocalDateTime createdAt;

    /**
     * 更新时间
     */
    private LocalDateTime updatedAt;
} 