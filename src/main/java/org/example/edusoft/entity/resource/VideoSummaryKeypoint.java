package org.example.edusoft.entity.resource;

import lombok.Data;

/**
 * 视频知识点实体类
 */
@Data
public class VideoSummaryKeypoint {
    /**
     * 知识点ID
     */
    private Long id;

    /**
     * 关联的摘要ID
     */
    private Long summaryId;

    /**
     * 知识点内容
     */
    private String point;

    /**
     * 相关时间点(秒)
     */
    private Integer timestamp;

    /**
     * 重要程度
     */
    private String importance;
}
