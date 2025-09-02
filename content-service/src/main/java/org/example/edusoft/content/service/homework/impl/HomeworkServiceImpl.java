package org.example.edusoft.content.service.homework.impl;

import lombok.extern.slf4j.Slf4j;
import org.example.edusoft.content.dto.homework.HomeworkDTO;
import org.example.edusoft.content.dto.homework.HomeworkSubmissionDTO;
import org.example.edusoft.content.entity.homework.Homework;
import org.example.edusoft.content.entity.homework.HomeworkSubmission;
import org.example.edusoft.content.mapper.homework.HomeworkMapper;
import org.example.edusoft.content.mapper.homework.HomeworkSubmissionMapper;
import org.example.edusoft.content.service.homework.HomeworkService;
import org.example.edusoft.content.service.file.FileUpload;
import org.example.edusoft.content.entity.file.FileType;
import org.example.edusoft.content.entity.file.FileInfo;
import org.example.edusoft.content.common.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 作业服务实现类
 */
@Slf4j
@Service
public class HomeworkServiceImpl implements HomeworkService {

    @Autowired
    private HomeworkMapper homeworkMapper;
    
    @Autowired
    private HomeworkSubmissionMapper submissionMapper;
    
    @Autowired
    private FileUpload fileUploadService;

    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    @Override
    @Transactional
    public Long createHomework(Long classId, String title, String description,
                             String endTime, MultipartFile file) {
        // 参数校验
        if (classId == null || title == null || title.trim().isEmpty()) {
            throw new RuntimeException("班级ID和作业标题不能为空");
        }

        // 创建作业实体
        Homework homework = new Homework();
        homework.setClassId(classId);
        homework.setTitle(title);
        homework.setDescription(description);
        homework.setEndTime(endTime);

        // 设置创建时间和更新时间
        LocalDateTime now = LocalDateTime.now();
        homework.setCreatedAt(now);
        homework.setUpdatedAt(now);

        // 上传附件（如果有）
        if (file != null && !file.isEmpty()) {
            // 使用新的上传接口
            Result<?> result = fileUploadService.upload(file, title, null, null, classId, "private", null, FileType.OTHER, null);
            if (result.getData() instanceof FileInfo fileInfo) {
                homework.setFileUrl(fileInfo.getUrl());
                homework.setFileName(fileInfo.getName());
            }
        }

        // 保存作业信息
        homeworkMapper.insert(homework);
        return homework.getId();
    }

    @Override
    public HomeworkDTO getHomework(Long id) {
        Homework homework = homeworkMapper.selectById(id);
        if (homework == null) {
            return null;
        }
        
        HomeworkDTO dto = new HomeworkDTO();
        dto.setId(homework.getId());
        dto.setTitle(homework.getTitle());
        dto.setDescription(homework.getDescription());
        dto.setClassId(homework.getClassId());
        dto.setFileUrl(homework.getFileUrl());
        dto.setFileName(homework.getFileName());
        dto.setEndTime(homework.getEndTime());
        dto.setCreatedAt(homework.getCreatedAt());
        dto.setUpdatedAt(homework.getUpdatedAt());
        
        return dto;
    }

    @Override
    public List<HomeworkDTO> getHomeworkList(Long classId) {
        List<Homework> homeworkList = homeworkMapper.selectByClassId(classId);
        return homeworkList.stream().map(homework -> {
            HomeworkDTO dto = new HomeworkDTO();
            dto.setId(homework.getId());
            dto.setTitle(homework.getTitle());
            dto.setDescription(homework.getDescription());
            dto.setClassId(homework.getClassId());
            dto.setFileUrl(homework.getFileUrl());
            dto.setFileName(homework.getFileName());
            dto.setEndTime(homework.getEndTime());
            dto.setCreatedAt(homework.getCreatedAt());
            dto.setUpdatedAt(homework.getUpdatedAt());
            return dto;
        }).collect(Collectors.toList());
    }

    @Override
    @Transactional
    public Long submitHomework(Long homeworkId, Long studentId, String content, MultipartFile file) {
        // 参数校验
        if (file == null || file.isEmpty()) {
            throw new RuntimeException("提交的文件不能为空");
        }

        // 检查作业是否存在
        Homework homework = homeworkMapper.selectById(homeworkId);
        if (homework == null) {
            throw new RuntimeException("作业不存在");
        }

        // 检查是否已过截止时间
        if (homework.getEndTime() != null && LocalDateTime.now().isAfter(LocalDateTime.parse(homework.getEndTime()))) {
            throw new RuntimeException("作业已过截止时间");
        }

        // 检查是否已经提交过
        HomeworkSubmission existingSubmission = submissionMapper.selectByHomeworkAndStudent(homeworkId, studentId);
        if (existingSubmission != null) {
            throw new RuntimeException("您已经提交过该作业");
        }

        // 创建提交记录
        HomeworkSubmission submission = new HomeworkSubmission();
        submission.setHomeworkId(homeworkId);
        submission.setStudentId(studentId);
        submission.setStudentName("学生" + studentId); // 简化处理
        submission.setContent(content);

        // 使用新的上传接口
        String title = file.getOriginalFilename();
        String folder = "homework-submission/" + homeworkId + "/" + studentId;
        Result<?> result = fileUploadService.upload(file, title, null, null, null, "private", null, FileType.OTHER, null);
        if (result.getData() instanceof FileInfo fileInfo) {
            submission.setFileUrl(fileInfo.getUrl());
            submission.setFileName(fileInfo.getName());
        }
        submission.setSubmittedAt(LocalDateTime.now());

        // 保存提交记录
        submissionMapper.insert(submission);
        return submission.getId();
    }

