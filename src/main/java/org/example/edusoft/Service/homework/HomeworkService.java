package org.example.edusoft.service.homework;

import org.example.edusoft.dto.homework.HomeworkDTO;
import org.example.edusoft.dto.homework.HomeworkSubmissionDTO;
import org.springframework.web.multipart.MultipartFile;
import jakarta.servlet.http.HttpServletResponse;
import java.util.List;

/**
 * 作业服务接口
 */
public interface HomeworkService {
    /**
     * 创建作业
     * @param classId 班级ID
     * @param title 作业标题
     * @param description 作业描述
     * @param endTime 截止时间（格式：yyyy-MM-dd HH:mm:ss）
     * @param file 附件文件
     * @param createdBy 创建者ID
     * @return 作业ID
     */
    Long createHomework(Long classId, String title, String description, 
                       String endTime, MultipartFile file);

    /**
     * 获取作业详情
     * @param id 作业ID
     * @return 作业信息
     */
    HomeworkDTO getHomework(Long id);

    /**
     * 获取班级作业列表
     * @param classId 班级ID
     * @return 作业列表
     */
    List<HomeworkDTO> getHomeworkList(Long classId);

    /**
     * 提交作业
     * @param homeworkId 作业ID
     * @param studentId 学生ID
     * @param file 提交的文件
     * @return 提交记录ID
     */
    Long submitHomework(Long homeworkId, Long studentId, MultipartFile file);

    /**
     * 获取作业提交列表
     * @param homeworkId 作业ID
     * @return 提交记录列表
     */
    List<HomeworkSubmissionDTO> getSubmissionList(Long homeworkId);

    /**
     * 获取学生的提交记录
     * @param homeworkId 作业ID
     * @param studentId 学生ID
     * @return 提交记录
     */
    HomeworkSubmissionDTO getStudentSubmission(Long homeworkId, Long studentId);

    /**
     * 下载作业附件
     * @param homeworkId 作业ID
     * @param response HTTP响应对象
     */
    void downloadHomeworkFile(Long homeworkId, HttpServletResponse response);

    /**
     * 下载提交的作业文件
     * @param submissionId 提交记录ID
     * @param response HTTP响应对象
     */
    void downloadSubmissionFile(Long submissionId, HttpServletResponse response);

    /**
     * 删除作业
     * @param homeworkId 作业ID
     */
    void deleteHomework(Long homeworkId);
} 