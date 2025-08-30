package org.example.edusoft.content.dto.homework;

import lombok.Data;

/**
 * 创建作业请求DTO
 */
@Data
public class CreateHomeworkRequest {
    private Long classId;
    private String title;
    private String description;
    private String endTime;
}
