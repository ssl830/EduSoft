package org.example.edusoft.entity.practice;

import lombok.Data;

// JudgeRequest.java（教师批改请求）
@Data
public class JudgeRequest {
    private Long answerId;
    private Integer score;
}