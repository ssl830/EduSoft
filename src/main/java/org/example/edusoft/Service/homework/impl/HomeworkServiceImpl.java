package org.example.edusoft.service.homework.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.edusoft.common.domain.FileBo;
import org.example.edusoft.common.storage.IFileStorage;
import org.example.edusoft.common.storage.IFileStorageProvider;
import org.example.edusoft.dto.homework.HomeworkDTO;
import org.example.edusoft.dto.homework.HomeworkSubmissionDTO;
import org.example.edusoft.entity.file.FileType;
import org.example.edusoft.entity.homework.Homework;
import org.example.edusoft.entity.homework.HomeworkSubmission;
import org.example.edusoft.exception.BusinessException;
import org.example.edusoft.mapper.homework.HomeworkMapper;
import org.example.edusoft.mapper.homework.HomeworkSubmissionMapper;
import org.example.edusoft.service.file.FileAccessService;
import org.example.edusoft.service.homework.HomeworkService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import jakarta.servlet.http.HttpServletResponse;

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
    private final IFileStorageProvider storageProvider;
    private final FileAccessService fileAccessService;

    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    @Override
    @Transactional
    public Long createHomework(Long classId, String title, String description,
                             String endTime, MultipartFile file) {
        // 参数校验
        if (classId == null || title == null || title.trim().isEmpty()) {
            throw new BusinessException("班级ID和作业标题不能为空");
        }

        // 创建作业实体
        Homework homework = new Homework();
        homework.setClassId(classId);
        homework.setTitle(title);
        homework.setDescription(description);
        homework.setCreatedBy(1L);

        // 设置截止时间
        if (endTime != null && !endTime.trim().isEmpty()) {
            homework.setDeadline(LocalDateTime.parse(endTime, DATE_TIME_FORMATTER));
        }

        // 设置创建时间和更新时间
        LocalDateTime now = LocalDateTime.now();
        homework.setCreatedAt(now);
        homework.setUpdatedAt(now);

        // 上传附件（如果有）
        if (file != null && !file.isEmpty()) {
            String uniqueName = String.format("homework/%d/%s", classId, file.getOriginalFilename());
            IFileStorage storage = storageProvider.getStorage();
            FileBo fileBo = storage.upload(file, uniqueName, FileType.PDF);

            homework.setObjectName(fileBo.getFileName());
            homework.setAttachmentUrl(fileBo.getUrl());
        }

        // 保存作业信息
        homeworkMapper.insert(homework);
        return homework.getId();
    }

    @Override
    @Transactional
    public Long submitHomework(Long homeworkId, Long studentId, MultipartFile file) {
        // 参数校验
        if (file == null || file.isEmpty()) {
            throw new BusinessException("提交的文件不能为空");
        }

        // 检查作业是否存在
        Homework homework = homeworkMapper.selectById(homeworkId);
        if (homework == null) {
            throw new BusinessException("作业不存在");
        }

        // 检查是否已过截止时间
        if (homework.getDeadline() != null && LocalDateTime.now().isAfter(homework.getDeadline())) {
            throw new BusinessException("作业已过截止时间");
        }

        // 上传文件
        String uniqueName = String.format("homework/submission/%d/%d_%s",
            homeworkId, studentId, file.getOriginalFilename());

        IFileStorage storage = storageProvider.getStorage();
        FileBo fileBo = storage.upload(file, uniqueName, FileType.PDF);

        // 创建提交记录
        HomeworkSubmission submission = new HomeworkSubmission();
        submission.setHomeworkId(homeworkId);
        submission.setStudentId(studentId);
        submission.setObjectName(fileBo.getFileName());
        submission.setFileUrl(fileBo.getUrl());
        submission.setSubmittedAt(LocalDateTime.now());
        submission.setFileUrl(fileBo.getUrl());
        System.out.println("fileBo.getFileName(): " + fileBo.getFileName());
        // 保存提交记录
        submissionMapper.insert(submission);
        return submission.getId();
    }

    @Override
    public List<HomeworkDTO> getHomeworkList(Long classId) {
        List<Homework> homeworkList = homeworkMapper.selectByClassId(classId);
        System.out.println("homeworkList: " + homeworkList);
        return homeworkList.stream().map(homework -> {
            String fileUrl = homework.getObjectName() != null ?
                fileAccessService.getDownloadUrlByObjectName(homework.getObjectName()).getUrl() : null;

            return HomeworkDTO.builder()
                .homeworkId(homework.getId())
                .title(homework.getTitle())
                .description(homework.getDescription())
                .fileUrl(fileUrl)
                .fileName(homework.getObjectName())
                .endTime(homework.getDeadline() != null ?
                    homework.getDeadline().format(DATE_TIME_FORMATTER) : null)
                .build();
        }).collect(Collectors.toList());
    }

    @Override
    public List<HomeworkSubmissionDTO> getSubmissionList(Long homeworkId) {
        List<HomeworkSubmission> submissions = submissionMapper.selectByHomeworkId(homeworkId);

        return submissions.stream().map(submission -> {
            String fileUrl = submission.getObjectName() != null ?
                fileAccessService.getDownloadUrlByObjectName(submission.getObjectName()).getUrl() : null;

            return HomeworkSubmissionDTO.builder()
                .submissionId(submission.getId())
                .studentId(String.valueOf(submission.getStudentId()))
                .studentName(submission.getStudentName())
                .fileUrl(fileUrl)
                .fileName(submission.getObjectName())
                .submitTime(submission.getSubmittedAt().format(DATE_TIME_FORMATTER))
                .build();
        }).collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void deleteHomework(Long homeworkId) {
        // 获取作业信息
        Homework homework = homeworkMapper.selectById(homeworkId);
        if (homework == null) {
            return;
        }

        // 删除作业附件
        if (homework.getObjectName() != null) {
            IFileStorage storage = storageProvider.getStorage();
            storage.delete(homework.getObjectName());
        }

        // 获取所有提交记录
        List<HomeworkSubmission> submissions = submissionMapper.selectByHomeworkId(homeworkId);

        // 删除所有提交的文件
        IFileStorage storage = storageProvider.getStorage();
        for (HomeworkSubmission submission : submissions) {
            if (submission.getObjectName() != null) {
                storage.delete(submission.getObjectName());
            }
        }

        // 删除所有提交记录
        submissionMapper.deleteByHomeworkId(homeworkId);

        // 删除作业记录
        homeworkMapper.deleteById(homeworkId);
    }

    @Override
    public HomeworkDTO getHomework(Long id) {
        Homework homework = homeworkMapper.selectById(id);
        if (homework == null) {
            return null;
        }

        String fileUrl = homework.getObjectName() != null ?
            fileAccessService.getDownloadUrl(homework.getId()).getUrl() : null;

        return HomeworkDTO.builder()
            .homeworkId(homework.getId())
            .title(homework.getTitle())
            .description(homework.getDescription())
            .fileUrl(fileUrl)
            .fileName(homework.getObjectName())
            .endTime(homework.getDeadline() != null ?
                homework.getDeadline().format(DATE_TIME_FORMATTER) : null)
            .build();
    }

    @Override
    public HomeworkSubmissionDTO getStudentSubmission(Long homeworkId, Long studentId) {
        HomeworkSubmission submission = submissionMapper.selectByHomeworkAndStudent(homeworkId, studentId);
        if (submission == null) {
            return null;
        }
//         system.out.println("submission: " + submission);
        String fileUrl = submission.getObjectName() != null ?
            fileAccessService.getDownloadUrlByObjectName(submission.getObjectName()).getUrl() : null;

        return HomeworkSubmissionDTO.builder()
            .submissionId(submission.getId())
            .studentId(String.valueOf(submission.getStudentId()))
            .fileUrl(fileUrl)
            .fileName(submission.getObjectName())
            .submitTime(submission.getSubmittedAt().format(DATE_TIME_FORMATTER))
            .build();
    }

    @Override
    public void downloadHomeworkFile(Long homeworkId, HttpServletResponse response) {
        Homework homework = homeworkMapper.selectById(homeworkId);
        if (homework == null || homework.getObjectName() == null) {
            throw new BusinessException("作业附件不存在");
        }

        IFileStorage storage = storageProvider.getStorage();
        storage.download(homework.getObjectName(), response);
    }

    @Override
    public void downloadSubmissionFile(Long submissionId, HttpServletResponse response) {
        HomeworkSubmission submission = submissionMapper.selectById(submissionId);
        if (submission == null || submission.getObjectName() == null) {
            throw new BusinessException("提交文件不存在");
        }

        IFileStorage storage = storageProvider.getStorage();
        storage.download(submission.getObjectName(), response);
    }
}
