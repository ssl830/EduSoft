package org.example.edusoft.entity.selfpractice;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("SelfSubmission")
public class SelfSubmission {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long selfPracticeId;
    private Long studentId;
    private LocalDateTime submittedAt;
    private Integer score;
    private Boolean isJudged;
} 