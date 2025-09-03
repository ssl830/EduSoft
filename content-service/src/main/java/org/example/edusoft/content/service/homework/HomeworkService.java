package org.example.edusoft.content.service.homework;

import jakarta.servlet.http.HttpServletRequest;
import org.example.edusoft.content.dto.homework.HomeworkDTO;
import org.example.edusoft.content.dto.homework.HomeworkSubmissionDTO;
import org.springframework.web.multipart.MultipartFile;
import jakarta.servlet.http.HttpServletResponse;
import java.time.LocalDate;
import java.util.List;

public interface HomeworkService {
    
    /**
     * 创建作业
     */
    Long createHomework(Long classId, String title, String description, String endTime, MultipartFile file, Long createdBy);
    
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
    Long submitHomework(Long homeworkId, Long studentId, String studentName, MultipartFile file, HttpServletRequest request);
    
    /**
     * 获取作业提交列表
     */
    List<HomeworkSubmissionDTO> getSubmissionList(Long homeworkId, HttpServletRequest request);
    
    /**
     * 获取学生提交的作业
     */
    HomeworkSubmissionDTO getStudentSubmission(Long homeworkId, Long studentId);
    
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
     * 获取课程的作业总数
     * @param courseId 课程ID
     * @return 作业总数
     */
    int getHomeworkCountByCourse(Long courseId);

    /**
     * 统计教师创建的作业数量
     * @param start 开始日期
     * @param end 结束日期
     * @param teacherIds 教师ID列表
     * @return 作业数量
     */
    int countTeacherCreateHomework(LocalDate start, LocalDate end, List<Long> teacherIds);

    /**
     * 统计学生提交的作业数量
     * @param start 开始日期
     * @param end 结束日期
     * @param studentIds 学生ID列表
     * @return 提交数量
     */
    int countStudentSubmitHomework(LocalDate start, LocalDate end, List<Long> studentIds);
} 