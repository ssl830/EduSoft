package org.example.edusoft.learning.service.impl;

import lombok.RequiredArgsConstructor;
import org.example.edusoft.common.exception.BusinessException;
import org.example.edusoft.learning.dto.HomeworkDTO;
import org.example.edusoft.learning.dto.HomeworkSubmissionDTO;
import org.example.edusoft.learning.entity.Homework;
import org.example.edusoft.learning.entity.HomeworkSubmission;
import org.example.edusoft.learning.mapper.HomeworkMapper;
import org.example.edusoft.learning.mapper.HomeworkSubmissionMapper;
import org.example.edusoft.learning.service.HomeworkService;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class HomeworkServiceImpl implements HomeworkService {

    private final HomeworkMapper homeworkMapper;
    private final HomeworkSubmissionMapper homeworkSubmissionMapper;
    // private final FileService fileService; // 假设有一个文件服务

    @Override
    public Long createHomework(Long classId, String title, String description, String endTime, MultipartFile file, Long createdBy) {
        Homework homework = new Homework();
        homework.setClassId(classId);
        homework.setTitle(title);
        homework.setDescription(description);
        homework.setCreatedBy(createdBy);
        homework.setCreatedAt(LocalDateTime.now());
        homework.setUpdatedAt(LocalDateTime.now());

        if (endTime != null && !endTime.isEmpty()) {
            homework.setDeadline(LocalDateTime.parse(endTime, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
        }

        if (file != null && !file.isEmpty()) {
            // String objectName = "homework/" + classId + "/" + file.getOriginalFilename();
            // String fileUrl = fileService.uploadFile(file, objectName);
            // homework.setAttachmentUrl(fileUrl);
            // homework.setObjectName(objectName);
        }

        homeworkMapper.insert(homework);
        return homework.getId();
    }

    @Override
    public HomeworkDTO getHomework(Long id) {
        Homework homework = homeworkMapper.selectById(id);
        if (homework == null) {
            throw new BusinessException("作业不存在");
        }
        return convertToDto(homework);
    }

    @Override
    public List<HomeworkDTO> getHomeworkList(Long classId) {
        return homeworkMapper.selectByClassId(classId).stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    @Override
    public Long submitHomework(Long homeworkId, Long studentId, MultipartFile file) {
        Homework homework = homeworkMapper.selectById(homeworkId);
        if (homework == null) {
            throw new BusinessException("作业不存在");
        }
        if (homework.getDeadline() != null && LocalDateTime.now().isAfter(homework.getDeadline())) {
            throw new BusinessException("已超过作业截止日期");
        }

        HomeworkSubmission existingSubmission = homeworkSubmissionMapper.selectByHomeworkAndStudent(homeworkId, studentId);
        if (existingSubmission != null) {
            // 如果允许重复提交，则更新，否则抛出异常
            // 此处简单处理为不允许重复提交
            throw new BusinessException("你已经提交过该作业");
        }

        HomeworkSubmission submission = new HomeworkSubmission();
        submission.setHomeworkId(homeworkId);
        submission.setStudentId(studentId);
        submission.setSubmittedAt(LocalDateTime.now());

        if (file != null && !file.isEmpty()) {
            // String objectName = "homework/submission/" + homeworkId + "/" + studentId + "_" + file.getOriginalFilename();
            // String fileUrl = fileService.uploadFile(file, objectName);
            // submission.setFileUrl(fileUrl);
            // submission.setObjectName(objectName);
        }

        homeworkSubmissionMapper.insert(submission);
        return submission.getId();
    }

    @Override
    public List<HomeworkSubmissionDTO> getSubmissionList(Long homeworkId) {
        return homeworkSubmissionMapper.selectByHomeworkId(homeworkId).stream()
                .map(this::convertSubmissionToDto)
                .collect(Collectors.toList());
    }

    @Override
    public HomeworkSubmissionDTO getStudentSubmission(Long homeworkId, Long studentId) {
        HomeworkSubmission submission = homeworkSubmissionMapper.selectByHomeworkAndStudent(homeworkId, studentId);
        return submission != null ? convertSubmissionToDto(submission) : null;
    }

    @Override
    public void downloadHomeworkFile(Long homeworkId, HttpServletResponse response) {
        Homework homework = homeworkMapper.selectById(homeworkId);
        if (homework == null || homework.getObjectName() == null) {
            throw new BusinessException("作业或附件不存在");
        }
        // fileService.downloadFile(homework.getObjectName(), response);
    }

    @Override
    public void downloadSubmissionFile(Long submissionId, HttpServletResponse response) {
        HomeworkSubmission submission = homeworkSubmissionMapper.selectById(submissionId);
        if (submission == null || submission.getObjectName() == null) {
            throw new BusinessException("提交记录或文件不存在");
        }
        // fileService.downloadFile(submission.getObjectName(), response);
    }

    @Override
    public void deleteHomework(Long homeworkId) {
        // 1. 删除OSS上的所有提交文件
        List<HomeworkSubmission> submissions = homeworkSubmissionMapper.selectByHomeworkId(homeworkId);
        for (HomeworkSubmission submission : submissions) {
            if (submission.getObjectName() != null) {
                // fileService.deleteFile(submission.getObjectName());
            }
        }
        // 2. 删除数据库中的所有提交记录
        homeworkSubmissionMapper.deleteByHomeworkId(homeworkId);

        // 3. 删除OSS上的作业附件
        Homework homework = homeworkMapper.selectById(homeworkId);
        if (homework != null && homework.getObjectName() != null) {
            // fileService.deleteFile(homework.getObjectName());
        }

        // 4. 删除数据库中的作业记录
        homeworkMapper.deleteById(homeworkId);
    }

    private HomeworkDTO convertToDto(Homework homework) {
        String fileUrl = null;
        String fileName = null;
        if (homework.getObjectName() != null) {
            // fileUrl = fileService.getSignedUrl(homework.getObjectName());
            fileName = homework.getObjectName().substring(homework.getObjectName().lastIndexOf('/') + 1);
        }

        return HomeworkDTO.builder()
                .homeworkId(homework.getId())
                .title(homework.getTitle())
                .description(homework.getDescription())
                .fileUrl(fileUrl)
                .fileName(fileName)
                .endTime(homework.getDeadline() != null ? homework.getDeadline().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")) : null)
                .build();
    }

    private HomeworkSubmissionDTO convertSubmissionToDto(HomeworkSubmission submission) {
        String fileUrl = null;
        String fileName = null;
        if (submission.getObjectName() != null) {
            // fileUrl = fileService.getSignedUrl(submission.getObjectName());
            fileName = submission.getObjectName().substring(submission.getObjectName().lastIndexOf('/') + 1);
        }

        return HomeworkSubmissionDTO.builder()
                .submissionId(submission.getId())
                .studentId(submission.getStudentId().toString())
                .studentName(submission.getStudentName())
                .fileUrl(fileUrl)
                .fileName(fileName)
                .submitTime(submission.getSubmittedAt().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")))
                .build();
    }
}
