package org.example.edusoft.entity.resource;

import lombok.Data;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import java.time.LocalDateTime;

/**
 * 教学资源与学习进度合并DTO
 */
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ResourceProgressDTO {
    /**
     * 学习记录ID
     */
    private Long learningrecordId;

    /**
     * 资源ID
     */
    private Long resourceId;

    /**
     * 学生ID
     */
    private Long studentId;

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
     * 带签名的文件访问URL
     */
    private String fileUrl;

    /**
     * 视频时长（秒）
     */
    private Integer duration;

    /**
     * 最后观看时间字符串
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private String lastWatch;

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
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private String lastWatchTime;

    /**
     * 创建时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private String createdAt;

    /**
     * 更新时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private String updatedAt;

    /**
     * 版本号
     */
    private Integer version;
} 