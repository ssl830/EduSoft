package org.example.edusoft.entity.resource;

import lombok.Data;

/**
 * 视频摘要阶段实体类
 */
@Data
public class VideoSummaryStage {
    /**
     * 阶段ID
     */
    private Long id;

    /**
     * 关联的摘要ID
     */
    private Long summaryId;

    /**
     * 阶段标题
     */
    private String title;

    /**
     * 开始时间(秒)
     */
    private Integer startTime;

    /**
     * 结束时间(秒)
     */
    private Integer endTime;

    /**
     * 阶段描述
     */
    private String description;

    /**
     * 排序序号
     */
    private Integer sortOrder;
}
