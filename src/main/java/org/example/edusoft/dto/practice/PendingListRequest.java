package org.example.edusoft.dto.practice;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

/**
 * 获取待批改列表的请求
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PendingListRequest {
    private Long practiceId;  // 练习ID（可选）
    private Long classId;     // 班级ID（必需）
} 