package org.example.edusoft.learning.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("self_submission")
public class SelfSubmission {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long selfPracticeId;
    private Long studentId;
    private LocalDateTime submittedAt;
    private Integer score;
    private Boolean isJudged;
    private String feedback;
}
