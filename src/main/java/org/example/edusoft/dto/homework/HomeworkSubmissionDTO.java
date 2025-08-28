package org.example.edusoft.dto.homework;

import lombok.Data;
import lombok.Builder;

/**
 * 作业提交数据传输对象
 */
@Data
@Builder
public class HomeworkSubmissionDTO {
    /**
     * 提交记录ID
     */
    private Long submissionId;

    /**
     * 学生ID
     */
    private String studentId;

    /**
     * 学生姓名
     */
    private String studentName;

    /**
     * 带签名的文件访问URL
     */
    private String fileUrl;

    /**
     * 文件名
     */
    private String fileName;

    /**
     * 提交时间
     */
    private String submitTime;
} 