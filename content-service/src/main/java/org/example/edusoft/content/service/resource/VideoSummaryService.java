package org.example.edusoft.service.resource;

import org.example.edusoft.dto.resource.VideoSummaryDetailDTO;

/**
 * 视频摘要服务接口
 */
public interface VideoSummaryService {

    /**
     * 为教学资源生成视频摘要
     * @param resourceId 教学资源ID
     * @return 视频摘要详情
     */
    VideoSummaryDetailDTO generateSummaryForResource(Long resourceId);

    /**
     * 根据资源ID获取视频摘要
     * @param resourceId 教学资源ID
     * @return 视频摘要详情，如果不存在则返回null
     */
    VideoSummaryDetailDTO getSummaryByResourceId(Long resourceId);

    /**
     * 根据摘要ID获取视频摘要
     * @param summaryId 摘要ID
     * @return 视频摘要详情，如果不存在则返回null
     */
    VideoSummaryDetailDTO getSummaryById(Long summaryId);

    /**
     * 删除视频摘要
     * @param summaryId 摘要ID
     * @return 是否删除成功
     */
    boolean deleteSummary(Long summaryId);

    /**
     * 根据资源ID删除视频摘要
     * @param resourceId 教学资源ID
     * @return 是否删除成功
     */
    boolean deleteSummaryByResourceId(Long resourceId);

    /**
     * 重新生成视频摘要
     * @param resourceId 教学资源ID
     * @return 新的视频摘要详情
     */
    VideoSummaryDetailDTO regenerateSummary(Long resourceId);
}
