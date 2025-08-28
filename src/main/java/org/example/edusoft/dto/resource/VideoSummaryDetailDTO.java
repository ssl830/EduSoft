package org.example.edusoft.dto.resource;

import lombok.Data;
import org.example.edusoft.entity.resource.VideoSummaryKeypoint;
import org.example.edusoft.entity.resource.VideoSummaryStage;
import org.example.edusoft.entity.resource.VideoSummaryTimestamp;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 视频摘要详情DTO
 */
@Data
public class VideoSummaryDetailDTO {
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

    /**
     * 阶段列表
     */
    private List<VideoSummaryStage> stages;

    /**
     * 知识点列表
     */
    private List<VideoSummaryKeypoint> keyPoints;

    /**
     * 重要时间点列表
     */
    private List<VideoSummaryTimestamp> keyTimestamps;
}
