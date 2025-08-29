package org.example.edusoft.controller.resource;

import lombok.extern.slf4j.Slf4j;
import org.example.edusoft.common.Result;
import org.example.edusoft.dto.resource.VideoSummaryDetailDTO;
import org.example.edusoft.service.resource.VideoSummaryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * 视频摘要控制器
 */
@Slf4j
@RestController
@RequestMapping("/api/video-summary")
public class VideoSummaryController {

    @Autowired
    private VideoSummaryService videoSummaryService;

    /**
     * 为教学资源生成视频摘要
     */
    @PostMapping("/generate/{resourceId}")
    public Result<VideoSummaryDetailDTO> generateSummary(@PathVariable Long resourceId) {
        try {
            VideoSummaryDetailDTO summary = videoSummaryService.generateSummaryForResource(resourceId);
            return Result.success(summary);
        } catch (Exception e) {
            log.error("生成视频摘要失败, resourceId: " + resourceId, e);
            return Result.error("生成视频摘要失败: " + e.getMessage());
        }
    }

    /**
     * 根据资源ID获取视频摘要
     */
    @GetMapping("/resource/{resourceId}")
    public Result<VideoSummaryDetailDTO> getSummaryByResourceId(@PathVariable Long resourceId) {
        try {
            VideoSummaryDetailDTO summary = videoSummaryService.getSummaryByResourceId(resourceId);
            if (summary == null) {
                return Result.error("视频摘要不存在");
            }
            return Result.success(summary);
        } catch (Exception e) {
            log.error("获取视频摘要失败, resourceId: " + resourceId, e);
            return Result.error("获取视频摘要失败: " + e.getMessage());
        }
    }

    /**
     * 根据摘要ID获取视频摘要
     */
    @GetMapping("/{summaryId}")
    public Result<VideoSummaryDetailDTO> getSummaryById(@PathVariable Long summaryId) {
        try {
            VideoSummaryDetailDTO summary = videoSummaryService.getSummaryById(summaryId);
            if (summary == null) {
                return Result.error("视频摘要不存在");
            }
            return Result.success(summary);
        } catch (Exception e) {
            log.error("获取视频摘要失败, summaryId: " + summaryId, e);
            return Result.error("获取视频摘要失败: " + e.getMessage());
        }
    }

    /**
     * 重新生成视频摘要
     */
    @PostMapping("/regenerate/{resourceId}")
    public Result<VideoSummaryDetailDTO> regenerateSummary(@PathVariable Long resourceId) {
        try {
            VideoSummaryDetailDTO summary = videoSummaryService.regenerateSummary(resourceId);
            return Result.success(summary);
        } catch (Exception e) {
            log.error("重新生成视频摘要失败, resourceId: " + resourceId, e);
            return Result.error("重新生成视频摘要失败: " + e.getMessage());
        }
    }

    /**
     * 删除视频摘要
     */
    @DeleteMapping("/{summaryId}")
    public Result<String> deleteSummary(@PathVariable Long summaryId) {
        try {
            boolean success = videoSummaryService.deleteSummary(summaryId);
            if (success) {
                return Result.success("删除成功");
            } else {
                return Result.error("删除失败");
            }
        } catch (Exception e) {
            log.error("删除视频摘要失败, summaryId: " + summaryId, e);
            return Result.error("删除视频摘要失败: " + e.getMessage());
        }
    }

    /**
     * 根据资源ID删除视频摘要
     */
    @DeleteMapping("/resource/{resourceId}")
    public Result<String> deleteSummaryByResourceId(@PathVariable Long resourceId) {
        try {
            boolean success = videoSummaryService.deleteSummaryByResourceId(resourceId);
            if (success) {
                return Result.success("删除成功");
            } else {
                return Result.error("删除失败");
            }
        } catch (Exception e) {
            log.error("删除视频摘要失败, resourceId: " + resourceId, e);
            return Result.error("删除视频摘要失败: " + e.getMessage());
        }
    }
}