    @Override
    public List<HomeworkSubmissionDTO> getSubmissionList(Long homeworkId) {
        List<HomeworkSubmission> submissions = submissionMapper.selectByHomeworkId(homeworkId);
        return submissions.stream().map(submission -> {
            HomeworkSubmissionDTO dto = new HomeworkSubmissionDTO();
            dto.setId(submission.getId());
            dto.setHomeworkId(submission.getHomeworkId());
            dto.setStudentId(submission.getStudentId());
            dto.setStudentName(submission.getStudentName());
            dto.setFileUrl(submission.getFileUrl());
            dto.setFileName(submission.getFileName());
            dto.setSubmittedAt(submission.getSubmittedAt());
            return dto;
        }).collect(Collectors.toList());
    }

    @Override
    public HomeworkSubmissionDTO getStudentSubmission(Long homeworkId, Long studentId) {
        HomeworkSubmission submission = submissionMapper.selectByHomeworkAndStudent(homeworkId, studentId);
        if (submission == null) {
            return null;
        }
        
        HomeworkSubmissionDTO dto = new HomeworkSubmissionDTO();
        dto.setId(submission.getId());
        dto.setHomeworkId(submission.getHomeworkId());
        dto.setStudentId(submission.getStudentId());
        dto.setStudentName(submission.getStudentName());
        dto.setFileUrl(submission.getFileUrl());
        dto.setFileName(submission.getFileName());
        dto.setSubmittedAt(submission.getSubmittedAt());
        
        return dto;
    }

    @Override
    public void exportSubmissions(Long homeworkId, HttpServletResponse response) {
        // TODO: 实现导出功能
        log.info("导出作业提交列表，作业ID: {}", homeworkId);
    }
    
    @Override
    public void gradeHomework(Long submissionId, String feedback, Integer score) {
        HomeworkSubmission submission = submissionMapper.selectById(submissionId);
        if (submission != null) {
            submission.setFeedback(feedback);
            submission.setScore(score);
            submission.setUpdatedAt(LocalDateTime.now());
            submissionMapper.updateById(submission);
        }
    }
    
    @Override
    public void downloadHomeworkFile(Long homeworkId, HttpServletResponse response) {
        Homework homework = homeworkMapper.selectById(homeworkId);
        if (homework == null) {
            throw new RuntimeException("作业不存在");
        }
        
        // 简化的文件下载逻辑
        try {
            String fileName = homework.getFileName() != null ? homework.getFileName() : "homework_" + homeworkId + ".txt";
            response.setContentType("text/plain; charset=UTF-8");
            response.setHeader("Content-Disposition", "attachment; filename=\"" + fileName + "\"");
            
            // 生成作业内容
            String content = String.format("作业标题: %s\n作业描述: %s\n截止时间: %s\n创建时间: %s\n\n这是一个模拟的作业文件内容。",
                homework.getTitle(),
                homework.getDescription() != null ? homework.getDescription() : "无描述",
                homework.getEndTime() != null ? homework.getEndTime() : "无截止时间",
                homework.getCreatedAt() != null ? homework.getCreatedAt().toString() : "未知"
            );
            
            response.getWriter().write(content);
        } catch (IOException e) {
            throw new RuntimeException("文件下载失败", e);
        }
    }

    @Override
    public void downloadSubmissionFile(Long submissionId, HttpServletResponse response) {
        HomeworkSubmission submission = submissionMapper.selectById(submissionId);
        if (submission == null) {
            throw new RuntimeException("提交记录不存在");
        }
        
        // 简化的文件下载逻辑
        try {
            String fileName = "submission_" + submissionId + ".txt";
            response.setContentType("text/plain; charset=UTF-8");
            response.setHeader("Content-Disposition", "attachment; filename=\"" + fileName + "\"");
            
            // 生成提交内容
            String content = String.format("提交记录ID: %d\n作业ID: %d\n学生ID: %d\n学生姓名: %s\n提交时间: %s\n\n这是一个模拟的作业提交文件内容。",
                submission.getId(),
                submission.getHomeworkId(),
                submission.getStudentId(),
                submission.getStudentName() != null ? submission.getStudentName() : "未知",
                submission.getSubmittedAt() != null ? submission.getSubmittedAt().toString() : "未知"
            );
            
            response.getWriter().write(content);
        } catch (IOException e) {
            throw new RuntimeException("文件下载失败", e);
        }
    }

    @Override
    @Transactional
    public void deleteHomework(Long homeworkId) {
        // 删除作业的所有提交记录
        submissionMapper.deleteByHomeworkId(homeworkId);
        // 删除作业
        homeworkMapper.deleteById(homeworkId);
    }

    @Override
    public int getHomeworkCountByCourse(Long courseId) {
        if (courseId == null) {
            throw new RuntimeException("课程ID不能为空");
        }
        return homeworkMapper.countByCourseId(courseId);
    }
}
