package org.example.edusoft.service.homework;

import org.example.edusoft.common.domain.FileBo;
import org.example.edusoft.common.storage.IFileStorage;
import org.example.edusoft.common.storage.IFileStorageProvider;
import org.example.edusoft.entity.file.FileType;
import org.example.edusoft.entity.homework.Homework;
import org.example.edusoft.entity.homework.HomeworkSubmission;
import org.example.edusoft.mapper.homework.HomeworkMapper;
import org.example.edusoft.mapper.homework.HomeworkSubmissionMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import jakarta.servlet.http.HttpServletResponse;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 作业服务实现类
 */
@Service
public class HomeworkServiceImpl implements HomeworkService {

    @Autowired
    private HomeworkMapper homeworkMapper;

    @Autowired
    private HomeworkSubmissionMapper submissionMapper;

    @Autowired
    private IFileStorageProvider storageProvider;

    @Override
    @Transactional
    public Long createHomework(Homework homework, MultipartFile attachmentFile) {
        // 设置创建时间和更新时间
        LocalDateTime now = LocalDateTime.now();
        homework.setCreatedAt(now);
        homework.setUpdatedAt(now);

        // 上传附件到OSS
        if (attachmentFile != null && !attachmentFile.isEmpty()) {
            String uniqueName = String.format("homework/%d/%s", 
                homework.getClassId(), 
                attachmentFile.getOriginalFilename());
            
            IFileStorage storage = storageProvider.getStorage();
            FileBo fileBo = storage.upload(attachmentFile, uniqueName, FileType.PDF);
            
            homework.setObjectName(fileBo.getFileName());
            homework.setAttachmentUrl(fileBo.getUrl());
        }

        // 保存作业信息
        homeworkMapper.insert(homework);
        return homework.getId();
    }

    @Override
    public Homework getHomework(Long id) {
        return homeworkMapper.selectById(id);
    }

    @Override
    public List<Homework> getHomeworkList(Long classId) {
        return homeworkMapper.selectByClassId(classId);
    }

    @Override
    @Transactional
    public Long submitHomework(Long homeworkId, Long studentId, String submissionType, MultipartFile file) {
        // 检查作业是否存在
        Homework homework = homeworkMapper.selectById(homeworkId);
        if (homework == null) {
            throw new RuntimeException("作业不存在");
        }

        // 检查是否已过截止时间
        if (homework.getDeadline() != null && LocalDateTime.now().isAfter(homework.getDeadline())) {
            throw new RuntimeException("作业已过截止时间");
        }

        // 上传文件到OSS
        String uniqueName = String.format("hwsubmit/%d/%d_%s", 
            homeworkId, 
            studentId,
            file.getOriginalFilename());
        
        IFileStorage storage = storageProvider.getStorage();
        FileBo fileBo = storage.upload(file, uniqueName, FileType.PDF);

        // 创建提交记录
        HomeworkSubmission submission = new HomeworkSubmission();
        submission.setHomeworkId(homeworkId);
        submission.setStudentId(studentId);
        submission.setSubmissionType(submissionType);
        submission.setObjectName(fileBo.getFileName());
        submission.setFileUrl(fileBo.getUrl());
        submission.setSubmittedAt(LocalDateTime.now());

        // 保存提交记录
        submissionMapper.insert(submission);
        return submission.getId();
    }

    @Override
    public List<HomeworkSubmission> getSubmissionList(Long homeworkId) {
        return submissionMapper.selectByHomeworkId(homeworkId);
    }

    @Override
    public HomeworkSubmission getStudentSubmission(Long homeworkId, Long studentId) {
        return submissionMapper.selectByHomeworkAndStudent(homeworkId, studentId);
    }

    @Override
    public void downloadHomeworkFile(Long homeworkId, HttpServletResponse response) {
        Homework homework = homeworkMapper.selectById(homeworkId);
        if (homework == null || homework.getObjectName() == null) {
            throw new RuntimeException("作业附件不存在");
        }
        
        IFileStorage storage = storageProvider.getStorage();
        storage.download(homework.getObjectName(), response);
    }

    @Override
    public void downloadSubmissionFile(Long submissionId, HttpServletResponse response) {
        HomeworkSubmission submission = submissionMapper.selectById(submissionId);
        if (submission == null || submission.getObjectName() == null) {
            throw new RuntimeException("提交文件不存在");
        }
        
        IFileStorage storage = storageProvider.getStorage();
        storage.download(submission.getObjectName(), response);
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
            submissionMapper.deleteById(submission.getId());
        }

        // 删除作业记录
        homeworkMapper.deleteById(homeworkId);
    }
} 