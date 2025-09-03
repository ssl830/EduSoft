package org.example.edusoft.content.service.homework.impl;

import lombok.extern.slf4j.Slf4j;
import lombok.RequiredArgsConstructor;
import org.example.edusoft.content.dto.homework.HomeworkDTO;
import org.example.edusoft.content.dto.homework.HomeworkSubmissionDTO;
import org.example.edusoft.content.entity.homework.Homework;
import org.example.edusoft.content.entity.homework.HomeworkSubmission;
import org.example.edusoft.content.mapper.homework.HomeworkMapper;
import org.example.edusoft.content.mapper.homework.HomeworkSubmissionMapper;
import org.example.edusoft.content.service.homework.HomeworkService;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.example.edusoft.content.common.Result;
import org.example.edusoft.content.entity.file.FileType;
import org.example.edusoft.content.service.file.FileUpload;
import org.example.edusoft.content.service.file.FileAccessService;
import org.example.edusoft.content.entity.file.FileAccessDTO;
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
@RequiredArgsConstructor
public class HomeworkServiceImpl implements HomeworkService {

    private final HomeworkMapper homeworkMapper;
    private final HomeworkSubmissionMapper submissionMapper;
    private final FileUpload fileUpload;
    private final FileAccessService fileAccessService;

    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    @Override
    @Transactional
    public Long createHomework(Long classId, String title, String description,
                             String endTime, MultipartFile file, Long createdBy) {
        // 参数校验
        if (classId == null || title == null || title.trim().isEmpty()) {
            throw new RuntimeException("班级ID和作业标题不能为空");
        }

        // 创建作业实体
        Homework homework = new Homework();
        homework.setClassId(classId);
        homework.setTitle(title);
        homework.setDescription(description);
        homework.setCreatedBy(createdBy);

        if (endTime != null && !endTime.isEmpty()) {
            homework.setDeadline(LocalDateTime.parse(endTime, DATE_TIME_FORMATTER));
        }

        // 设置创建时间和更新时间
        LocalDateTime now = LocalDateTime.now();
        homework.setCreatedAt(now);
        homework.setUpdatedAt(now);

        // 上传附件（如果有）
        if (file != null && !file.isEmpty()) {
            String objectName = "homework/" + classId + "/" + file.getOriginalFilename();
            try {
                Result<?> uploadResult = fileUpload.uploadFile(file, file.getOriginalFilename(), 
                    null, null, "private", createdBy, FileType.HOMEWORK.name());
                if (uploadResult.isSuccess()) {
                    homework.setObjectName(objectName);
                    FileAccessDTO accessDTO = fileAccessService.getDownloadUrlByObjectName(objectName);
                    homework.setAttachmentUrl(accessDTO.getUrl());
                }
            } catch (JsonProcessingException e) {
                throw new RuntimeException("上传作业文件失败", e);
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
        
        return HomeworkDTO.builder()
                .id(homework.getId())
                .title(homework.getTitle())
                .description(homework.getDescription())
                .classId(homework.getClassId())
                .fileUrl(homework.getAttachmentUrl())
                .fileName(homework.getObjectName() != null ? homework.getObjectName().substring(homework.getObjectName().lastIndexOf('/') + 1) : null)
                .endTime(homework.getDeadline() != null ? homework.getDeadline().format(DATE_TIME_FORMATTER) : null)
                .createdAt(homework.getCreatedAt())
                .updatedAt(homework.getUpdatedAt())
                .isActive(homework.getIsActive())
                .build();
    }

    @Override
    public List<HomeworkDTO> getHomeworkList(Long classId) {
        List<Homework> homeworkList = homeworkMapper.selectByClassId(classId);
        return homeworkList.stream().map(homework -> HomeworkDTO.builder()
                .id(homework.getId())
                .title(homework.getTitle())
                .description(homework.getDescription())
                .classId(homework.getClassId())
                .fileUrl(homework.getAttachmentUrl())
                .fileName(homework.getObjectName() != null ? homework.getObjectName().substring(homework.getObjectName().lastIndexOf('/') + 1) : null)
                .endTime(homework.getDeadline() != null ? homework.getDeadline().format(DATE_TIME_FORMATTER) : null)
                .createdAt(homework.getCreatedAt())
                .updatedAt(homework.getUpdatedAt())
                .isActive(homework.getIsActive())
                .build()
        ).collect(Collectors.toList());
    }

    @Override
    @Transactional
    public Long submitHomework(Long homeworkId, Long studentId, String studentName, MultipartFile file) {
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
        if (homework.getDeadline() != null && LocalDateTime.now().isAfter(homework.getDeadline())) {
            throw new RuntimeException("作业已过截止时间");
        }

        // 创建或更新提交记录
        HomeworkSubmission submission = new HomeworkSubmission();
        submission.setHomeworkId(homeworkId);
        submission.setStudentId(studentId);
        submission.setStudentName(studentName);

        // 上传文件
        String objectName = "homework/submission/" + homeworkId + "/" + studentId + "_" + file.getOriginalFilename();
        try {
            Result<?> uploadResult = fileUpload.uploadFile(file, file.getOriginalFilename(), 
                null, null, "private", studentId, FileType.HOMEWORK_SUBMISSION.name());
            if (uploadResult.isSuccess()) {
                submission.setObjectName(objectName);
                FileAccessDTO accessDTO = fileAccessService.getDownloadUrlByObjectName(objectName);
                submission.setFileUrl(accessDTO.getUrl());
            } else {
                throw new RuntimeException("上传提交文件失败");
            }
        } catch (JsonProcessingException e) {
            throw new RuntimeException("上传提交文件失败", e);
        }
        submission.setSubmittedAt(LocalDateTime.now());

        // 保存提交记录
        submissionMapper.insert(submission);
        return submission.getId();
    }

    @Override
    public List<HomeworkSubmissionDTO> getSubmissionList(Long homeworkId) {
        List<HomeworkSubmission> submissions = submissionMapper.selectByHomeworkId(homeworkId);
        return submissions.stream().map(submission -> HomeworkSubmissionDTO.builder()
                .submissionId(submission.getId())
                .studentId(submission.getStudentId().toString())
                .studentName(submission.getStudentName())
                .fileUrl(submission.getFileUrl())
                .fileName(submission.getObjectName() != null ? submission.getObjectName().substring(submission.getObjectName().lastIndexOf('/') + 1) : null)
                .submitTime(submission.getSubmittedAt().format(DATE_TIME_FORMATTER))
                .build()
        ).collect(Collectors.toList());
    }

    @Override
    public HomeworkSubmissionDTO getStudentSubmission(Long homeworkId, Long studentId) {
        HomeworkSubmission submission = submissionMapper.selectByHomeworkAndStudent(homeworkId, studentId);
        if (submission == null) {
            return null;
        }
        
        return HomeworkSubmissionDTO.builder()
                .submissionId(submission.getId())
                .studentId(submission.getStudentId().toString())
                .studentName(submission.getStudentName())
                .fileUrl(submission.getFileUrl())
                .fileName(submission.getObjectName() != null ? submission.getObjectName().substring(submission.getObjectName().lastIndexOf('/') + 1) : null)
                .submitTime(submission.getSubmittedAt().format(DATE_TIME_FORMATTER))
                .build();
    }

    @Override
    public void downloadHomeworkFile(Long homeworkId, HttpServletResponse response) {
        try {
            Homework homework = homeworkMapper.selectById(homeworkId);
            if (homework == null || homework.getObjectName() == null) {
                throw new RuntimeException("作业或附件不存在");
            }
            
            FileAccessDTO accessDTO = fileAccessService.getDownloadUrlByObjectName(homework.getObjectName());
            response.sendRedirect(accessDTO.getUrl());
        } catch (IOException e) {
            throw new RuntimeException("下载作业文件失败: " + e.getMessage(), e);
        }
    }

    @Override
    public void downloadSubmissionFile(Long submissionId, HttpServletResponse response) {
        try {
            HomeworkSubmission submission = submissionMapper.selectById(submissionId);
            if (submission == null || submission.getObjectName() == null) {
                throw new RuntimeException("提交记录或附件不存在");
            }
            
            FileAccessDTO accessDTO = fileAccessService.getDownloadUrlByObjectName(submission.getObjectName());
            response.sendRedirect(accessDTO.getUrl());
        } catch (IOException e) {
            throw new RuntimeException("下载提交文件失败: " + e.getMessage(), e);
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


}
