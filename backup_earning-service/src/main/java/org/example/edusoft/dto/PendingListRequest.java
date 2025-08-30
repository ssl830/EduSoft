package org.example.edusoft.learning.dto.practice;

import lombok.Data;

/**
 * 获取待批改列表的请求
 */
@Data
public class PendingListRequest {
    private Long practiceId;  // 练习ID（可选）
    private Long classId;     // 班级ID（必需）
}
