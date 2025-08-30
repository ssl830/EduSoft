package org.example.edusoft.learning.controller;

import lombok.RequiredArgsConstructor;
import org.example.edusoft.learning.Result;
import org.example.edusoft.learning.dto.HomeworkDTO;
import org.example.edusoft.learning.dto.HomeworkSubmissionDTO;
import org.example.edusoft.learning.service.HomeworkService;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
//import javax.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpServletResponse;

import java.util.List;

@RestController
@RequestMapping("/api/learning/homework")
@RequiredArgsConstructor
public class HomeworkController {

    private final HomeworkService homeworkService;

    @PostMapping
    public Result<Long> createHomework(
            @RequestParam("class_id") Long classId,
            @RequestParam("title") String title,
            @RequestParam(value = "description", required = false) String description,
            @RequestParam(value = "end_time", required = false) String endTime,
            @RequestParam(value = "file", required = false) MultipartFile file,
            @RequestHeader("X-User-Id") Long userId) { // 从网关获取用户ID
        try {
            Long homeworkId = homeworkService.createHomework(classId, title, description, endTime, file, userId);
            return Result.success(homeworkId, "作业创建成功");
        } catch (Exception e) {
            return Result.error("作业创建失败：" + e.getMessage());
        }
    }

    @GetMapping("/{id}")
    public Result<HomeworkDTO> getHomework(@PathVariable Long id) {
        try {
            HomeworkDTO homework = homeworkService.getHomework(id);
            return Result.success(homework, "获取作业详情成功");
        } catch (Exception e) {
            return Result.error("获取作业详情失败：" + e.getMessage());
        }
    }

    @GetMapping("/class/{classId}")
    public Result<List<HomeworkDTO>> getHomeworkList(@PathVariable Long classId) {
        try {
            List<HomeworkDTO> homeworkList = homeworkService.getHomeworkList(classId);
            return Result.success(homeworkList, "获取作业列表成功");
        } catch (Exception e) {
            return Result.error("获取作业列表失败：" + e.getMessage());
        }
    }

    @PostMapping("/{homeworkId}/submit")
    public Result<Long> submitHomework(
            @PathVariable Long homeworkId,
            @RequestHeader("X-User-Id") Long studentId,
            @RequestPart("file") MultipartFile file) {
        try {
            Long submissionId = homeworkService.submitHomework(homeworkId, studentId, file);
            return Result.success(submissionId, "作业提交成功");
        } catch (Exception e) {
            return Result.error("作业提交失败：" + e.getMessage());
        }
    }

    @GetMapping("/{homeworkId}/submissions")
    public Result<List<HomeworkSubmissionDTO>> getSubmissionList(@PathVariable Long homeworkId) {
        try {
            List<HomeworkSubmissionDTO> submissionList = homeworkService.getSubmissionList(homeworkId);
            return Result.success(submissionList, "获取提交列表成功");
        } catch (Exception e) {
            return Result.error("获取提交列表失败：" + e.getMessage());
        }
    }

    @GetMapping("/{homeworkId}/submission")
    public Result<HomeworkSubmissionDTO> getStudentSubmission(
            @PathVariable Long homeworkId,
            @RequestHeader("X-User-Id") Long studentId) {
        try {
            HomeworkSubmissionDTO submission = homeworkService.getStudentSubmission(homeworkId, studentId);
            return Result.success(submission, "获取提交记录成功");
        } catch (Exception e) {
            return Result.error("获取提交记录失败：" + e.getMessage());
        }
    }

    @GetMapping("/{homeworkId}/download")
    public void downloadHomeworkFile(@PathVariable Long homeworkId, HttpServletResponse response) {
        try {
            homeworkService.downloadHomeworkFile(homeworkId, response);
        } catch (Exception e) {
            // 异常处理
        }
    }



    @GetMapping("/submission/{submissionId}/download")
    public void downloadSubmissionFile(@PathVariable Long submissionId, HttpServletResponse response) {
        try {
            homeworkService.downloadSubmissionFile(submissionId, response);
        } catch (Exception e) {
            // 异常处理
        }
    }

    @DeleteMapping("/{homeworkId}")
    public Result<Void> deleteHomework(@PathVariable Long homeworkId) {
        try {
            homeworkService.deleteHomework(homeworkId);
            return Result.success(null, "作业删除成功");
        } catch (Exception e) {
            return Result.error("作业删除失败：" + e.getMessage());
        }
    }
}
