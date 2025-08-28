package org.example.edusoft.entity.resource;

import lombok.Data;

/**
 * 视频重要时间点实体类
 */
@Data
public class VideoSummaryTimestamp {
    /**
     * 时间点ID
     */
    private Long id;

    /**
     * 关联的摘要ID
     */
    private Long summaryId;

    /**
     * 时间点(秒)
     */
    private Integer time;

    /**
     * 重要事件或内容描述
     */
    private String event;

    /**
     * 事件类型
     */
    private String type;
}
