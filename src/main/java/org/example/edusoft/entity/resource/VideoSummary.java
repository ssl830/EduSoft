package org.example.edusoft.entity.resource;

import lombok.Data;
import java.time.LocalDateTime;

/**
 * 视频摘要实体类
 */
@Data
public class VideoSummary {
    /**
     * 摘要ID
     */
    private Long id;

    /**
     * 关联的教学资源ID
     */
    private Long resourceId;

    /**
     * 视频标题
     */
    private String videoTitle;

    /**
     * 课程名称
     */
    private String courseName;

    /**
     * 视频内容概述
     */
    private String summary;

    /**
     * 视频转写文本
     */
    private String transcript;

    /**
     * 视频时长(秒)
     */
    private Integer duration;

    /**
     * 创建时间
     */
    private LocalDateTime createdAt;

    /**
     * 更新时间
     */
    private LocalDateTime updatedAt;
}
