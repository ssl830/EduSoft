package org.example.edusoft.content.controller;

import org.example.edusoft.content.common.Result;
import org.example.edusoft.content.dto.progress.LearningProgressDTO;
import org.example.edusoft.content.dto.progress.ProgressStatisticsDTO;
import org.example.edusoft.content.dto.progress.ProgressUpdateRequest;
import org.example.edusoft.content.entity.resource.LearningProgress;
import org.example.edusoft.content.service.progress.LearningProgressService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;
import java.util.List;

/**
 * 学习进度管理控制器
 */
@RestController
@RequestMapping("/api/content/learning-progress")
@CrossOrigin(origins = "*")
public class LearningProgressController {

    @Autowired
    private LearningProgressService learningProgressService;

    /**
     * 更新学习进度
     */
    @PostMapping("/update")
    public Result<LearningProgress> updateProgress(@Valid @RequestBody ProgressUpdateRequest request) {
        try {
            LearningProgress progress = learningProgressService.updateProgress(
                request.getResourceId(),
                request.getStudentId(),
                request.getProgress(),
                request.getPosition()
            );
            return Result.success(progress, "学习进度更新成功");
        } catch (Exception e) {
            return Result.error("学习进度更新失败：" + e.getMessage());
        }
    }

    /**
     * 获取学习进度
     */
    @GetMapping("/{resourceId}/{studentId}")
    public Result<LearningProgress> getProgress(
            @PathVariable Long resourceId,
            @PathVariable Long studentId) {
        try {
            LearningProgress progress = learningProgressService.getProgress(resourceId, studentId);
            if (progress == null) {
                return Result.error("未找到学习进度记录");
            }
            return Result.success(progress, "获取学习进度成功");
        } catch (Exception e) {
            return Result.error("获取学习进度失败：" + e.getMessage());
        }
    }

    /**
     * 获取学生的所有学习进度
     */
    @GetMapping("/student/{studentId}")
    public Result<List<LearningProgressDTO>> getStudentProgress(@PathVariable Long studentId) {
        try {
            List<LearningProgressDTO> progressList = learningProgressService.getStudentProgress(studentId);
            return Result.success(progressList, "获取学生学习进度成功");
        } catch (Exception e) {
            return Result.error("获取学生学习进度失败：" + e.getMessage());
        }
    }

    /**
     * 获取资源的所有学习进度
     */
    @GetMapping("/resource/{resourceId}")
    public Result<List<LearningProgressDTO>> getResourceProgress(@PathVariable Long resourceId) {
        try {
            List<LearningProgressDTO> progressList = learningProgressService.getResourceProgress(resourceId);
            return Result.success(progressList, "获取资源学习进度成功");
        } catch (Exception e) {
            return Result.error("获取资源学习进度失败：" + e.getMessage());
        }
    }

    /**
     * 获取课程的学习进度统计
     */
    @GetMapping("/statistics/course/{courseId}")
    public Result<List<ProgressStatisticsDTO>> getCourseProgressStatistics(@PathVariable Long courseId) {
        try {
            List<ProgressStatisticsDTO> statistics = learningProgressService.getCourseProgressStatistics(courseId);
            return Result.success(statistics, "获取课程学习进度统计成功");
        } catch (Exception e) {
            return Result.error("获取课程学习进度统计失败：" + e.getMessage());
        }
    }

    /**
     * 获取章节的学习进度统计
     */
    @GetMapping("/statistics/chapter/{chapterId}")
    public Result<List<ProgressStatisticsDTO>> getChapterProgressStatistics(@PathVariable Long chapterId) {
        try {
            List<ProgressStatisticsDTO> statistics = learningProgressService.getChapterProgressStatistics(chapterId);
            return Result.success(statistics, "获取章节学习进度统计成功");
        } catch (Exception e) {
            return Result.error("获取章节学习进度统计失败：" + e.getMessage());
        }
    }

    /**
     * 删除学习进度
     */
    @DeleteMapping("/{resourceId}/{studentId}")
    public Result<Void> deleteProgress(
            @PathVariable Long resourceId,
            @PathVariable Long studentId) {
        try {
            boolean success = learningProgressService.deleteProgress(resourceId, studentId);
            if (success) {
                return Result.success(null, "学习进度删除成功");
            } else {
                return Result.error("学习进度删除失败");
            }
        } catch (Exception e) {
            return Result.error("学习进度删除失败：" + e.getMessage());
        }
    }

    /**
     * 批量更新学习进度
     */
    @PostMapping("/batch-update")
    public Result<List<LearningProgress>> batchUpdateProgress(@RequestBody List<LearningProgress> progressList) {
        try {
            List<LearningProgress> updatedProgress = learningProgressService.batchUpdateProgress(progressList);
            return Result.success(updatedProgress, "批量更新学习进度成功");
        } catch (Exception e) {
            return Result.error("批量更新学习进度失败：" + e.getMessage());
        }
    }
}
