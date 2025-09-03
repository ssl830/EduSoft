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
import org.example.edusoft.content.client.CourseClient;
import java.io.IOException;
import java.time.LocalDate;
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
    private final CourseClient courseClient;

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
                .homeworkId(homework.getId())
                .class_id(homework.getClassId())
                .title(homework.getTitle())
                .description(homework.getDescription())
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
                .homeworkId(homework.getId())
                .class_id(homework.getClassId())
                .title(homework.getTitle())
                .description(homework.getDescription())
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
        log.info("开始处理作业提交：homeworkId={}, studentId={}, studentName={}, fileName={}, fileSize={}", 
            homeworkId, studentId, studentName, file.getOriginalFilename(), file.getSize());
        
        // 参数校验
        if (file == null || file.isEmpty()) {
            log.error("提交的文件为空");
            throw new RuntimeException("提交的文件不能为空");
        }

        // 检查作业是否存在
        Homework homework = homeworkMapper.selectById(homeworkId);
        if (homework == null) {
            log.error("作业不存在：homeworkId={}", homeworkId);
            throw new RuntimeException("作业不存在");
        }
        log.info("找到作业：{}", homework);

        // 检查是否已过截止时间
        if (homework.getDeadline() != null && LocalDateTime.now().isAfter(homework.getDeadline())) {
            log.error("作业已过截止时间：deadline={}, current={}", 
                homework.getDeadline(), LocalDateTime.now());
            throw new RuntimeException("作业已过截止时间");
        }

        // 获取课程ID
        Long classId = homework.getClassId();
        log.info("开始获取课程ID：作业ID={}, 班级ID={}", homeworkId, classId);
        Long courseId;
        try {
            courseId = courseClient.getCourseIdByClassId(classId);
            if (courseId == null) {
                log.error("获取课程ID失败：班级不存在或未关联到课程：classId={}", classId);
                throw new RuntimeException("获取课程ID失败：班级不存在或未关联到课程");
            }
            log.info("成功获取到课程ID：courseId={}, classId={}", courseId, classId);
        } catch (Exception e) {
            log.error("调用课程服务获取课程ID时发生错误：classId={}, error={}", classId, e.getMessage());
            throw new RuntimeException("获取课程ID失败：" + e.getMessage());
        }

        // 创建或更新提交记录
        HomeworkSubmission submission = new HomeworkSubmission();
        submission.setHomeworkId(homeworkId);
        submission.setStudentId(studentId);
        submission.setStudentName(studentName);

        // 上传文件
        String objectName = "homework/submission/" + homeworkId + "/" + studentId + "_" + file.getOriginalFilename();
        try {
            String title = file.getOriginalFilename();
            String type = FileType.HOMEWORK_SUBMISSION.name();
            log.info("开始上传提交文件：objectName={}, fileSize={}, fileName={}, courseId={}, classId={}, type={}", 
                    objectName, file.getSize(), title, courseId, homework.getClassId(), type);
            
            // 重要：提交的作业文件设置为班级可见
            Result<?> uploadResult = fileUpload.uploadFile(
                file,          // 文件
                title,         // 文件名
                courseId,      // 课程ID
                null,          // 章节ID，作业提交不需要
                "CLASS_ONLY",  // 可见性：仅班级可见
                studentId,     // 上传者ID
                type          // 文件类型
            );
            log.info("文件上传服务返回结果：{}", uploadResult);
            log.info("文件上传结果：{}", uploadResult);
            if (uploadResult.isSuccess()) {
                submission.setObjectName(objectName);
                FileAccessDTO accessDTO = fileAccessService.getDownloadUrlByObjectName(objectName);
                submission.setFileUrl(accessDTO.getUrl());
                log.info("获取到文件访问地址：{}", accessDTO.getUrl());
            } else {
                throw new RuntimeException("上传提交文件失败");
            }
        } catch (JsonProcessingException e) {
            throw new RuntimeException("上传提交文件失败", e);
        }
        submission.setSubmittedAt(LocalDateTime.now());

        // 保存提交记录
        log.info("开始保存提交记录：{}", submission);
        submissionMapper.insert(submission);
        log.info("提交记录保存成功，ID：{}", submission.getId());
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
    public int getHomeworkCountByCourse(Long courseId) {
        try {
            // 从course-service获取该课程下的所有班级ID
            List<Long> classIds = courseClient.getAllClassIdsByCourseId(courseId);
            if (classIds.isEmpty()) {
                return 0;
            }
            // 根据班级ID列表统计作业总数
            return homeworkMapper.countByClassIds(classIds);
        } catch (Exception e) {
            log.error("获取课程作业总数失败: courseId={}, error={}", courseId, e.getMessage());
            return 0;
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
    public int countTeacherCreateHomework(LocalDate start, LocalDate end, List<Long> teacherIds) {
        try {
            return homeworkMapper.countTeacherCreateHomework(start, end, teacherIds);
        } catch (Exception e) {
            log.error("统计教师创建作业数量失败: error={}", e.getMessage());
            return 0;
        }
    }

    @Override
    public int countStudentSubmitHomework(LocalDate start, LocalDate end, List<Long> studentIds) {
        try {
            return submissionMapper.countStudentSubmitHomework(start, end, studentIds);
        } catch (Exception e) {
            log.error("统计学生提交作业数量失败: error={}", e.getMessage());
            return 0;
        }
    }

}
