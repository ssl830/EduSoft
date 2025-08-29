package org.example.edusoft.learning.service;

import org.example.edusoft.learning.dto.homework.HomeworkDTO;
import org.example.edusoft.learning.dto.homework.HomeworkSubmissionDTO;
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
    List<HomeworkDTO> getHomeworkByClass(Long classId);
    
    /**
     * 获取课程作业列表
     * @param courseId 课程ID
     * @return 作业列表
     */
    List<HomeworkDTO> getHomeworkByCourse(Long courseId);

    /**
     * 更新作业
     * @param id 作业ID
     * @param title 标题
     * @param description 描述
     * @param endTime 截止时间
     * @param file 附件
     * @return 是否成功
     */
    void updateHomework(Long id, String title, String description, String endTime, MultipartFile file);
    
    /**
     * 提交作业
     * @param homeworkId 作业ID
     * @param content 内容
     * @param files 提交的文件列表
     * @return 提交记录ID
     */
    Long submitHomework(Long homeworkId, String content, List<MultipartFile> files);

    /**
     * 获取作业提交列表
     * @param homeworkId 作业ID
     * @return 提交记录列表
     */
    List<HomeworkSubmission> getAllSubmissions(Long homeworkId);

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
    void downloadAttachment(Long homeworkId, HttpServletResponse response);

    /**
     * 下载提交的作业文件
     * @param submissionId 提交记录ID
     * @param fileIndex 文件索引
     * @param response HTTP响应对象
     */
    void downloadSubmissionFile(Long submissionId, Integer fileIndex, HttpServletResponse response);

    /**
     * 评分
     * @param submissionId 提交ID
     * @param score 分数
     * @param feedback 反馈
     */
    void gradeSubmission(Long submissionId, Integer score, String feedback);

    /**
     * 删除作业
     * @param homeworkId 作业ID
     */
    void deleteHomework(Long homeworkId);
}
