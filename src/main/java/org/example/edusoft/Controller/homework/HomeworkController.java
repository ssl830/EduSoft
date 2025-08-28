package org.example.edusoft.controller.homework;

import lombok.RequiredArgsConstructor;
import org.example.edusoft.common.domain.Result;
import org.example.edusoft.dto.homework.HomeworkDTO;
import org.example.edusoft.dto.homework.HomeworkSubmissionDTO;
import org.example.edusoft.service.homework.HomeworkService;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import jakarta.servlet.http.HttpServletResponse;
import org.example.edusoft.entity.homework.Homework;
import org.example.edusoft.entity.homework.HomeworkSubmission;

import java.util.List;

/**
 * 作业管理控制器
 */
@RestController
@RequestMapping("/api/homework")
@RequiredArgsConstructor
public class HomeworkController {

    private final HomeworkService homeworkService;

    /**
     * 创建作业
     */
    @PostMapping("/create")
    public Result<Long> createHomework(
            @RequestParam("class_id") Long classId,
            @RequestParam("title") String title,
            @RequestParam(value = "description", required = false) String description,
            @RequestParam(value = "end_time", required = false) String endTime,
            @RequestParam(value = "file", required = false) MultipartFile file) {
        try {
            Long homeworkId = homeworkService.createHomework(classId, title, description, endTime, file);
            return Result.ok(homeworkId, "作业创建成功");
        } catch (Exception e) {
            return Result.error("作业创建失败：" + e.getMessage());
        }
    }

    /**
     * 获取作业详情
     * @param id 作业ID
     * @return 作业信息
     */
    @GetMapping("/{id}")
    public Result<HomeworkDTO> getHomework(@PathVariable Long id) {
        try {
            HomeworkDTO homework = homeworkService.getHomework(id);
            if (homework == null) {
                return Result.error("作业不存在");
            }
            return Result.ok(homework, "获取作业详情成功");
        } catch (Exception e) {
            return Result.error("获取作业详情失败：" + e.getMessage());
        }
    }

    /**
     * 获取班级作业列表 
     * @param classId 班级ID
     * @return 作业列表
     */
    @GetMapping("/list")
    public Result<List<HomeworkDTO>> getHomeworkList(@RequestParam Long classId) {
        try {
            List<HomeworkDTO> homeworkList = homeworkService.getHomeworkList(classId);
            return Result.ok(homeworkList, "获取作业列表成功");
        } catch (Exception e) {
            return Result.error("获取作业列表失败：" + e.getMessage());
        }
    }

    /**
     * 提交作业
     * @param homeworkId 作业ID
     * @param studentId 学生ID
     * @param submissionType 提交类型
     * @param file 提交的文件
     * @return 提交记录ID
     */
    @PostMapping("/submit/{homeworkId}")
    public Result<Long> submitHomework(
            @PathVariable Long homeworkId,
            @RequestParam Long student_id,
            @RequestPart("file") MultipartFile file) {
        try {
            Long submissionId = homeworkService.submitHomework(homeworkId, student_id, file);
            return Result.ok(submissionId, "作业提交成功");
        } catch (Exception e) {
            return Result.error("作业提交失败：" + e.getMessage());
        }
    }

    /**
     * 获取作业提交列表
     * @param homeworkId 作业ID
     * @return 提交记录列表
     */
    @GetMapping("/submissions/{homeworkId}")
    public Result<List<HomeworkSubmissionDTO>> getSubmissionList(@PathVariable Long homeworkId) {
        try {
            List<HomeworkSubmissionDTO> submissions = homeworkService.getSubmissionList(homeworkId);
            return Result.ok(submissions, "获取提交列表成功");
        } catch (Exception e) {
            return Result.error("获取提交列表失败：" + e.getMessage());
        }
    }

    /**
     * 获取学生的提交记录
     * @param homeworkId 作业ID
     * @param studentId 学生ID
     * @return 提交记录
     */
    @GetMapping("/submission")
    public Result<HomeworkSubmissionDTO> getStudentSubmission(
            @RequestParam Long homeworkId,
            @RequestParam Long studentId) {
        try {
            HomeworkSubmissionDTO submission = homeworkService.getStudentSubmission(homeworkId, studentId);
            if (submission == null) {
                return Result.error("未找到提交记录");
            }
            return Result.ok(submission, "获取提交记录成功");
        } catch (Exception e) {
            return Result.error("获取提交记录失败：" + e.getMessage());
        }
    }

    /**
     * 下载作业附件
     * @param homeworkId 作业ID
     * @param response HTTP响应对象
     */
    @GetMapping("/file/{homeworkId}")
    public void downloadHomeworkFile(
            @PathVariable Long homeworkId,
            HttpServletResponse response) {
        try {
            homeworkService.downloadHomeworkFile(homeworkId, response);
        } catch (Exception e) {
            throw new RuntimeException("文件下载失败：" + e.getMessage());
        }
    }

    /**
     * 下载提交的作业文件
     * @param submissionId 提交记录ID
     * @param response HTTP响应对象
     */
    @GetMapping("/submission/file/{submissionId}")
    public void downloadSubmissionFile(
            @PathVariable Long submissionId,
            HttpServletResponse response) {
        try {
            homeworkService.downloadSubmissionFile(submissionId, response);
        } catch (Exception e) {
            throw new RuntimeException("文件下载失败：" + e.getMessage());
        }
    }

    /**
     * 删除作业
     * @param homeworkId 作业ID
     * @return 操作结果
     */
    @DeleteMapping("/{homeworkId}")
    public Result<Void> deleteHomework(@PathVariable Long homeworkId) {
        try {
            homeworkService.deleteHomework(homeworkId);
            return Result.ok(null, "作业删除成功");
        } catch (Exception e) {
            return Result.error("作业删除失败：" + e.getMessage());
        }
    }
} 