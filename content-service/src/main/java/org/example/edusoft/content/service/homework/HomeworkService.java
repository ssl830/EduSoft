package org.example.edusoft.content.service.homework;

import org.example.edusoft.content.dto.homework.HomeworkDTO;
import org.example.edusoft.content.dto.homework.HomeworkSubmissionDTO;
import org.springframework.web.multipart.MultipartFile;
import jakarta.servlet.http.HttpServletResponse;
import java.util.List;

public interface HomeworkService {
    
    /**
     * 创建作业
     */
    Long createHomework(Long classId, String title, String description, String endTime, MultipartFile file);
    
    /**
     * 获取作业详情
     */
    HomeworkDTO getHomework(Long id);
    
    /**
     * 获取班级作业列表
     */
    List<HomeworkDTO> getHomeworkList(Long classId);
    
    /**
     * 提交作业
     */
    Long submitHomework(Long homeworkId, Long studentId, String content, MultipartFile file);
    
    /**
     * 获取作业提交列表
     */
    List<HomeworkSubmissionDTO> getSubmissionList(Long homeworkId);
    
    /**
     * 获取学生提交的作业
     */
    HomeworkSubmissionDTO getStudentSubmission(Long homeworkId, Long studentId);
    
    /**
     * 批改作业
     */
    void gradeHomework(Long submissionId, String feedback, Integer score);
    
    /**
     * 导出作业提交列表
     */
    void exportSubmissions(Long homeworkId, HttpServletResponse response);
    
    /**
     * 下载作业附件
     */
    void downloadHomeworkFile(Long homeworkId, HttpServletResponse response);
    
    /**
     * 下载提交的作业文件
     */
    void downloadSubmissionFile(Long submissionId, HttpServletResponse response);
    
    /**
     * 删除作业
     */
    void deleteHomework(Long homeworkId);
    
    /**
     * 根据课程ID统计作业总数
     */
    int getHomeworkCountByCourse(Long courseId);
} 